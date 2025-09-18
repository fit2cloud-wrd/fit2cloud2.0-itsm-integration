package com.fit2cloud.itsm.controller;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.rabbitmq.RabbitmqResourceMessage;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.service.impl.consume.ResourceConsume;
import com.fit2cloud.itsm.common.constants.CMDBConstants;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.model.request.ExternalSystemSyncDetailRequest;
import com.fit2cloud.itsm.model.request.PciApiLogRequest;
import com.fit2cloud.itsm.model.response.ExternalSystemSyncDetailLogRep;
import com.fit2cloud.itsm.model.response.PciApiLogRep;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.fit2cloud.itsm.service.sync.ExternalSystemSyncService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RequestMapping("api/log")
@RestController
public class ApiLogController {

    @Resource
    private ApiLogService apiLogService;

    @Resource
    private ResourceConsume resourceConsume;

    @Resource
    private ExternalSystemSyncService externalSystemSyncService;

    @Resource
    private ResourceConsume consume;

//    @PostMapping("/pushAllCmdb")
//    public Object pushAllCmdb(@RequestBody RabbitmqResourceMessage resourceMessage) {
//        consume.pushAllCmdb(resourceMessage);
//        return "ok";
//    }

    /**
     * 查询日志列表
     */
    @I18n
    @RequestMapping(value = "/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public Pager<List<PciApiLogRep>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciApiLogRequest apiLogRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiLogService.getApiLogList(apiLogRequest));
    }

    @GetMapping("count/inPush/{systemType}")
    @ApiOperation("推送中资源")
    @I18n(I18nConstants.CLUSTER)
    public int countInPushLogs(@PathVariable String systemType) {
        return apiLogService.countInPushLogs(systemType);
    }

    @GetMapping("detail/{logId}")
    @I18n(I18nConstants.CLUSTER)
    public String getLogMessage(@PathVariable String logId) {
        return apiLogService.getLogMessage(logId);
    }

    @GetMapping("/pushAll")
    @I18n
    public Object pushAll() {
        LogUtil.info("Begin Syncing By Manual ::: {}", new Date());
        ResultHolder resultHolder = new ResultHolder();
        String jmsStatus = apiLogService.getValue("cmdb.pushALl");
        if (CMDBConstants.CMDB_PUSH_ALL.equals(jmsStatus)) {
            resultHolder.setSuccess(false);
            resultHolder.setMessage(Translator.toI18nKey("i18n_ctl_push_job_exist"));
            LogUtil.error("Begin With Manual Failed , Cause a Push Job is already Running  ::: {}", new Date());
            return resultHolder;
        }
        try {
            apiLogService.updateSyncStatus("cmdb.pushALl", CMDBConstants.CMDB_PUSH_ALL);
            // 推送虚拟机
//            resourceConsume.pushAllCmdb();
            apiLogService.updateSyncStatus("cmdb.pushALl", CMDBConstants.CMDB_PUSH_ALL_SUCCESS);
        } catch (Exception e) {
            apiLogService.updateSyncStatus("cmdb.pushALl", CMDBConstants.CMDB_PUSH_ALL_FAILED);
            LogUtil.error("Begin With Manual Error :{}", ExceptionUtils.getStackTrace(e));
            resultHolder.setSuccess(false);
            resultHolder.setMessage(e.getMessage());
        }
        LogUtil.info("Begin Syncing By Manual Finished ::: {}", new Date());
        return resultHolder;
    }

    @GetMapping("/pushAll/status")
    @I18n
    public String status() {
        return apiLogService.getValue("cmdb.pushALl");
    }

    @GetMapping("/syncHost")
    @I18n
    public Object syncHost() {
        LogUtil.info("Begin Syncing By Manual ::: {}", new Date());
        ResultHolder resultHolder = new ResultHolder();
        String jmsStatus = apiLogService.getValue("cmdb.syncHost");
        if (CMDBConstants.CMDB_PUSH_ALL.equals(jmsStatus)) {
            resultHolder.setSuccess(false);
            resultHolder.setMessage(Translator.toI18nKey("i18n_sync_host_job_exist"));
            LogUtil.error("Begin With Manual Failed , Cause a Sync Host Job is already Running  ::: {}", new Date());
            return resultHolder;
        }
        try {
            apiLogService.updateSyncStatus("cmdb.syncHost", CMDBConstants.CMDB_PUSH_ALL);
            // 推送虚拟机
//            resourceConsume.syncHost();
            apiLogService.updateSyncStatus("cmdb.syncHost", CMDBConstants.CMDB_PUSH_ALL_SUCCESS);
        } catch (Exception e) {
            apiLogService.updateSyncStatus("cmdb.syncHost", CMDBConstants.CMDB_PUSH_ALL_FAILED);
            LogUtil.error("Begin With Manual Error :{}", ExceptionUtils.getStackTrace(e));
            resultHolder.setSuccess(false);
            resultHolder.setMessage(e.getMessage());
        }
        LogUtil.info("Begin Syncing By Manual Finished ::: {}", new Date());
        return resultHolder;
    }

    @GetMapping("/syncHost/status")
    @I18n
    public String getSyncHostStatus() {
        return apiLogService.getValue("cmdb.syncHost");
    }

    @RequestMapping("syncDetail/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.DOCKING_LOG_READ)
    public Pager<List<ExternalSystemSyncDetailLogRep>> getSyncDetailLogs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ExternalSystemSyncDetailRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiLogService.getSyncDetailLogs(request));
    }


    @GetMapping("/syncAll")
    @I18n
    public Object syncAll() {
        LogUtil.info("Begin Syncing By Manual ::: {}", new Date());
        ResultHolder resultHolder = new ResultHolder();
        String jmsStatus = apiLogService.getValue("cmdb.syncAll");
        if (CMDBConstants.CMDB_PUSH_ALL.equals(jmsStatus)) {
            resultHolder.setSuccess(false);
            resultHolder.setMessage(Translator.toI18nKey("i18n_ctl_push_job_exist"));
            LogUtil.error("Begin With Manual Failed , Cause a Push Job is already Running  ::: {}", new Date());
            return resultHolder;
        }
        try {
            apiLogService.updateSyncStatus("cmdb.syncAll", CMDBConstants.CMDB_PUSH_ALL);
            // 开始同步
            externalSystemSyncService.syncOrgAndUser();
            apiLogService.updateSyncStatus("cmdb.syncAll", CMDBConstants.CMDB_PUSH_ALL_SUCCESS);
        } catch (Exception e) {
            apiLogService.updateSyncStatus("cmdb.syncAll", CMDBConstants.CMDB_PUSH_ALL_FAILED);
            LogUtil.error("Begin With Manual Error :{}", ExceptionUtils.getStackTrace(e));
            resultHolder.setSuccess(false);
            resultHolder.setMessage(e.getMessage());
        }
        LogUtil.info("Begin Syncing By Manual Finished ::: {}", new Date());
        return resultHolder;
    }

    @GetMapping("/syncAll/status")
    @I18n
    public String syncAllStatus() {
        return apiLogService.getValue("cmdb.syncAll");
    }
}

