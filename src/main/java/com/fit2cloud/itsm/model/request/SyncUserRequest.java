package com.fit2cloud.itsm.model.request;

import com.fit2cloud.itsm.model.PciSyncUserSetting;
import io.swagger.annotations.ApiModelProperty;

public class SyncUserRequest extends PciSyncUserSetting {
    @ApiModelProperty(value = "排序", hidden = true)
    private String sort;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
