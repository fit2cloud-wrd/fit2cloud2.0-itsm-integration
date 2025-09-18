package com.fit2cloud.itsm.model.request;

import io.swagger.annotations.ApiModelProperty;

public class PciApiAccountRequest {
    private String name;

    private String systemType;

    private String providerFactoryId;

    private String status;

    private String apiEndpoint;

    private String testApiEndpoint;

    private String version;

    private String enableFlag;

    private String syncStatus;

    private Boolean autoSync;


    private String sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getProviderFactoryId() {
        return providerFactoryId;
    }

    public void setProviderFactoryId(String providerFactoryId) {
        this.providerFactoryId = providerFactoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getTestApiEndpoint() {
        return testApiEndpoint;
    }

    public void setTestApiEndpoint(String testApiEndpoint) {
        this.testApiEndpoint = testApiEndpoint;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Boolean getAutoSync() {
        return autoSync;
    }

    public void setAutoSync(Boolean autoSync) {
        this.autoSync = autoSync;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
