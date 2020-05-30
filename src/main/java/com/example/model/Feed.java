package com.example.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {

    private int id;
    private int userId;
    private Date createdDate;
    private int type;
    private String data;
    private JSONObject jsonObject = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        jsonObject = JSONObject.parseObject(data);
    }

    public String get(String key) {
        return jsonObject == null ? null : jsonObject.getString(key);
    }
}
