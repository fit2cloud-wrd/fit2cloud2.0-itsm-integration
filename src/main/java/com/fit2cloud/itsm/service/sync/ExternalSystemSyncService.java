package com.fit2cloud.itsm.service.sync;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.UserMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.itsm.common.utils.SyncExternalOrgAndUserUtils;
import com.fit2cloud.itsm.dao.ExtPciApiLogMapper;
import com.fit2cloud.itsm.dao.PciExternalSystemOrgMapper;
import com.fit2cloud.itsm.dao.PciExternalSystemOrgMappingMapper;
import com.fit2cloud.itsm.dao.PciExternalSystemUserMapper;
import com.fit2cloud.itsm.model.PciExternalSystemOrg;
import com.fit2cloud.itsm.model.PciExternalSystemOrgMappingKey;
import com.fit2cloud.itsm.model.PciExternalSystemUser;
import com.fit2cloud.itsm.model.PciExternalSystemUserExample;
import com.fit2cloud.itsm.model.request.CreateOrganizationReq;
import com.fit2cloud.itsm.model.request.CreateUserReq;
import com.fit2cloud.itsm.model.request.UserOperateDTO;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalSystemSyncService {
    @Resource
    private PciExternalSystemOrgMapper pciExternalSystemOrgMapper;

    @Resource
    private PciExternalSystemUserMapper pciExternalSystemUserMapper;

    @Resource
    private PciExternalSystemOrgMappingMapper pciExternalSystemOrgMappingMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WorkspaceMapper workspaceMapper;

    @Resource
    private ApiLogService apiLogService;

    @Resource
    private ExtPciApiLogMapper extPciApiLogMapper;

    @Resource
    private SyncSettingService syncSettingService;

    @Resource
    private ExternalSystemService externalSystemService;

    public void syncOrgAndUser() {
        deleteHistoryDetailLog("orgSync");

        if (!SyncExternalOrgAndUserUtils.isCreateOrgServiceReady()) {
            throw new RuntimeException("管理中心服务异常，请联系管理员检查！");
        }

        // 查询机构中间表数据 org_sync_from_soa
        List<PciExternalSystemOrg> orgSyncFromSoaList = pciExternalSystemOrgMapper.selectByExample(null);

        // 查询已经存在的组织映射关系 org_soa_org
        Map<String, String> soaOrgMap = pciExternalSystemOrgMappingMapper.selectByExample(null).stream().collect(Collectors.toMap(PciExternalSystemOrgMappingKey::getExternalSystemOrgId, PciExternalSystemOrgMappingKey::getOrgId));

        // 按id排序，先把父组织创建
        Collections.sort(orgSyncFromSoaList, new Comparator<PciExternalSystemOrg>() {
            @Override
            public int compare(PciExternalSystemOrg u1, PciExternalSystemOrg u2) {
                try {
                    int diff = Integer.parseInt(u1.getId()) - Integer.parseInt(u2.getId());
                    if (diff > 0) {
                        return 1;
                    } else if (diff < 0) {
                        return -1;
                    }
                    return 0;
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        OrganizationExample example = new OrganizationExample();
        example.createCriteria().andParentIdEqualTo("0");
        List<Organization> organizationList = organizationMapper.selectByExample(example);

        // 查询开启同步的机构
        List<String> syncSettingList = syncSettingService.getIsSyncOrgList().stream().map(syncSetting -> syncSetting.getSyncOrgId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(syncSettingList)) {
            return;
        }

        // 创建组织
        for (PciExternalSystemOrg orgSyncFromSoa: orgSyncFromSoaList) {
            createOrgniztion(soaOrgMap, orgSyncFromSoa, syncSettingList,
                    CollectionUtils.isNotEmpty(organizationList) ? organizationList.get(0) : null);
        }

        // 更新组织
        for (PciExternalSystemOrg orgSyncFromSoa: orgSyncFromSoaList) {
            updateOrgniztion(soaOrgMap, orgSyncFromSoa, syncSettingList, CollectionUtils.isNotEmpty(organizationList) ? organizationList.get(0) : null);
        }

        // 同步用户
        syncUser();
    }

    private void createOrgniztion(Map<String, String> soaOrgMap, PciExternalSystemOrg orgSyncFromSoa, List<String> syncSettingList,
                                  Organization organization) {
        if (soaOrgMap.get(orgSyncFromSoa.getId()) == null
                && syncSettingList.stream().anyMatch(syncOrgId -> Objects.equals(syncOrgId, orgSyncFromSoa.getId()))) {
            // 若为最上级组织，需特殊处理，直接更新总部
            if (StringUtils.isBlank(orgSyncFromSoa.getParentId())) {
                updateFirstOrg(organization, orgSyncFromSoa, soaOrgMap);
                return;
            }
            // 创建组织
            CreateOrganizationReq createOrganizationReq = new CreateOrganizationReq();
            createOrganizationReq.setName(orgSyncFromSoa.getName());
            createOrganizationReq.setParentId(soaOrgMap.get(orgSyncFromSoa.getParentId()) == null ? "" : soaOrgMap.get(orgSyncFromSoa.getParentId()));
            createOrganizationReq.setDescription(orgSyncFromSoa.getShortName());
            createOrganizationReq.setCreateTime(Instant.now().toEpochMilli());
            createOrganizationReq.setCreateWorkspace(true);
            String workspaceName = orgSyncFromSoa.getName() + "-" + orgSyncFromSoa.getId() + Translator.get("i18n_menu_workspace");
            createOrganizationReq.setWorkspaceName(workspaceName);
            try {
                Organization org = SyncExternalOrgAndUserUtils.triggerCreateOrg(JSONObject.toJSONString(createOrganizationReq));
                if (org != null) {
                    // 插入组织映射关系
                    soaOrgMap.put(orgSyncFromSoa.getId(), org.getId());
                    PciExternalSystemOrgMappingKey orgSoaOrgKey = new PciExternalSystemOrgMappingKey();
                    orgSoaOrgKey.setOrgId(org.getId());
                    orgSoaOrgKey.setExternalSystemOrgId(orgSyncFromSoa.getId());
                    pciExternalSystemOrgMappingMapper.insert(orgSoaOrgKey);
                } else {
                    // 记录同步异常日志
                    apiLogService.insertSyncDetailLog("orgSync", orgSyncFromSoa.getName(), orgSyncFromSoa.getId(), "同步组织数据到管理中心返回数据为空！");
                }
            } catch (Exception exception) {
                // 记录同步异常日志
                apiLogService.insertSyncDetailLog("orgSync", orgSyncFromSoa.getName(), orgSyncFromSoa.getId(), exception.getMessage());
            }
        }
    }

    private void updateFirstOrg(Organization firstOrg, PciExternalSystemOrg orgSyncFromSoa, Map<String, String> soaOrgMap) {
        Organization organization = new Organization();
        BeanUtils.copyBean(organization, firstOrg);
        organization.setName(orgSyncFromSoa.getName());
        Organization org = SyncExternalOrgAndUserUtils.triggerUpdateOrg(JSONObject.toJSONString(organization));

        if (org == null) {
            // 记录同步异常日志
            apiLogService.insertSyncDetailLog("orgSync", orgSyncFromSoa.getName(), orgSyncFromSoa.getId(), "同步组织数据到管理中心返回数据为空！");
        } else if (!soaOrgMap.containsKey(orgSyncFromSoa.getId())) {
            // 更新外部机构表的最上级组织编码
            externalSystemService.setTopOrgValue(orgSyncFromSoa.getId());

            // 插入组织映射关系
            soaOrgMap.put(orgSyncFromSoa.getId(), org.getId());
            PciExternalSystemOrgMappingKey orgSoaOrgKey = new PciExternalSystemOrgMappingKey();
            orgSoaOrgKey.setOrgId(org.getId());
            orgSoaOrgKey.setExternalSystemOrgId(orgSyncFromSoa.getId());
            pciExternalSystemOrgMappingMapper.insert(orgSoaOrgKey);
        }
    }

    private void updateOrgniztion(Map<String, String> soaOrgMap, PciExternalSystemOrg orgSyncFromSoa, List<String> syncSettingList, Organization firstOrg) {
        try {
            Organization organization = organizationMapper.selectByPrimaryKey(soaOrgMap.get(orgSyncFromSoa.getId()));
            if (organization != null
                    && syncSettingList.stream().anyMatch(syncOrgId -> Objects.equals(syncOrgId, orgSyncFromSoa.getId()))) {
                organization.setName(orgSyncFromSoa.getName());
                // 若为最上级组织，需特殊处理，直接更新总部
                if (StringUtils.isBlank(orgSyncFromSoa.getParentId())) {
                    updateFirstOrg(firstOrg, orgSyncFromSoa, soaOrgMap);
                    return;
                } else {
                    organization.setParentId(soaOrgMap.get(orgSyncFromSoa.getParentId()) == null ? "" : soaOrgMap.get(orgSyncFromSoa.getParentId()));
                }
                Organization org = SyncExternalOrgAndUserUtils.triggerUpdateOrg(JSONObject.toJSONString(organization));
            }
        } catch (Exception exception) {
            // 记录同步异常日志
            apiLogService.insertSyncDetailLog("orgSync", orgSyncFromSoa.getName(), orgSyncFromSoa.getId(), exception.getMessage());
        }
    }

    public void syncUser() {
        deleteHistoryDetailLog("userSync");

        if (!SyncExternalOrgAndUserUtils.isCreateOrgServiceReady()) {
            throw new RuntimeException("管理中心服务异常，请联系管理员检查！");
        }

        // 同步用户 并把组织成员授权到对应的组织工作空间下
        PciExternalSystemUserExample userSyncFromSoaExample = new PciExternalSystemUserExample();
        List<PciExternalSystemUser> userList = pciExternalSystemUserMapper.selectByExample(userSyncFromSoaExample);

        // 查询已经存在的组织映射关系 org_soa_org
        Map<String, String> soaOrgMap = pciExternalSystemOrgMappingMapper.selectByExample(null).stream().collect(Collectors.toMap(PciExternalSystemOrgMappingKey::getExternalSystemOrgId, PciExternalSystemOrgMappingKey::getOrgId));

        // 查询开启同步的机构
        List<String> syncSettingList = syncSettingService.getIsSyncUserList().stream().map(syncSetting -> syncSetting.getSyncUserId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(syncSettingList)) {
            return;
        }

        for (PciExternalSystemUser userSyncFromSoa : userList) {
            try {
                if (syncSettingList.stream().allMatch(syncOrgId -> !Objects.equals(syncOrgId, userSyncFromSoa.getId()))) {
                    continue;
                }

                User user = userMapper.selectByPrimaryKey(userSyncFromSoa.getId());
                String orgId = soaOrgMap.get(userSyncFromSoa.getOrgId());
                String userId = "";

                if (user == null) {
                    userId = userSyncFromSoa.getId();
                    CreateUserReq userReq = new CreateUserReq();
                    userReq.setId(userId);
                    userReq.setName(userSyncFromSoa.getName());
                    userReq.setOrgId(orgId);
                    if (StringUtils.isBlank(userSyncFromSoa.getEmail())) {
                        userReq.setEmail(userSyncFromSoa.getId() + "@tbea.com");
                    } else {
                        userReq.setEmail(userSyncFromSoa.getEmail());
                    }
                    userReq.setPhone(userSyncFromSoa.getPhoneNumber());
                    userReq.setActive(userSyncFromSoa.getOnTheJob().equalsIgnoreCase("Y"));
                    userReq.setSource("SOA");
                    userReq.setCreateTime(System.currentTimeMillis());
                    userReq.setPassword(EncryptUtils.md5Encrypt("123456").toString());
                    UserDTO userDTO = SyncExternalOrgAndUserUtils.triggerCreateUser(JSONObject.toJSONString(userReq));

                    // 授权工作空间
                    WorkspaceExample example = new WorkspaceExample();
                    example.createCriteria().andOrganizationIdEqualTo(orgId);
                    List<Workspace> workspaces = workspaceMapper.selectByExample(example);

                    Map<String, Object> map = new HashMap<>();
                    map.put("userIds", Lists.newArrayList(userId));
                    map.put("roleId", "USER");
                    Optional.ofNullable(workspaces).orElse(new ArrayList<>()).forEach(workspace -> {
                        SyncExternalOrgAndUserUtils.triggerUpdateUserRoleByUser(workspace.getId(), map);
                    });

                } else {
                    userId = user.getId();
                    UserOperateDTO userReq = new UserOperateDTO();
                    BeanUtils.copyBean(userReq, user);
                    userReq.setName(userSyncFromSoa.getName());
                    userReq.setOrgId(orgId);
                    if (StringUtils.isBlank(userSyncFromSoa.getEmail())) {
                        userReq.setEmail(userSyncFromSoa.getId() + "@tbea.com");
                    } else {
                        userReq.setEmail(userSyncFromSoa.getEmail());
                    }
                    userReq.setPhone(userSyncFromSoa.getPhoneNumber());
                    userReq.setActive(userSyncFromSoa.getOnTheJob().equalsIgnoreCase("Y"));
                    userReq.setRoleInfoList(SyncExternalOrgAndUserUtils.getRoleInfo(userId));
                    UserDTO userDTO = SyncExternalOrgAndUserUtils.triggerUpdateUser(JSONObject.toJSONString(userReq));
                }


            } catch (Exception exception) {
                // 记录同步异常日志
                apiLogService.insertSyncDetailLog("userSync", userSyncFromSoa.getName(), userSyncFromSoa.getId(), exception.getMessage());
            }
        }
    }

    /**
     * 开启新的同步前删除旧的同步明细数据
     * @param type
     * @return
     */
    private void deleteHistoryDetailLog(String type) {
        apiLogService.deleteSyncDetailLogByApiLogId(type);
    }
}
