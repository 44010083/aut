package com.ob.dev.aut.model;

public class ApiModel {
    private String id;
    private String packageName;
    private String fileName;
    private String bootPath;
    private String apiName;
    private String apiPath;
    private String apiInput;
    private String apiOutput;
    private String apiType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getBootPath() {
        return bootPath;
    }

    public void setBootPath(String bootPath) {
        this.bootPath = bootPath;
    }

    public String getApiInput() {
        return apiInput;
    }

    public void setApiInput(String apiInput) {
        this.apiInput = apiInput;
    }

    public String getApiOutput() {
        return apiOutput;
    }

    public void setApiOutput(String apiOutput) {
        this.apiOutput = apiOutput;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }
}
