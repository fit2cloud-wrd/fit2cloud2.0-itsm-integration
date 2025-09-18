package com.fit2cloud.itsm.controller;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.model.dto.ProcessRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("api/process")
@RestController
public class ProcessApiController {

    @Resource
    private IntegrationManager integrationManager;

    @PostMapping("complete")
    public ResultHolder processComplete(@RequestBody ProcessRequest request){
        LogUtil.info("itsm审批通过接口收到的请求参数ProcessRequest："+ JSONObject.toJSONString(request));
        integrationManager.getITSMService().onOAProcessComplete(request.getExternalProcessId(), request.getRemark(), request.getAssign());
        return ResultHolder.success(null);
    }

    @PostMapping("reject")
    public ResultHolder processReject(@RequestBody ProcessRequest request){
        LogUtil.info("itsm审批拒绝接口收到的请求参数ProcessRequest："+ JSONObject.toJSONString(request));
        integrationManager.getITSMService().onOAProcessReject(request.getExternalProcessId(), request.getRemark(), request.getAssign());
        return ResultHolder.success(null);
    }

}
