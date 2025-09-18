package com.fit2cloud.itsm.model.request;

import com.fit2cloud.commons.server.base.domain.Organization;
import io.swagger.annotations.ApiModelProperty;

public class CreateOrganizationReq extends Organization {
    @ApiModelProperty(value = "组织名称", required = true)
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("英文名")
    private String englishName;

    @ApiModelProperty("父组织ID")
    private String parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否创建工作空间")
    private boolean createWorkspace;

    @ApiModelProperty("工作空间名称")
    private String workspaceName;

    //组织管理员
    private String administrators;

    //组织管理员是否加入当前组织
    private boolean adminJoin;

    //组织管理员是否授权到组织下所有工作空间
    private boolean adminGrantWorkspace;

    public boolean isAdminJoin() {
        return adminJoin;
    }

    public void setAdminJoin(boolean adminJoin) {
        this.adminJoin = adminJoin;
    }

    public String getAdministrators() {
        return administrators;
    }

    public boolean isAdminGrantWorkspace() {
        return adminGrantWorkspace;
    }

    public void setAdminGrantWorkspace(boolean adminGrantWorkspace) {
        this.adminGrantWorkspace = adminGrantWorkspace;
    }

    public void setAdministrators(String administrators) {
        this.administrators = administrators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean isCreateWorkspace() {
        return createWorkspace;
    }

    public void setCreateWorkspace(boolean createWorkspace) {
        this.createWorkspace = createWorkspace;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
}
