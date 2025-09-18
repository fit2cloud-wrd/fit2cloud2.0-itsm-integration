package com.fit2cloud.itsm.model.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/6/18  下午2:48
 * Description:
 */
public class RoleInfo implements Serializable {

    @ApiModelProperty("是否创建新的工作空间")
    private boolean workspace;//前缀不能is、create!
    @ApiModelProperty("组织 ID 集合")
    private List<String> organizationIds;
    @ApiModelProperty("普通用户选择工作空间为哪一个组织")
    private String selectOrganizationId;//普通用户选择工作空间为哪一个组织
    @ApiModelProperty("工作空间 ID 集合")
    private List<String> workspaceIds;
    @ApiModelProperty("角色ID")
    private String roleId;
    @ApiModelProperty("角色父ID")
    private String roleParentId;

    public boolean getWorkspace() {
        return workspace;
    }

    public void setWorkspace(boolean workspace) {
        this.workspace = workspace;
    }

    public List<String> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<String> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<String> getWorkspaceIds() {
        return workspaceIds;
    }

    public void setWorkspaceIds(List<String> workspaceIds) {
        this.workspaceIds = workspaceIds;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleParentId() {
        return roleParentId;
    }

    public void setRoleParentId(String roleParentId) {
        this.roleParentId = roleParentId;
    }

    public String getSelectOrganizationId() {
        return selectOrganizationId;
    }

    public void setSelectOrganizationId(String selectOrganizationId) {
        this.selectOrganizationId = selectOrganizationId;
    }
}
