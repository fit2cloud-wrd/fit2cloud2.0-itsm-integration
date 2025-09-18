package com.fit2cloud.itsm.model.request;

import com.fit2cloud.commons.server.base.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @description: TODO
 * @author zhaoqian
 * @date 2022/7/20 4:47 下午
 */
@ApiModel(parent = User.class)
public class UserOperateDTO extends User {

    @ApiModelProperty(value = "组织")
    private String orgId;

    private List<String> ids;

    @ApiModelProperty(value = "角色信息列表")
    private List<RoleInfo> roleInfoList;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    public void setRoleInfoList(List<RoleInfo> roleInfoList) {
        this.roleInfoList = roleInfoList;
    }
}
