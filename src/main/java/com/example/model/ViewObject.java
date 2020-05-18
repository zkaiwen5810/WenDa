package com.example.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {

    private Map<String, Object> objs = new HashMap<>();

    public Object get(String key){
        return objs.get(key);
    }

    public void set(String key, Object value){
        objs.put(key, value);
    }
}
