package com.fit2cloud.itsm.sample;

import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.service.impl.handler.EsbProcessEventHandler;
import com.fit2cloud.itsm.service.impl.handler.OAProcessEventHandler;
import com.fit2cloud.itsm.service.ITSMService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 流程对接配置示例，请在要使用的配置方法上添加 @Bean 注解
 * 方法名必须是 integrationManager
 */
@Configuration
public class IntegrationConfigSample {

    /**
     * 使用 OA 流程时，参考此配置
     * @param defaultProcessService
     * @param processEventHandler
     * @return
     */
    @Bean
    public IntegrationManager integrationManager(ITSMService defaultProcessService, OAProcessEventHandler processEventHandler){
        IntegrationManager integrationManager = new IntegrationManager();
//        integrationManager.setProcessService(defaultProcessService);
//        integrationManager.setProcessEventHandler(processEventHandler);
//        integrationManager.setOaClient(new OAClientSample());
        // 在 CMP 中调用接口创建工单的对接方式
//        integrationManager.setOaIntegrationType(OAIntegrationType.CreateExternalOrderWhenSubmit);
        // 在 OA 系统创建工单后跳转 CMP 申请的对接方式
//         integrationManager.setOaIntegrationType(OAIntegrationType.ExternalOrderReadyBeforeSubmit);
//         integrationManager.setCmdbClient(new CMDBClientSample());
        return integrationManager;
    }

    /**
     * 使用 ESB 待办通知时，参考此配置
     * @param defaultProcessService
     * @param esbProcessEventHandler
     * @return
     */
    public IntegrationManager integrationManager(ITSMService defaultProcessService, EsbProcessEventHandler esbProcessEventHandler){
        IntegrationManager integrationManager = new IntegrationManager();
//        integrationManager.setProcessService(defaultProcessService);
//        integrationManager.setProcessEventHandler(esbProcessEventHandler);
//        integrationManager.setEsbClient(new ESBClientSample());
//        integrationManager.setCmdbClient(new CMDBClientSample());
        return integrationManager;
    }
}
