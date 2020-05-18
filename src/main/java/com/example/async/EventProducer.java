package com.example.async;

import com.alibaba.fastjson.JSONObject;
import com.example.util.RedisAdapter;
import com.example.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    RedisAdapter redisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventqueueKey();
            redisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
