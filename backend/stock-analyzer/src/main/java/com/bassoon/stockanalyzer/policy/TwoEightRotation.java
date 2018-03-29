package com.bassoon.stockanalyzer.policy;

import com.bassoon.stockanalyzer.service.JsonUtils;
import com.bassoon.stockanalyzer.spark.SparkRepository;
import com.bassoon.stockanalyzer.utils.DateUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class TwoEightRotation implements Serializable {
    @Autowired
    private SparkRepository sparkRepository;

    //stock_zz_k_data
    //stock_hs_k_data

    public List<TwoEightNode2> getWeekData(String table) {
        Dataset<Row> dataset = sparkRepository.getDatasetByTable(table);
        dataset.createOrReplaceTempView(table);
        dataset = dataset.sqlContext().sql("select * from " + table + " where date >= '2007-01-19'");
        dataset = dataset.persist(StorageLevel.MEMORY_AND_DISK());
        Dataset<TwoEightNode2> ds = dataset.map(new MapFunction<Row, TwoEightNode2>() {
            TwoEightNode2 previousNode = null;

            @Override
            public TwoEightNode2 call(Row row) throws Exception {
                String date = (String) row.getAs("date");
                if (DateUtils.dateToWeek(date) == 5) {
                    Double close = (Double) row.getAs("close");
                    TwoEightNode2 myself = new TwoEightNode2();
                    myself.setClose(close);
                    myself.setDate(date);
                    myself.setPreviousNode(previousNode);
                    myself.calulateMoney();
                    previousNode = myself;
                    return myself;
                }
                return null;
            }
        }, Encoders.bean(TwoEightNode2.class));
        ds = ds.filter(new FilterFunction<TwoEightNode2>() {
            @Override
            public boolean call(TwoEightNode2 twoEightNode) throws Exception {
                if (twoEightNode != null) {
                    return true;
                }
                return false;
            }
        });
        return ds.collectAsList();
    }

    /**
     * 二八轮动业务，一共4条线
     * reload = true 表示每次都重新通过spark计算，然后覆盖缓存
     * false 表示直接从缓存取
     *
     * @return
     */
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redisport}")
    private int port;

    public List<TwoEightNode> generateTwoEightRatationData(boolean reload) {
        Jedis jedis = new Jedis(host, port);
        if (!reload) {
            //先从缓存取
//            List<TwoEightNode> result = (List<TwoEightNode>) this.sparkJedisRepository.get("tow_eight", List.class, TwoEightNode.class);
            String json = jedis.get("two_eight");
            if(json != null && !json.equals("")){
                List<TwoEightNode> result = JsonUtils.jsonToObject(json, List.class, TwoEightNode.class);
                if (result != null) {
                    return result;
                }
            }
        }
        String[] tables = new String[]{"stock_zz_k_data", "stock_hs_k_data"};
        List<Dataset<Row>> dss = new ArrayList<Dataset<Row>>();
        for (String table : tables) {
            Dataset<Row> ds = sparkRepository.getDatasetByTable(table);
            ds.createOrReplaceTempView(table);
            ds = ds.sqlContext().sql("select * from " + table + " where date >= '2007-01-19'");
            dss.add(ds);
        }
        Column[] columns_0 = new Column[]{dss.get(0).col("close").as("zzclose"), dss.get(0).col("date")};
        Column[] columns_1 = new Column[]{dss.get(1).col("close").as("hsclose"), dss.get(1).col("date")};
        Dataset<Row> ds = dss.get(0).select(columns_0).join(dss.get(1).select(columns_1), "date");
        ds = ds.sort("date");
        ds = ds.persist(StorageLevel.MEMORY_AND_DISK());
        Dataset<TwoEightNode> _ds = ds.map(new MapFunction<Row, TwoEightNode>() {

            @Override
            public TwoEightNode call(Row row) throws Exception {
                String date = (String) row.getAs("date");
                if (DateUtils.dateToWeek(date) == 5) {
                    TwoEightNode node = new TwoEightNode();
                    Double zzclose = (Double) row.getAs("zzclose");
                    Double hsclose = (Double) row.getAs("hsclose");
                    node.setDate(date);
                    node.setHsclose(hsclose);
                    node.setZzclose(zzclose);
                    return node;
                }
                return null;
            }
        }, Encoders.bean(TwoEightNode.class));
        _ds = _ds.filter(new FilterFunction<TwoEightNode>() {
            @Override
            public boolean call(TwoEightNode twoEightNode) throws Exception {
                if (twoEightNode != null) {
                    return true;
                }
                return false;
            }
        });
        List<TwoEightNode> nodes = _ds.collectAsList();
        int nodesSize = nodes.size();
        TwoEightNode previousNode = null;
        TwoEightNode currentNode = null;
        TwoEightNode compareNode = null;
        for (int i = 0; i < nodesSize; i++) {
            //计算basic收益
            currentNode = nodes.get(i);
            currentNode.setPreviousNode(previousNode);
            currentNode.calulateBasicMoney();
            //计算轮动收益
            if (i - 3 >= 0) {
                currentNode.calculateAdvancedMoney(nodes.get(i - 3));
                currentNode.calculateAdvancedMoney2(nodes.get(i - 3));//如果四周数值比较后，涨幅小于0，那么空仓
            }
            previousNode = currentNode;
        }
        jedis.set("two_eight", JsonUtils.objectToJson(nodes));
        return nodes;
    }

    public static void main(String argz[]) {

    }
}
