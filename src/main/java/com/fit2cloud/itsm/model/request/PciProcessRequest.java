package com.fit2cloud.itsm.model.request;

import io.swagger.annotations.ApiModelProperty;

public class PciProcessRequest {

    @ApiModelProperty("流程ID")
    private String processId;

    @ApiModelProperty("模块ID")
    private String module;

    @ApiModelProperty("订单号")
    private String businessKey;

    @ApiModelProperty("外部系统订单ID")
    private String externalProcessId;

    @ApiModelProperty("资源类型")
    private String resourceType;

    @ApiModelProperty(value = "排序", hidden = true)
    private String sort;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getExternalProcessId() {
        return externalProcessId;
    }

    public void setExternalProcessId(String externalProcessId) {
        this.externalProcessId = externalProcessId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
