package com.fit2cloud.itsm.controller;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.model.PciExternalSystemOrg;
import com.fit2cloud.itsm.model.PciExternalSystemUser;
import com.fit2cloud.itsm.model.request.PciExternalSystemOrgReq;
import com.fit2cloud.itsm.model.request.PciExternalSystemUserReq;
import com.fit2cloud.itsm.model.response.PciExternalSystemOrgRep;
import com.fit2cloud.itsm.model.response.PciExternalSystemUserRep;
import com.fit2cloud.itsm.service.sync.ExternalSystemService;
import com.fit2cloud.itsm.service.sync.SyncSettingService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("external/system")
@RestController
public class ExternalSystemController {

    @Resource
    private SyncSettingService syncSettingService;

    @Resource
    private ExternalSystemService externalSystemService;

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
    public Pager<List<PciExternalSystemOrgRep>> syncOrgList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciExternalSystemOrgReq request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, externalSystemService.getExternalSystemOrgList(request));
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
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    public Pager<List<PciExternalSystemUserRep>> syncUserList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciExternalSystemUserReq request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, externalSystemService.getExternalSystemUserList(request));
    }

    @RequestMapping("/org/addToSyncSetting/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void addOrgToSyncSetting(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_org_setting_not_exist"));
        }
        String applyUser = SessionUtils.getUser().getId();
        externalSystemService.addOrgToSyncSetting(id, applyUser);
    }

    @PostMapping(value = "/org/batchAddToSyncSetting")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void batchAddOrgToSyncSetting(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_org_setting_not_exist"));
        }
        String applyUser = SessionUtils.getUser().getId();
        externalSystemService.batchAddOrgToSyncSetting(ids, applyUser);
    }

    @RequestMapping("/user/addToSyncSetting/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void addUserToSyncSetting(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_user_setting_not_exist"));
        }
        String applyUser = SessionUtils.getUser().getId();
        externalSystemService.addUserToSyncSetting(id, applyUser);
    }

    @PostMapping(value = "/user/batchAddToSyncSetting")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_EDIT)
    public void batchAddUserToSyncSetting(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_sync_user_setting_not_exist"));
        }
        String applyUser = SessionUtils.getUser().getId();
        externalSystemService.batchAddUserToSyncSetting(ids, applyUser);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_org_tree" + Translator.SUFFIX)
    @GetMapping(value = "/orgTree/{id}")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    @I18n
    public List<TreeNode> orgTree(@PathVariable String id) {
        SessionUser sessionUser = SessionUtils.getUser();
        return externalSystemService.orgTree(id, false);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_organization_tag" + Translator.SUFFIX)
    @GetMapping(value = "/organization/{id}")
    @I18n
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    public PciExternalSystemOrg getOrg(@PathVariable String id) {
        return externalSystemService.getOrgById(id);
    }

    //用户列表
    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_list" + Translator.SUFFIX)
    @PostMapping(value = "/allUsers")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    @I18n
    public List<PciExternalSystemUser> getUsers() {
        return externalSystemService.allUsers();
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_org_tree_list" + Translator.SUFFIX)
    @GetMapping(value = "/orgTreeList")
    @RequiresPermissions(PermissionConstants.SYNC_ORG_USER_READ)
    @I18n
    public List<PciExternalSystemOrgRep> orgTreeList() {
        return externalSystemService.orgTreeList(null, false);
    }

    @GetMapping("/getTopOrg")
    @I18n
    public String getTopOrg() {
        return externalSystemService.getTopOrgValue();
    }

    @PostMapping("/setTopOrg/{code}")
    @I18n
    public void setTopOrg(@PathVariable String code) {
        externalSystemService.setTopOrgValue(code);
    }
}

