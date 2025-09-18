package com.fit2cloud.itsm.model.response;

import com.fit2cloud.itsm.model.PciExternalSystemUser;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PciExternalSystemUserRep extends PciExternalSystemUser implements Serializable {
    private Boolean isSync;

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }
}