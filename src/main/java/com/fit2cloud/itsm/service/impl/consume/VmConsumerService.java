package com.fit2cloud.itsm.service.impl.consume;


import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.CloudServer;
import com.fit2cloud.commons.server.base.mapper.CloudEipMapper;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.model.rabbitmq.RabbitmqResourceMessage;
import com.fit2cloud.commons.server.service.CloudServerCommonService;
import com.fit2cloud.commons.server.service.strategy.ResourceQueueConsumerAbstractStrategy;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.integration.ITSMIntegration;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.common.utils.ProviderFactoryUtil;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 虚拟机消息处理
 */
@Service
@Slf4j
public class VmConsumerService extends ResourceQueueConsumerAbstractStrategy {

    @Resource
    private IntegrationManager integrationManager;
    @Resource
    private CloudServerCommonService cloudServerCommonService;
    @Resource
    private CloudEipMapper cloudEipMapper;
    @Resource
    private ApiAccountService apiAccountService;

    @Override
    public ResourceTypeConstants getResourceType() {
        return ResourceTypeConstants.VIRTUALMACHINE;
    }

    @Override
    public void insert(RabbitmqResourceMessage message) {
        consume(message);
    }

    @Override
    public void delete(RabbitmqResourceMessage message) {
        consume(message);
    }

    @Override
    public void update(RabbitmqResourceMessage message) {
        consume(message);
    }

    @Override
    public void permissionUpdate(RabbitmqResourceMessage message) {
    }


    public void consume(RabbitmqResourceMessage resourceMessage) {
        try {
            Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();

            // 要执行的Provider实现类
            PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
            if (pciApiAccountDTO == null) {
                LogUtil.error("未查询到启用API账号，请检查！");
                return;
            }

            // 要执行的厂商
            ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());
//            ICMDBProvider provider = (ICMDBProvider) providerFactory.getProvider(ICMDBProvider.class, pciApiAccountDTO.getVersion());

            // 执行消费逻辑
//            if (provider == null) {
//                LogUtil.error("ICMDBProvider 版本 v1.0 的实现类不存在，请检查！");
//                return;
//            }

            // 根据参数映射组织数据，调用第三方接口
            CloudServer cloudServer = cloudServerCommonService.get(resourceMessage.getResourceId());
            Asserts.notNull(cloudServer, "CloudServer with ID:" + resourceMessage.getResourceId());
//            provider.onCloudServerChanged(cloudServer, resourceMessage.getResourceOperation());
        } catch (Exception e) {
            LogUtil.error("RabbitMQ consume error: resource Message -> " + JSONObject.toJSONString(resourceMessage) + e.getMessage(), e);
        }
    }


}

