package com.fit2cloud.itsm.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.domain.WorkspaceExample;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtWorkspaceCommonMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.service.RoleCommonService;
import com.fit2cloud.commons.server.utils.OrganizationUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {
    @Resource
    private ExtWorkspaceCommonMapper extWorkspaceCommonMapper;
    @Resource
    private RoleCommonService roleCommonService;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private OrganizationMapper organizationMapper;

    public List<Workspace> selectWorkByNames(List<String> names){
        return workspaceMapper.selectByExample(new WorkspaceExample(){{
            createCriteria().andNameIn(names);
        }});
    }

    public Workspace selectWorkspaceByName(String name){
        List<Workspace> workspaces = workspaceMapper.selectByExample(new WorkspaceExample() {{
            createCriteria().andNameEqualTo(name);
        }});
        if (CollUtil.isNotEmpty(workspaces)){
            return workspaces.get(0);
        }
        return null;
    }
    public List<TreeNode> selectWorkspaceTreeNode(String userId) {
        String roleId = SessionUtils.getUser().getParentRoleId();
        if (roleCommonService.isExtendAdmin(roleId)) {
            return extWorkspaceCommonMapper.selectWorkspaceTreeNode(null, null, null);
        } else if (roleCommonService.isExtendOrgAdmin(roleId)) {
            return extWorkspaceCommonMapper.selectWorkspaceTreeNode(userId, roleId, SessionUtils.getOrganizationId());
        } else {
            return null;
        }
    }

    public List<Workspace> listOfUser() {
        WorkspaceExample example = new WorkspaceExample();
        if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            example.createCriteria().andOrganizationIdIn(OrganizationUtils.getOrgIdsByParentIdsWithSelf(SessionUtils.getOrganizationId()));
        }
        example.setOrderByClause("name");
        return workspaceMapper.selectByExample(example);
    }

    Workspace selectWorkspaceById(String id) {
        return workspaceMapper.selectByPrimaryKey(id);
    }

    //organizationCheckFunc: 参数是组织ID，返回该组织是否被选中
    //workspaceCheckFunc: 参数是工作空间ID，返回该工作空间是否被选中
    List<TreeNode> getWorkspaceTree(Function<String, Boolean> organizationCheckFunc, Function<String, Boolean> workspaceCheckFunc) {
        List<Organization> organizations = organizationMapper.selectByExample(null);

        if (CollectionUtils.isEmpty(organizations)) {
            return new ArrayList<>();
        }

        List<Workspace> workspaces = workspaceMapper.selectByExample(null);
        if (CollectionUtils.isEmpty(workspaces)) {
            return new ArrayList<>();
        }

        Map<String, String> organizationMap = new ConcurrentHashMap<>();
        organizations.parallelStream().forEach(organization -> organizationMap.put(organization.getId(), organization.getName()));

        List<TreeNode> result = new Vector<>();
        Map<String, List<Workspace>> collect = workspaces.parallelStream()
                .filter(workspace -> organizationMap.containsKey(workspace.getOrganizationId()))
                .collect(Collectors.groupingBy(Workspace::getOrganizationId));

        collect.entrySet().parallelStream().forEach(entry -> result.add(new TreeNode() {{
            setId(entry.getKey());
            setName(organizationMap.get(entry.getKey()));
            setChecked(organizationCheckFunc.apply(entry.getKey()));
            boolean parentChecked = isChecked();
            setChildren(entry.getValue().parallelStream().map(workspace -> new TreeNode() {{
                setId(workspace.getId());
                setName(workspace.getName());
                setChecked(parentChecked || workspaceCheckFunc.apply(workspace.getId()));
            }}).collect(Collectors.toList()));
        }}));
        return result.parallelStream().sorted(Comparator.comparing(TreeNode::getName)).collect(Collectors.toList());
    }

    public Workspace getWorkSpace(String workspaceId) {
        return workspaceMapper.selectByPrimaryKey(workspaceId);
    }

}
