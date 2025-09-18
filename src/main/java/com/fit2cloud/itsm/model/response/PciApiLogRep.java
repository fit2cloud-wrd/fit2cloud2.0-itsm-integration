package com.fit2cloud.itsm.model.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PciApiLogRep implements Serializable {
    private String id;

    private String apiId;

    private String method;

    private String workspace;

    private String resourceType;

    private String resourceId;

    private String resourceName;

    private String module;

    private Integer code;

    private Long executeTime;

    private Integer expendedTime;

    private String apiAccountName;

    private String apiType;

    private String apiTypeName;

    private String workspaceName;

    private String endpoint;

    private String startExecuteTime;

    private String providerFactoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getExpendedTime() {
        return expendedTime;
    }

    public void setExpendedTime(Integer expendedTime) {
        this.expendedTime = expendedTime;
    }

    public String getApiAccountName() {
        return apiAccountName;
    }

    public void setApiAccountName(String apiAccountName) {
        this.apiAccountName = apiAccountName;
    }

    public String getApiTypeName() {
        return apiTypeName;
    }

    public void setApiTypeName(String apiTypeName) {
        this.apiTypeName = apiTypeName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getStartExecuteTime() {
        return startExecuteTime;
    }

    public void setStartExecuteTime(String startExecuteTime) {
        this.startExecuteTime = startExecuteTime;
    }

    public String getProviderFactoryId() {
        return providerFactoryId;
    }

    public void setProviderFactoryId(String providerFactoryId) {
        this.providerFactoryId = providerFactoryId;
    }
}