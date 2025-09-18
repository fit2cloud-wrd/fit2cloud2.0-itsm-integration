package com.fit2cloud.itsm.controller;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.service.impl.ProcessIntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping(value = "/process")
public class ProcessIntegrationController {
    @Resource
    private ProcessIntegrationService processIntegrationService;

    @PostMapping(value = "on/event")
    public ResultHolder onProcessEvent(@RequestBody BusinessEventContextDTO processEventContext) {
        LogUtil.info("【onProcessEvent】监听到新的事件 ......");
        LogUtil.info("                processEventContext:" + processEventContext.getBusinessKey() + " - " + processEventContext.getProcessName());
        String errmsg = processIntegrationService.pushBusinessEventContext(processEventContext);
        LogUtil.info("result:" + errmsg);
        LogUtil.info("【onProcessEvent】...... 事件处理完成！");
        if (StringUtils.isNotEmpty(errmsg)) {
            return ResultHolder.error(errmsg);
        }

        return ResultHolder.success(null);
    }

    /**
     * 流程重新推送
     */
    @PostMapping("repush/{logid}")
    @RequiresPermissions(PermissionConstants.DOCKING_LOG_PUSH)
    public Boolean repush(@PathVariable String logid) {
        LogUtil.info("【onProcessEvent】流程重新推送 ......");
        Boolean repush = processIntegrationService.repush(logid);
        LogUtil.info("【onProcessEvent】...... " + repush + " 流程重新推送完成！");
        return repush;
    }
}
