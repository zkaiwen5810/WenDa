package com.example.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisTest {
    private static void print(int index, Object value){
        System.out.println(String.format("%d:%s", index, value.toString()));
    }


    public static void main(String[] args){
        Jedis jedis = new Jedis();

        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "1829293838");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));

        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hkeys(userKey));

        JedisPool jedisPool = new JedisPool();
        for (int i = 0; i < 100; i++){
            Jedis j = jedisPool.getResource();
            print(45, j.get("aaa"));
            j.close();
        }
    }
}
