package com.fit2cloud.itsm.service.sync;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.itsm.common.constants.SystemParameterConstants;
import com.fit2cloud.itsm.dao.ExtPciSyncSettingMapper;
import com.fit2cloud.itsm.dao.PciExternalSystemOrgMapper;
import com.fit2cloud.itsm.dao.PciExternalSystemUserMapper;
import com.fit2cloud.itsm.model.PciExternalSystemOrg;
import com.fit2cloud.itsm.model.PciExternalSystemOrgExample;
import com.fit2cloud.itsm.model.PciExternalSystemUser;
import com.fit2cloud.itsm.model.PciExternalSystemUserExample;
import com.fit2cloud.itsm.model.dto.McOrgTreeNode;
import com.fit2cloud.itsm.model.request.PciExternalSystemOrgReq;
import com.fit2cloud.itsm.model.request.PciExternalSystemUserReq;
import com.fit2cloud.itsm.model.request.SyncOrgRequest;
import com.fit2cloud.itsm.model.request.SyncUserRequest;
import com.fit2cloud.itsm.model.response.PciExternalSystemOrgRep;
import com.fit2cloud.itsm.model.response.PciExternalSystemUserRep;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ExternalSystemService {

    @Resource
    private ExtPciSyncSettingMapper extPciSyncSettingMapper;

    @Resource
    private PciExternalSystemOrgMapper pciExternalSystemOrgMapper;

    @Resource
    private PciExternalSystemUserMapper pciExternalSystemUserMapper;

    @Resource
    private SyncSettingService syncSettingService;

    @Resource
    private SystemParameterMapper parameterMapper;

    public List<PciExternalSystemOrgRep> getExternalSystemOrgList(PciExternalSystemOrgReq request) {
        List<PciExternalSystemOrgRep> syncOrgSettingList = extPciSyncSettingMapper.getExternalSystemOrgList(request);
        Optional.ofNullable(syncOrgSettingList).orElse(new ArrayList<>()).forEach(orgSettingRep -> {
            if (orgSettingRep.getSync() == null) {
                orgSettingRep.setSync(false);
            }
        });
        return syncOrgSettingList;
    }

    public List<PciExternalSystemUserRep> getExternalSystemUserList(PciExternalSystemUserReq request) {
        List<PciExternalSystemUserRep> syncUserSettingRepList = extPciSyncSettingMapper.getExternalSystemUserList(request);
        Optional.ofNullable(syncUserSettingRepList).orElse(new ArrayList<>()).forEach(userSyncFromSoaRep -> {
            if (userSyncFromSoaRep.getSync() == null) {
                userSyncFromSoaRep.setSync(false);
            }
        });
        return syncUserSettingRepList;
    }

    public void addOrgToSyncSetting(String id, String applyUser) {
        PciExternalSystemOrg orgSyncFromSoa = pciExternalSystemOrgMapper.selectByPrimaryKey(id);

        if (orgSyncFromSoa == null) {
            F2CException.throwException(Translator.get("i18n_ex_sync_org_setting_not_exist"));
        }

        SyncOrgRequest syncOrgRequest = new SyncOrgRequest();
        syncOrgRequest.setIsSync(true);
        syncOrgRequest.setSyncOrgId(id);
        syncOrgRequest.setSyncOrgCode(orgSyncFromSoa.getId());
        syncOrgRequest.setSyncOrgName(orgSyncFromSoa.getName());
        syncOrgRequest.setRemark(orgSyncFromSoa.getCompanyCdescription());
        syncSettingService.insertSyncOrg(syncOrgRequest, applyUser);
    }

    @Transactional
    public void batchAddOrgToSyncSetting(List<String> ids, String applyUser) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            addOrgToSyncSetting(id, applyUser);
        });
    }

    public void addUserToSyncSetting(String id, String applyUser) {
        PciExternalSystemUser userSyncFromSoa = pciExternalSystemUserMapper.selectByPrimaryKey(id);

        if (userSyncFromSoa == null) {
            F2CException.throwException(Translator.get("i18n_ex_sync_user_setting_not_exist"));
        }

        SyncUserRequest syncUserRequest = new SyncUserRequest();
        syncUserRequest.setIsSync(true);
        syncUserRequest.setSyncUserId(id);
        syncUserRequest.setSyncUserCode(userSyncFromSoa.getId());
        syncUserRequest.setSyncUserName(userSyncFromSoa.getName());
        syncUserRequest.setRemark(userSyncFromSoa.getBusinessUnitName());
        syncUserRequest.setEmail(userSyncFromSoa.getEmail());
        syncUserRequest.setOrgId(userSyncFromSoa.getOrgId());
        syncUserRequest.setOrgName(userSyncFromSoa.getOrgName());
        syncSettingService.insertSyncUser(syncUserRequest, applyUser);
    }

    @Transactional
    public void batchAddUserToSyncSetting(List<String> ids, String applyUser) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            addUserToSyncSetting(id, applyUser);
        });
    }


    public List<TreeNode> orgTree(String checkId, boolean noRoot) {
        List<TreeNode> resultList = new ArrayList<>();
        PciExternalSystemOrgExample example = new PciExternalSystemOrgExample();
        List<PciExternalSystemOrg> orgs = pciExternalSystemOrgMapper.selectByExample(example);
        TreeNode node = new TreeNode();
        for (PciExternalSystemOrg org : orgs) {
            if (StringUtils.isBlank(org.getParentId())) {
                node.setId(org.getId());
                node.setName(org.getName());
                node.setChecked(org.getId().equalsIgnoreCase(checkId));
                node.setCollapsed(false);
                break;
            }
        }

        if (noRoot) {
            node = setChildren(node, orgs, checkId);
            resultList.addAll(node.getChildren());
        } else {
            resultList.add(setChildren(node, orgs, checkId));
        }
        return resultList;
    }

    public TreeNode setChildren(TreeNode node, List<PciExternalSystemOrg> list, String checkId) {
        final boolean open = list.size()<100;
        List<McOrgTreeNode> listnew = list.stream().map(organization -> {
            McOrgTreeNode treeNode = new McOrgTreeNode();
            treeNode.setId(organization.getId());
            treeNode.setName(organization.getName());
            treeNode.setChecked(organization.getId().equalsIgnoreCase(checkId));
            if (open) {
                treeNode.setCollapsed(false);
            }
            treeNode.setParentId(organization.getParentId());
            return treeNode;
        }).collect(Collectors.toList());
        //找不到父的才会放到此里面
        List<TreeNode> result = new ArrayList<>();
        Map<String,McOrgTreeNode> fatherDTO = listnew.stream().collect(Collectors.toMap(McOrgTreeNode::getId, item -> item));
        for(McOrgTreeNode org : listnew)
        {

            if(fatherDTO.containsKey(org.getParentId()))
            {
                fatherDTO.get(org.getParentId()).getChildren().add(org);
            }
            else{
                if(org.getParentId().equals(node.getId())){
                    result.add(org);
                }
            }
        }
        if (fatherDTO.containsKey(node.getId())) {
            node.setChildren(fatherDTO.get(node.getId()).getChildren());
        }
        return node;
    }


    public PciExternalSystemOrg getOrgById(String id) {
        return pciExternalSystemOrgMapper.selectByPrimaryKey(id);
    }

    public List<PciExternalSystemUser> allUsers() {
        PciExternalSystemUserExample userExample = new PciExternalSystemUserExample();
        List<PciExternalSystemUser> allUsers = pciExternalSystemUserMapper.selectByExample(userExample);
        allUsers.stream().forEach(user -> user.setName(user.getName()+"("+user.getId()+")"));
        return allUsers;
    }

    public List<PciExternalSystemOrgRep> orgTreeList(String orgId, boolean noRoot) {
        if (orgId == null) {
            orgId = "10000000";
        }
        PciExternalSystemOrgExample example = new PciExternalSystemOrgExample();
        List<PciExternalSystemOrg> orgs = pciExternalSystemOrgMapper.selectByExample(example);
        return orgTreeListSort(orgId, orgs, 0, "", noRoot);
    }


    public List<PciExternalSystemOrgRep> orgTreeListSort(String parentOrgId, List<PciExternalSystemOrg> orgs, int count, String path, boolean noRoot) {
        List<PciExternalSystemOrgRep> orgsResult = new ArrayList<PciExternalSystemOrgRep>();
        Map<String, List<PciExternalSystemOrgRep>> pidChildrenOrgsMap = new HashMap<>();
        AtomicReference<PciExternalSystemOrg> dbParentOrg = new AtomicReference<>();
        orgs.forEach(organization -> {
            PciExternalSystemOrgRep organizationDTO = new PciExternalSystemOrgRep();
            BeanUtils.copyBean(organizationDTO, organization);
            pidChildrenOrgsMap.compute(organization.getParentId(), (k, v) -> Optional.ofNullable(v).orElse(new ArrayList<>())).add(organizationDTO);
            if (StringUtils.equals(organization.getId(), parentOrgId)) {
                dbParentOrg.set(organization);
            }
        });
        if (Objects.isNull(dbParentOrg.get())) {
            F2CException.throwException("System Error. Cannot find organization by id: " + parentOrgId);
        }
        PciExternalSystemOrgRep parentOrg = new PciExternalSystemOrgRep();
        BeanUtils.copyBean(parentOrg, dbParentOrg.get());
        parentOrg.setShowText("/" + parentOrg.getName());
        parentOrg.setTmpLevel(0);
        orgsResult.add(parentOrg);

        getChildOrg(orgsResult, pidChildrenOrgsMap, parentOrg, 1);

        boolean isAdminUser = Objects.equals(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ADMIN.toString());
        if (noRoot && orgsResult.size() > 0 && isAdminUser) {
            orgsResult.remove(0);
        }
        return orgsResult;
    }


    private void getChildOrg(List<PciExternalSystemOrgRep> orgsResult, Map<String,List<PciExternalSystemOrgRep>> pidChildrenOrgsMap, PciExternalSystemOrgRep parentOrg, int level){
        List<PciExternalSystemOrgRep> children = pidChildrenOrgsMap.get(parentOrg.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            int nextLevel =  level + 1;
            for (PciExternalSystemOrgRep child : children) {
                child.setShowText(String.format("%s/%s", parentOrg.getShowText(), child.getName()));
                child.setName(getBlank(level) + child.getName());
                child.setTmpLevel(level);
                orgsResult.add(child);
                getChildOrg(orgsResult, pidChildrenOrgsMap, child, nextLevel);
            }
        }
    }

    public String getBlank(int i) {
        StringBuffer sb = new StringBuffer();
        for(int m = 1;m<=i;m++){
            sb.append("\u3000");
        }
        return sb.toString();
    }

    public String getTopOrgValue() {
        SystemParameter systemParameter = parameterMapper.selectByPrimaryKey(SystemParameterConstants.EXTERNAL_SYSTEM_TOP_ORG);
        if (systemParameter == null) {
            // 不存在则插入
            systemParameter = new SystemParameter();
            systemParameter.setParamKey(SystemParameterConstants.EXTERNAL_SYSTEM_TOP_ORG);
            systemParameter.setType("text");
            parameterMapper.insert(systemParameter);
        }
        return systemParameter.getParamValue();
    }

    public void setTopOrgValue(String code) {
        SystemParameter systemParameter = parameterMapper.selectByPrimaryKey(SystemParameterConstants.EXTERNAL_SYSTEM_TOP_ORG);
        if (systemParameter == null) {
            // 不存在则插入
            systemParameter = new SystemParameter();
            systemParameter.setParamKey(SystemParameterConstants.EXTERNAL_SYSTEM_TOP_ORG);
            systemParameter.setType("text");
            systemParameter.setParamValue(code);
            parameterMapper.insert(systemParameter);
        } else {
            systemParameter.setParamValue(code);
            parameterMapper.updateByPrimaryKey(systemParameter);
        }
    }
}
