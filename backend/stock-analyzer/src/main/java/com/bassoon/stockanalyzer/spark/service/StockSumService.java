package com.bassoon.stockanalyzer.spark.service;

import static org.apache.spark.sql.functions.expr;
import static scala.collection.JavaConversions.asScalaBuffer;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.bassoon.stockanalyzer.spark.config.SparkRepository;
import com.google.common.collect.Lists;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import org.springframework.beans.factory.annotation.Autowired;
import scala.collection.JavaConversions;
import scala.collection.Seq;

/**
 * StockSumService
 *
 * @author lwan
 * @since Apr 8, 2018, 9:48:46 AM
 */
public class StockSumService implements Serializable {

  @Autowired
  private SparkRepository sparkRepository;

  public StockSumService() {
    sparkRepository.registerUdaf("mySum", new MySum());
  }

  public void doSum() {
    doSum("");
  }

  public void doSum(int startYear, int endYear) {
    doSum(" WHERE year >= " + startYear + " AND year <= " + endYear);
  }

  private void doSum(String whereClause) {

    // Create temp views

    sparkRepository.createOrReplaceTempViewByTable("stock_basics", "basics_view");
    sparkRepository.createOrReplaceTempViewByTable("stock_profit_data", "roe_view");
    sparkRepository.createOrReplaceTempViewByTable("stock_cashflow_data", "cashflowratio_view");
    sparkRepository.createOrReplaceTempViewByTable("stock_debtpay_data", "currentratio_view");
    sparkRepository.createOrReplaceTempViewByTable("stock_growth_data", "epsg_view");
    sparkRepository.createOrReplaceTempViewByTable("stock_operation_data", "turnover_view");

    // Create initial datasets

    Dataset<Row> dsBasics = sparkRepository.getDatasetBySql(
        "SELECT code, name FROM basics_view" + whereClause);
    Dataset<Row> dsRoe = sparkRepository.getDatasetBySql(
        "SELECT code, name, roe, year, quarter FROM roe_view" + whereClause);
    Dataset<Row> dsCashflowRatio = sparkRepository.getDatasetBySql(
        "SELECT code, name, cashflowratio, year, quarter FROM cashflowratio_view" + whereClause);
    Dataset<Row> dsCurrentRatio = sparkRepository.getDatasetBySql(
        "SELECT code, name, currentratio, year, quarter FROM currentratio_view" + whereClause);
    Dataset<Row> dsEpsg = sparkRepository.getDatasetBySql(
        "SELECT code, name, epsg, year, quarter FROM epsg_view" + whereClause);
    Dataset<Row> dsTurnover = sparkRepository.getDatasetBySql(
        "SELECT code, name, currentasset_turnover, year, quarter FROM turnover_view" + whereClause);

    // Cleanup rows

    dsBasics = dsBasics.map(new MapFunction<Row, Row>() {
      @Override
      public Row call(Row row) throws Exception {
        String code = row.getString(0); // code
        String name = row.getString(1); // name
        name = name.replaceAll(" ", "").replaceAll("Ａ", "A");
        return RowFactory.create(code, name);
      }
    }, RowEncoder.apply(dsBasics.schema()));

    MapFunction<Row, Row> cleanupSumType = new MapFunction<Row, Row>() {
      @Override
      public Row call(Row row) throws Exception {
        String code = row.getString(0); // code
        String name = row.getString(1); // name
        name = name.replaceAll(" ", "").replaceAll("Ａ", "A");
        double sum = 0D;
        try {
          sum = row.getDouble(2); // value to sum
        } catch (NullPointerException npe) {}
        long year = 0L;
        try {
          year = row.getLong(3); // year
        } catch (NullPointerException npe) {}
        long quarter = 0L;
        try {
          quarter = row.getLong(4); // quarter
        } catch (NullPointerException npe) {}
        return RowFactory.create(code, name, sum, year, quarter);
      }
    };

    dsRoe = dsRoe.map(cleanupSumType, RowEncoder.apply(dsRoe.schema()));
    dsCashflowRatio = dsCashflowRatio.map(cleanupSumType, RowEncoder.apply(dsCashflowRatio.schema()));
    dsCurrentRatio = dsCurrentRatio.map(cleanupSumType, RowEncoder.apply(dsCurrentRatio.schema()));
    dsEpsg = dsEpsg.map(cleanupSumType, RowEncoder.apply(dsEpsg.schema()));
    dsTurnover = dsTurnover.map(cleanupSumType, RowEncoder.apply(dsTurnover.schema()));

    // Remove duplicate rows

    dsRoe = dsRoe.distinct();
    dsCashflowRatio = dsCashflowRatio.distinct();
    dsCurrentRatio = dsCurrentRatio.distinct();
    dsEpsg = dsEpsg.distinct();
    dsTurnover = dsTurnover.distinct();

    // Joinning datasets (full outer join)

    Seq<String> colCode = asScalaBuffer(Arrays.asList("code")).toSeq();
    Seq<String> colCodeYearQuarter = asScalaBuffer(Arrays.asList("code", "year", "quarter")).toSeq();

    Dataset<Row> joined = dsBasics.join(dsRoe, colCode, "fullouter");
    joined = joined.join(dsCashflowRatio, colCodeYearQuarter, "fullouter");
    joined = joined.join(dsCurrentRatio, colCodeYearQuarter, "fullouter");
    joined = joined.join(dsEpsg, colCodeYearQuarter, "fullouter");
    joined = joined.join(dsTurnover, colCodeYearQuarter, "fullouter");

    // Summing

    Dataset<Row> summed = joined
        .groupBy("code", "year")
        .agg(
            expr("mySum(roe)"), expr("mySum(cashflowratio)"), expr("mySum(currentratio)"),
            expr("mySum(epsg)"), expr("mySum(currentasset_turnover)")
        );

    // Return

    writeToDatabase(summed);
  }

  private void writeToDatabase(Dataset<Row> dataset) {
    throw new UnsupportedOperationException("To be implemented.");
  }

  public static class MySum extends UserDefinedAggregateFunction {

    private StructType inputSchema;
    private StructType bufferSchema;
    private DataType dataType;
    private boolean deterministic;

    public MySum() {
      inputSchema = DataTypes.createStructType(Arrays.asList(
          DataTypes.createStructField("inputColumn", DataTypes.DoubleType, true)
      ));
      bufferSchema = DataTypes.createStructType(Arrays.asList(
          DataTypes.createStructField("sum", DataTypes.DoubleType, true),
          DataTypes.createStructField("count", DataTypes.LongType, true)
      ));
      dataType = DataTypes.DoubleType;
      deterministic = true;
    }

    @Override
    public StructType inputSchema() {
      return inputSchema;
    }

    @Override
    public StructType bufferSchema() {
      return bufferSchema;
    }

    @Override
    public DataType dataType() {
      return dataType;
    }

    @Override
    public boolean deterministic() {
      return deterministic;
    }

    // initialize -> update -> merge -> evaluate

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
      buffer.update(0, 0D);
      buffer.update(1, 0L);
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
      if (!input.isNullAt(0)) {
        double updatedSum = buffer.getDouble(0) + input.getDouble(0);
        long updatedCount = buffer.getLong(1) + 1;
        buffer.update(0, updatedSum);
        buffer.update(1, updatedCount);
      }
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
      double mergedSum = buffer1.getDouble(0) + buffer2.getDouble(0);
      long mergedCount = buffer1.getLong(1) + buffer2.getLong(1);
      buffer1.update(0, mergedSum);
      buffer1.update(1, mergedCount);
    }

    @Override
    public Double evaluate(Row buffer) {
      double sum = buffer.getDouble(0);
      long count = buffer.getLong(1);
      if (count < 4) {
        return 0D;
      } else {
        return sum;
      }
    }

  }

}
