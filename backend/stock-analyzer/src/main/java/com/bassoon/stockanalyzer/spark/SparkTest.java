package com.bassoon.stockanalyzer.spark;

import com.bassoon.stockanalyzer.policy.RoeValue;
import com.bassoon.stockanalyzer.policy.StockNode;
import com.bassoon.stockanalyzer.policy.TwoEightNode;
import com.bassoon.stockanalyzer.policy.TwoEightRotation;
import com.bassoon.stockanalyzer.utils.DateUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import scala.Function1;
import scala.Tuple2;
import scala.reflect.ClassTag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SparkTest implements Serializable {
    private String dataSource = "jdbc:mysql://10.20.116.107:3306/CN_BASSOON?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
    private String dbUsername = "root";
    private String dbUserPwd = "Liaobi()7595k";
    private SparkSession sparkSession = null;

    public SparkTest() {
        System.setProperty("hadoop.home.dir", "C:\\home\\xxu\\github\\hadoop-common-2.2.0-bin\\");
        SparkConf sparkConf = new SparkConf().setAppName("myapp").set("spark.cores.max", "4").setMaster("spark://10.20.116.107:7077").
                setJars(new String[]{"C:\\home\\xxu\\github\\cuddly-bassoon\\backend\\stock-analyzer\\target\\stock-analyzer-0.0.1-SNAPSHOT.jar.original"});
        ;
        SparkContext sc = new SparkContext(sparkConf);
        sparkSession = SparkSession.builder().config(sc.getConf()).getOrCreate();
    }

    public Dataset<Row> getDatasetByTable(String tableName) {
        Dataset<Row> jdbcDF = sparkSession.read()
                .format("jdbc")
                .option("url", dataSource)
                .option("dbtable", tableName)
                .option("user", dbUsername)
                .option("password", dbUserPwd)
                .load();
        return jdbcDF;
    }

//    private Dataset<TwoEightNode> getWeekData(String table) {
//        Dataset<Row> dataset = this.getDatasetByTable(table);
//        dataset.createOrReplaceTempView(table);
//        dataset = dataset.sqlContext().sql("select * from " + table + " where date >= '2007-01-19'");
//        Encoder<TwoEightNode> twoEightNodeEncoder = Encoders.bean(TwoEightNode.class);
//        Dataset<TwoEightNode> ds = dataset.map(new MapFunction<Row, TwoEightNode>() {
//            TwoEightNode previousNode = new TwoEightNode();
//            TwoEightNode myself = null;
//
//            @Override
//            public TwoEightNode call(Row row) throws Exception {
//                String date = (String) row.getAs("date");
//                myself = new TwoEightNode();
//                if (DateUtils.dateToWeek(date) == 5) {
//                    Double close = (Double) row.getAs("close");
//                    myself.setDate(date);
//                    myself.setClose(close);
////                    myself.calulateMoney();
//                    if (this.previousNode.getClose() != 0) {
//                        double change = (double) ((myself.getClose() - previousNode.getClose()) / previousNode.getClose());
////                        myself.setHsmoney(Math.round(previousNode.getHsmoney() * (1 + change)));
//                    }
//                    previousNode = myself;
//                }
//                return myself;
//            }
//        }, twoEightNodeEncoder);
//        return ds;
//    }

    public List<TwoEightNode> comparedTwoAndEight(JavaRDD<TwoEightNode> hsJavaRDD, JavaRDD<TwoEightRotation> zzJavaRDD) {
        return null;

    }

    //stock_zz_k_data
    //stock_hs_k_data
    public List<TwoEightNode> generateTwoEightRatationData() {
        String[] tables = new String[]{"stock_zz_k_data", "stock_hs_k_data"};
        List<Dataset<Row>> dss = new ArrayList<Dataset<Row>>();
        for (String table : tables) {
            Dataset<Row> ds = this.getDatasetByTable(table);
            ds.createOrReplaceTempView(table);
            ds = ds.sqlContext().sql("select * from " + table + " where date >= '2007-01-19'");
            dss.add(ds);
        }
        Column[] columns_0 = new Column[]{dss.get(0).col("close").as("zzclose"), dss.get(0).col("date")};
        Column[] columns_1 = new Column[]{dss.get(1).col("close").as("hsclose"), dss.get(1).col("date")};
        Dataset<Row> ds = dss.get(0).select(columns_0).join(dss.get(1).select(columns_1), "date");
        ds = ds.sort("date");
        Dataset<TwoEightNode> _ds = ds.map(new MapFunction<Row, TwoEightNode>() {
            @Override
            public TwoEightNode call(Row row) throws Exception {
                TwoEightNode node = new TwoEightNode();
                String date = (String) row.getAs("date");
                if (DateUtils.dateToWeek(date) == 5) {
                    Double zzclose = (Double) row.getAs("zzclose");
                    Double hsclose = (Double) row.getAs("hsclose");
                    node.setDate(date);
                    node.setHsclose(hsclose);
                    node.setZzclose(zzclose);
                }
                return node;
            }
        }, Encoders.bean(TwoEightNode.class));
        return _ds.collectAsList();
    }

    public List<StockNode> stockEvalution(int year) {
        Dataset<Row> dataset = this.getDatasetByTable("stock_basics");
        dataset.createOrReplaceTempView("stock_basics_temp_view");
        dataset = dataset.sqlContext().sql("select code , name from stock_basics_temp_view");
        dataset = dataset.persist(StorageLevel.MEMORY_AND_DISK());
        Dataset<StockNode> ds = dataset.map(new MapFunction<Row, StockNode>() {
            @Override
            public StockNode call(Row row) throws Exception {
                String code = (String) row.getAs("code");
                String name = (String) row.getAs("name");
                StockNode node = new StockNode();
                if (code != null || !code.equals("")) {
                    node.setCode(code);
                    node.setName(name);
                }
                return node;
            }
        }, Encoders.bean(StockNode.class));
        List<StockNode> nodes = ds.collectAsList();
        for (StockNode node : nodes) {
            scoringROE(node, year);
        }
        return nodes;
    }

    public StockNode scoringROE(StockNode node, int year) {
        Dataset<Row> stock_profit_dataset = this.getDatasetByTable("stock_profit_data");
        stock_profit_dataset.createOrReplaceTempView("stock_profit_data_temp_view");
        //计算ROE
        //条件 ROE>0 , ROE(YEAR) > ROE(LAST YEAR)
        //ROE_TOTAL = 4季度ROE总和
        System.out.println("select roe from stock_profit_data_temp_view where code=\" + node.getCode() + \" and year=\" + year");
        Dataset<Double> roe_dataset = stock_profit_dataset.sqlContext().
                sql("select roe from stock_profit_data_temp_view where code=" + node.getCode() + " and year=" + year).as(Encoders.DOUBLE());
        System.out.println("kkkkkkk" + roe_dataset.collectAsList().size());
        Double doubleValue = roe_dataset.reduce(new ReduceFunction<Double>() {
            @Override
            public Double call(Double aDouble, Double t1) throws Exception {
                return aDouble + t1;
            }
        });
        if (doubleValue > 0) {
            node.setScore(1);
        }
        return node;
    }

    public static void main(String argz[]) {
        SparkTest test = new SparkTest();
//        System.out.println("-------" + test.generateTwoEightRatationData());
        test.stockEvalution(2017);

    }
}
