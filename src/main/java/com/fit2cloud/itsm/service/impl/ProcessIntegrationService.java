package com.fit2cloud.itsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.ProcessEventContext;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.dao.PciApiRequestLogMapper;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.service.IProcessEventHandler;
import com.fit2cloud.itsm.model.PciApiRequestLog;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ProcessIntegrationService {

    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private PciApiRequestLogMapper pciApiRequestLogMapper;

    @Resource
    private MicroService microService;

    public String pushBusinessEventContext(BusinessEventContextDTO processEventContext) {
        String errMsg = "";

        // 获取当前启用的 流程对接的 API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO == null) {
            LogUtil.error("未查询到启用的流程对接API账号，请检查！");
            return errMsg;
        }

        // 将获取到的API账号放入当前线程中
        IntegrationManager.getManager().set(new IntegrationManager());
        IntegrationManager integrationManager = IntegrationManager.getManager().get();
        integrationManager.setPciApiAccountDTO(pciApiAccountDTO);

        IProcessEventHandler handler = integrationManager.getProcessEventHandler();
        if (Objects.isNull(handler)) {
            LogUtil.error(Translator.get("i18n_pcis_handler_is_null"));
            throw new IllegalStateException(Translator.get("i18n_pcis_handler_is_null"));
        }
        try {
            LogUtil.info("                ...... 事件处理中 ......");
            handler.onProcessEvent(processEventContext);
            return errMsg;
        } catch (Exception e) {
            LogUtil.info("ITSM流程推送异常：" + e.getMessage());
            return e.getMessage();
        } finally {
            // 清除数据，防止出现内存泄漏
            IntegrationManager.getManager().remove();
        }
    }

    public Boolean repush(String logId) {
        Boolean result = Boolean.FALSE;

        /*查询请求体数据*/
        if (StringUtils.isNotEmpty(logId)) {
            PciApiRequestLog pciApiRequestLog = pciApiRequestLogMapper.selectByPrimaryKey(logId);

            /*将请求体转换为 BusinessEventContextDTO*/
            if (pciApiRequestLog != null) {
                ProcessEventContext processEventContext = JSONObject.parseObject(pciApiRequestLog.getRequestBody(), ProcessEventContext.class);
                String moduleName = processEventContext.getProcess().getModule();
                ResultHolder resultHolder = microService.postForResultHolder(moduleName, "business/resolve", processEventContext);
                BusinessEventContextDTO processEventContextDTO = (BusinessEventContextDTO)resultHolder.getData();
                /*执行重推*/
                String errMsg = pushBusinessEventContext(processEventContextDTO);
                if (StringUtils.isNotEmpty(errMsg)) {
                    throw new RuntimeException(errMsg);
                }
                result = Boolean.TRUE;
            } else {
                throw new RuntimeException(Translator.get("i18n_pci_request_log_is_null"));
            }
        } else {
            throw new RuntimeException(Translator.get("i18n_pci_log_id_is_null"));
        }

        return result;
    }
}
