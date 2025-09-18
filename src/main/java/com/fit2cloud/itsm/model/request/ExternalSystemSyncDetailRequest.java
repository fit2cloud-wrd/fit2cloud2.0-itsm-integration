package com.fit2cloud.itsm.model.request;

import io.swagger.annotations.ApiModelProperty;

public class ExternalSystemSyncDetailRequest {

    private String pciApiLogId;

    @ApiModelProperty("机构名称或用户名称")
    private String name;

    @ApiModelProperty("类型：机构/用户")
    private Boolean type;

    @ApiModelProperty("排序")
    private Integer sort;

    public String getPciApiLogId() {
        return pciApiLogId;
    }

    public void setPciApiLogId(String pciApiLogId) {
        this.pciApiLogId = pciApiLogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
