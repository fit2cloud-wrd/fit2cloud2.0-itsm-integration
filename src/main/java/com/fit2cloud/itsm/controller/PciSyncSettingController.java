package com.fit2cloud.itsm.controller;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.model.request.SyncOrgRequest;
import com.fit2cloud.itsm.model.request.SyncUserRequest;
import com.fit2cloud.itsm.model.response.PciSyncOrgSettingRep;
import com.fit2cloud.itsm.model.response.PciSyncUserSettingRep;
import com.fit2cloud.itsm.service.sync.SyncSettingService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RequestMapping("sync/setting")
@RestController
public class PciSyncSettingController {

    @Resource
    private SyncSettingService syncSettingService;

    /**
     * 查询同步机构列表
     *
     * @param goPage
     * @param pageSize
     * @param templateRequest
     * @return
     */
    @I18n
    @RequestMapping(value = "/org/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    public Pager<List<PciSyncOrgSettingRep>> syncOrgList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody SyncOrgRequest syncOrgRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, syncSettingService.getSyncOrgList(syncOrgRequest));
    }

    @GetMapping("/org/list")
    public List<Map<String, String>> getSyncOrganizations() {
        SessionUser sessionUser = SessionUtils.getUser();
        return syncSettingService.getOrganizations(sessionUser);
    }

    /**
     * 新增同步机构
     * @param request
     */
    @PostMapping(value = "/org/add")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void addSyncOrg(@RequestBody SyncOrgRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        syncSettingService.insertSyncOrg(request, applyUser);
    }

    /**
     * 删除同步机构
     * @param id
     */
    @RequestMapping("/org/delete/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_DELETE)
    public void deleteOrg(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        syncSettingService.deleteSyncOrg(id);
    }

    /**
     * 批量删除同步机构
     * @param ids
     */
    @PostMapping(value = "/org/batchDelete")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_DELETE)
    public void batchDeleteOrg(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        syncSettingService.batchDeleteSyncOrg(ids);
    }

    /**
     * 更新同步机构
     * @param request
     */
    @PostMapping(value = "/org/update")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void updateSyncOrg(@RequestBody SyncOrgRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        syncSettingService.updateSyncOrg(request, applyUser);
    }



    /**
     * 同步机构启用/禁用
     * @param id
     */
    @RequestMapping("/org/enable/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void enableSyncOrg(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_org_setting_not_exist"));
        }
        syncSettingService.enableSyncOrg(id);
    }


    /**
     * 查询同步用户列表
     *
     * @param goPage
     * @param pageSize
     * @param templateRequest
     * @return
     */
    @I18n
    @RequestMapping(value = "/user/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public Pager<List<PciSyncUserSettingRep>> syncUserList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody SyncUserRequest syncUserRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, syncSettingService.getSyncUserList(syncUserRequest));
    }

    /**
     * 新增同步用户
     * @param request
     */
    @PostMapping(value = "/user/add")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void addSyncUser(@RequestBody SyncUserRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        syncSettingService.insertSyncUser(request, applyUser);
    }

    /**
     * 删除同步用户
     * @param id
     */
    @RequestMapping("/user/delete/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_DELETE)
    public void deleteUser(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        syncSettingService.deleteSyncUser(id);
    }

    /**
     * 批量删除同步用户
     * @param ids
     */
    @PostMapping(value = "/user/batchDelete")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_DELETE)
    public void batchDeleteUser(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        syncSettingService.batchDeleteSyncUser(ids);
    }

    /**
     * 更新同步用户
     * @param request
     */
    @PostMapping(value = "/user/update")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void updateSyncUser(@RequestBody SyncUserRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        syncSettingService.updateSyncUser(request, applyUser);
    }


    /**
     * 同步用户启用/禁用
     * @param id
     */
    @RequestMapping("/user/enable/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void enableSyncUser(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_user_setting_not_exist"));
        }
        syncSettingService.enableSyncUser(id);
    }
}

