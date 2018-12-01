package com.learn.redis.GeoHash;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GeoHashClass
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 19:29
 **/
public class GeoHashClass {
    private Jedis jedis;
    private static final String host = "redis://localhost:6379";
    private static final String password = "test";
    private static final String geoKey = "company";

    public GeoHashClass(Jedis jedis) {
        this.jedis = jedis;
    }

    public void setPointLongiLatiFromExcle(String path) {
        ReadExcel test = new ReadExcel();
        try {
            ArrayList<ArrayList<String>> arr = test.xlsx_reader(path, 0, 1, 2);
            for (int i = 1; i < arr.size(); i++) {
                ArrayList<String> row = arr.get(i);
                Long geoadd = jedis.geoadd(geoKey, Double.valueOf(row.get(1)), Double.valueOf(row.get(2)), row.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public  String getNextPoint(Double longitude,Double latitude){
        List<GeoRadiusResponse> georadius = jedis.georadius(geoKey, longitude, latitude, 20, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().withCoord().sortAscending());
        return georadius.get(0).getMemberByString();
    }


    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host);
        jedisShardInfo.setPassword(password);
        Jedis conn = new Jedis(jedisShardInfo);
        GeoHashClass geoHashClass = new GeoHashClass(conn);
        geoHashClass.setPointLongiLatiFromExcle("C:\\Users\\Administrator\\Desktop\\geohash.xlsx");
        System.out.println("成功放入redis中");
        String memberStr = geoHashClass.getNextPoint(116.514, 39.905);
        System.out.println("最近的点是："+memberStr);
    }
}
