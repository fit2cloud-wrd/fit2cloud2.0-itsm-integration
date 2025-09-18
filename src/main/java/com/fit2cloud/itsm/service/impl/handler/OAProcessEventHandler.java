package com.fit2cloud.itsm.service.impl.handler;

import com.fit2cloud.commons.server.constants.ProcessEventArgumentsConstants;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.service.IProcessEventHandler;
import com.google.gson.Gson;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.fit2cloud.commons.server.constants.ProcessEventArgumentsConstants.valueOf;

@Component
public class OAProcessEventHandler implements IProcessEventHandler {

    @Lazy
    @Resource
    private IntegrationManager integrationManager;

    @Override
    public void onProcessEvent(BusinessEventContextDTO businessEventContextDTO) {
        ProcessEventArgumentsConstants arg = valueOf(businessEventContextDTO.getProcessEventContext().getArguments());
        integrationManager = IntegrationManager.getManager().get();

        LogUtil.info("----------------- 选择处理器[OAProcessEventHandler] -----------------");
        LogUtil.info("         Arg: " + arg);
        LogUtil.info("         BusinessEventContextDTO: " + new Gson().toJson(businessEventContextDTO));

        switch (arg) {
            case SUBMIT:
                // 此处调用 流程平台 接口将 CMP 流程发送给 流程平台
                integrationManager.getITSMService().onCmpProcessSubmit(businessEventContextDTO);
                break;
            case BUSINESS_FINISHED:
                integrationManager.getITSMService().onCmpBusinessComplete(businessEventContextDTO);
                break;
            case PENDING:
            case APPROVED:
            case REJECTED:
            case COMPLETE:
            case BUSINESS_WARN:
            case BUSINESS_ERROR:
            default:
                throw new IllegalStateException("请修改事件配置，流程 对接不支持此事件参数: " + arg);
        }
    }

}
