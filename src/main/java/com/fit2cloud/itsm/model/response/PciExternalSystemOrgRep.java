package com.fit2cloud.itsm.model.response;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.itsm.model.PciExternalSystemOrg;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class PciExternalSystemOrgRep extends PciExternalSystemOrg implements Serializable {
    private Boolean isSync;

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    @ApiModelProperty("组织数量")
    private long countOrgAdmin;
    @ApiModelProperty("子组织数量")
    private long countOrg;
    @ApiModelProperty("用户数量")
    private long countUser;
    @ApiModelProperty("单选框选中后显示文字")
    private String showText;
    @ApiModelProperty("组织层级，按照当前查询数据计算出的层级。非固定数据。如：若组织 A 在第二层，查询所有组织时 A 的层级是 2，当查询 A 及其所有下级时，A 的层级是 0")
    private int tmpLevel;

    public long getCountOrgAdmin() {
        return countOrgAdmin;
    }

    public void setCountOrgAdmin(long countOrgAdmin) {
        this.countOrgAdmin = countOrgAdmin;
    }

    public long getCountOrg() {
        return countOrg;
    }

    public void setCountOrg(long countOrg) {
        this.countOrg = countOrg;
    }

    public long getCountUser() {
        return countUser;
    }

    public void setCountUser(long countUser) {
        this.countUser = countUser;
    }

    public String getShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public int getTmpLevel() {
        return tmpLevel;
    }

    public void setTmpLevel(int tmpLevel) {
        this.tmpLevel = tmpLevel;
    }
}