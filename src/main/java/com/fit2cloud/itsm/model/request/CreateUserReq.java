package com.fit2cloud.itsm.model.request;

import com.fit2cloud.commons.server.base.domain.User;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class CreateUserReq extends User {

    @ApiModelProperty(value = "用户来源", hidden = true)
    private String source;

    @ApiModelProperty(value = "组织", required = true)
    private String orgId;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
