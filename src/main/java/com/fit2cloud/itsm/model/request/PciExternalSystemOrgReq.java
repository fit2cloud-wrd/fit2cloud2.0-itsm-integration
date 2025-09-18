package com.fit2cloud.itsm.model.request;

import com.fit2cloud.itsm.model.PciExternalSystemOrg;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PciExternalSystemOrgReq extends PciExternalSystemOrg implements Serializable {
    @ApiModelProperty(value = "排序", hidden = true)
    private String sort;

    @ApiModelProperty("同步标识（1同步/0不同步）")
    private Boolean isSync;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }
}