package com.fit2cloud.itsm.service.sync;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.utils.IDGenerator;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.DateUtil;
import com.fit2cloud.itsm.common.constants.PciTableIdConstants;
import com.fit2cloud.itsm.dao.ExtPciSyncSettingMapper;
import com.fit2cloud.itsm.dao.PciSyncOrgSettingMapper;
import com.fit2cloud.itsm.dao.PciSyncUserSettingMapper;
import com.fit2cloud.itsm.model.PciSyncOrgSetting;
import com.fit2cloud.itsm.model.PciSyncOrgSettingExample;
import com.fit2cloud.itsm.model.PciSyncUserSetting;
import com.fit2cloud.itsm.model.PciSyncUserSettingExample;
import com.fit2cloud.itsm.model.request.SyncOrgRequest;
import com.fit2cloud.itsm.model.request.SyncUserRequest;
import com.fit2cloud.itsm.model.response.PciSyncOrgSettingRep;
import com.fit2cloud.itsm.model.response.PciSyncUserSettingRep;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SyncSettingService {

    @Resource
    private PciSyncOrgSettingMapper pciSyncOrgSettingMapper;

    @Resource
    private
    PciSyncUserSettingMapper pciSyncUserSettingMapper;

    @Resource
    private ExtPciSyncSettingMapper extPciSyncSettingMapper;

    public List<PciSyncOrgSettingRep> getSyncOrgList(SyncOrgRequest syncOrgRequest) {
        List<PciSyncOrgSettingRep> syncOrgSettingList = extPciSyncSettingMapper.getSyncOrgList(syncOrgRequest);

        Optional.ofNullable(syncOrgSettingList).orElse(new ArrayList<>()).forEach(orgSettingRep -> {
            orgSettingRep.setCreateTimeText(DateUtil.getLong2ShortString(orgSettingRep.getCreateTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
            orgSettingRep.setUpdateTimeText(DateUtil.getLong2ShortString(orgSettingRep.getUpdateTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
        });

        return syncOrgSettingList;
    }

    public List<Map<String, String>> getOrganizations(SessionUser sessionUser) {
        PciSyncOrgSettingExample example = new PciSyncOrgSettingExample();
        example.setOrderByClause("sync_org_id");
        List<PciSyncOrgSetting> data = pciSyncOrgSettingMapper.selectByExample(example);
        List<Map<String, String>>  list = new ArrayList<>();
        for(PciSyncOrgSetting o :data){
            Map<String ,String> map= new HashMap<>();
            map.put("lable", o.getSyncOrgName());
            map.put("value", o.getSyncOrgId());
            list.add(map);
        }

        return list;
    }

    public List<PciSyncUserSettingRep> getSyncUserList(SyncUserRequest syncUserRequest) {
        List<PciSyncUserSettingRep> syncUserSettingRepList = extPciSyncSettingMapper.getSyncUserList(syncUserRequest);

        Optional.ofNullable(syncUserSettingRepList).orElse(new ArrayList<>()).forEach(userSettingRep -> {
            userSettingRep.setCreateTimeText(DateUtil.getLong2ShortString(userSettingRep.getCreateTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
            userSettingRep.setUpdateTimeText(DateUtil.getLong2ShortString(userSettingRep.getUpdateTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
        });

        return syncUserSettingRepList;
    }

    /**
     * 插入同步组织
     */
    public PciSyncOrgSetting insertSyncOrg(SyncOrgRequest syncOrgRequest, String applyUser) {
        PciSyncOrgSetting pciSyncOrgSetting = new PciSyncOrgSetting();

        PciSyncOrgSettingExample example = new PciSyncOrgSettingExample();
        example.createCriteria().andSyncOrgCodeEqualTo(syncOrgRequest.getSyncOrgCode());
        if (pciSyncOrgSettingMapper.countByExample(example) > 0){
            F2CException.getException("i18n_sync_org_name_not_same");
        }

        BeanUtils.copyBean(pciSyncOrgSetting, syncOrgRequest);

        if (StringUtils.isBlank(pciSyncOrgSetting.getSyncOrgId())) {
            pciSyncOrgSetting.setSyncOrgId(pciSyncOrgSetting.getSyncOrgCode());
        }

        String syncOrgId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_SYNC_ORG_ID_PREFIX);
        pciSyncOrgSetting.setId(syncOrgId);
        pciSyncOrgSetting.setCreateTime(System.currentTimeMillis());
        pciSyncOrgSetting.setUpdateTime(System.currentTimeMillis());
        pciSyncOrgSetting.setCreateUser(applyUser);
        pciSyncOrgSetting.setIsSync(true);
        pciSyncOrgSettingMapper.insert(pciSyncOrgSetting);

        return pciSyncOrgSetting;
    }

    public PciSyncOrgSetting updateSyncOrg(SyncOrgRequest syncOrgRequest, String applyUser) {
        PciSyncOrgSetting pciSyncOrgSetting = new PciSyncOrgSetting();

        BeanUtils.copyBean(pciSyncOrgSetting, syncOrgRequest);
        pciSyncOrgSetting.setUpdateTime(System.currentTimeMillis());
        pciSyncOrgSetting.setUpdateUser(applyUser);
        pciSyncOrgSettingMapper.updateByPrimaryKeySelective(pciSyncOrgSetting);

        return pciSyncOrgSetting;
    }

    public void enableSyncOrg(String id) {
        PciSyncOrgSetting pciSyncOrgSetting = pciSyncOrgSettingMapper.selectByPrimaryKey(id);

        if (pciSyncOrgSetting == null) {
            F2CException.throwException(Translator.get("i18n_ex_sync_org_setting_not_exist"));
        }

        if (pciSyncOrgSetting.getIsSync()) {
            pciSyncOrgSetting.setIsSync(false);
        } else {
            pciSyncOrgSetting.setIsSync(true);
        }

        pciSyncOrgSettingMapper.updateByPrimaryKeySelective(pciSyncOrgSetting);
    }

    public void deleteSyncOrg(String id) {
        pciSyncOrgSettingMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void batchDeleteSyncOrg(List<String> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            deleteSyncOrg(id);
        });
    }

    /**
     * 插入同步用户
     */
    public PciSyncUserSetting insertSyncUser(SyncUserRequest syncUserRequest, String applyUser) {
        PciSyncUserSetting pciSyncUserSetting = new PciSyncUserSetting();

        BeanUtils.copyBean(pciSyncUserSetting, syncUserRequest);

        PciSyncUserSettingExample example = new PciSyncUserSettingExample();
        example.createCriteria().andSyncUserCodeEqualTo(syncUserRequest.getSyncUserCode());
        if (pciSyncUserSettingMapper.countByExample(example) > 0){
            F2CException.throwException(Translator.get("i18n_sync_user_name_not_same"));
        }

        if (StringUtils.isBlank(pciSyncUserSetting.getSyncUserId())) {
            pciSyncUserSetting.setSyncUserId(pciSyncUserSetting.getSyncUserCode());
        }

        String syncUserId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_SYNC_USER_ID_PREFIX);
        pciSyncUserSetting.setId(syncUserId);
        pciSyncUserSetting.setCreateTime(System.currentTimeMillis());
        pciSyncUserSetting.setUpdateTime(System.currentTimeMillis());
        pciSyncUserSetting.setCreateUser(applyUser);
        pciSyncUserSetting.setIsSync(true);
        pciSyncUserSettingMapper.insert(pciSyncUserSetting);

        return pciSyncUserSetting;
    }

    public PciSyncUserSetting updateSyncUser(SyncUserRequest syncUserRequest, String applyUser) {
        PciSyncUserSetting pciSyncUserSetting = new PciSyncUserSetting();

        BeanUtils.copyBean(pciSyncUserSetting, syncUserRequest);
        pciSyncUserSetting.setUpdateTime(System.currentTimeMillis());
        pciSyncUserSetting.setUpdateUser(applyUser);
        pciSyncUserSettingMapper.updateByPrimaryKeySelective(pciSyncUserSetting);

        return pciSyncUserSetting;
    }


    public void deleteSyncUser(String id) {
        pciSyncUserSettingMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void batchDeleteSyncUser(List<String> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            deleteSyncUser(id);
        });
    }


    public void enableSyncUser(String id) {
        PciSyncUserSetting pciSyncUserSetting = pciSyncUserSettingMapper.selectByPrimaryKey(id);

        if (pciSyncUserSetting == null) {
            F2CException.throwException(Translator.get("i18n_ex_sync_user_setting_not_exist"));
        }

        if (pciSyncUserSetting.getIsSync()) {
            pciSyncUserSetting.setIsSync(false);
        } else {
            pciSyncUserSetting.setIsSync(true);
        }

        pciSyncUserSettingMapper.updateByPrimaryKeySelective(pciSyncUserSetting);
    }

    public List<PciSyncOrgSetting> getIsSyncOrgList() {
        PciSyncOrgSettingExample example = new PciSyncOrgSettingExample();
        example.createCriteria().andIsSyncEqualTo(true);
        return pciSyncOrgSettingMapper.selectByExample(example);
    }

    public List<PciSyncUserSetting> getIsSyncUserList() {
        PciSyncUserSettingExample example = new PciSyncUserSettingExample();
        example.createCriteria().andIsSyncEqualTo(true);
        return pciSyncUserSettingMapper.selectByExample(example);
    }

}
