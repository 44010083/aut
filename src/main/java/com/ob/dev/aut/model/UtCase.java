package com.ob.dev.aut.model;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class UtCase {
    private String id;
    private String name;
    private String in;
    private String apiId;
    private String out;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArgsModel> getIn() {
        List<ArgsModel> result = new ArrayList<ArgsModel>();
        try {
            result = JSON.parseArray(in, ArgsModel.class);
        } catch (Exception e) {
            result = new ArrayList<ArgsModel>();
        }
        return result;
    }

    public void setIn(String in) {
        this.in = in;
    }


    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
