package com.fit2cloud.itsm.service.impl.consume;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.CloudHost;
import com.fit2cloud.commons.server.base.mapper.CloudEipMapper;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.CloudServerDTO;
import com.fit2cloud.commons.server.model.rabbitmq.RabbitmqResourceMessage;
import com.fit2cloud.commons.server.service.CloudServerCommonService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.pm.PhysicalMachineDTO;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.common.utils.ProviderFactoryUtil;
import com.fit2cloud.itsm.model.dto.CMDBBatchSyncDTO;
import com.fit2cloud.itsm.model.dto.CMDBProviderPrepare;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.model.fusionaccess.DesktopServerDTO;
import com.fit2cloud.itsm.model.loadbalancer.LbLtmVirtualRet;
import com.fit2cloud.itsm.service.impl.ResourceModuleService;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ResourceConsume {

    @Resource
    private IntegrationManager integrationManager;
    @Resource
    private CloudServerCommonService cloudServerCommonService;
    @Resource
    private CloudEipMapper cloudEipMapper;
    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private ResourceModuleService resourceModuleService;

    /*public void pushAllCmdb(RabbitmqResourceMessage resourceMessage) {

        ICMDBProvider provider = null;
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
            provider = (ICMDBProvider) providerFactory.getProvider(ICMDBProvider.class, pciApiAccountDTO.getVersion());

        } catch (Exception e) {
            LogUtil.error("pushAllCmdb  error: resource Message -> " + JSONObject.toJSONString(resourceMessage) + e.getMessage(), e);
        }

        // 执行消费逻辑
        if (provider == null){
            LogUtil.error("ICMDBProvider 版本 v1.0 的实现类不存在，请检查！");
            return;
        }

        switch (resourceMessage.getResourceType()) {
            // 虚拟机推送CMDB服务
            case VIRTUALMACHINE:  {
                executeSyncCloudServer(provider, resourceMessage.getResourceOperation());
                break;
            }
            case LOAD_BALANCER:{
                executeSyncF5(provider,resourceMessage.getResourceOperation());
                break;
            }
            case CLOUD_EIP: {
                break;
            }
            default:{
                String msg = "Not supported resource type: " + resourceMessage.getResourceType().name();
                LogUtil.error(msg);
            }
        }
    }*/


    /*public void pushAllCmdb() {
        // 要执行的Provider实现类
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
        if (pciApiAccountDTO == null) {
            F2CException.throwException(Translator.get("i18n_api_account_not_enable"));
        }
        pushSingleCmdb(pciApiAccountDTO);
    }*/

    /*public void pushSingleCmdb(PciApiAccountDTO pciApiAccountDTO) {

        try {
            apiAccountService.validate(pciApiAccountDTO.getId());
        } catch (Exception e) {
            F2CException.throwException(String.format(Translator.get("i18n_vm_sync_failed_cause"), String.format(Translator.get("i18n_you_yun_api_not_available"), pciApiAccountDTO.getProviderFactoryName(), pciApiAccountDTO.getName())));
        }
        if (!"1".equals(pciApiAccountDTO.getStatus())) {
            F2CException.throwException(String.format(Translator.get("i18n_you_yun_api_not_available"), pciApiAccountDTO.getProviderFactoryName(), pciApiAccountDTO.getName()));
        }

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();
        // 要执行的厂商
        ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());
        ICMDBProvider provider = (ICMDBProvider) providerFactory.getProvider(ICMDBProvider.class, pciApiAccountDTO.getVersion());

        if (provider  == null) {
            LogUtil.error("ICMDBProvider 版本 v1.0 的实现类不存在，请检查！");
            return;
        }

        try{
            // 执行消费逻辑 同步虚拟机
            executeSyncCloudServer(provider);
        }catch (Exception e){
            LogUtil.error("推送虚拟机失败：" + e.getMessage());
        }

        // 同步F5
        try{
            executeSyncF5(provider, ResourceOperation.SYNC);
        }catch (Exception e){
            LogUtil.error("推送F5失败：" + e.getMessage());
        }

        // 同步云桌面
        try{
            executeSyncCloudDesktop(provider, ResourceOperation.SYNC);
        }catch (Exception e){
            LogUtil.error("推送云桌面失败：" + e.getMessage());
        }

        // 同步物理机
        try{
            executeSyncPhysicalMachine(provider, ResourceOperation.SYNC);
        }catch (Exception e){
            LogUtil.error("推送物理机失败：" + e.getMessage());
        }

        pciApiAccountDTO.setSyncStatus("END");
        apiAccountService.updatePciApiAccount(pciApiAccountDTO);
    }*/

    /*private void executeSyncCloudDesktop(ICMDBProvider provider, String resourceOperation) {
        List<DesktopServerDTO> desktopServerlist = resourceModuleService.getDesktopServerList();
        for (DesktopServerDTO desktopServer : desktopServerlist) {
            try {
                provider.onCloudDesktopChanged(desktopServer, resourceOperation);
            } catch (Exception exception) {
                LogUtil.error("推送云桌面失败：" + exception.getMessage() + "； 信息：" + JSONObject.toJSONString(desktopServer));
            }
        }
    }*/

    /*private void executeSyncF5(ICMDBProvider provider, String resourceOperation) {
        List<LbLtmVirtualRet> f5ServerList = resourceModuleService.getF5ServerList();
        for (LbLtmVirtualRet lbLtmVirtualRet : f5ServerList) {
            try {
                provider.onLoadbalancerChanged(lbLtmVirtualRet, resourceOperation);
            } catch (Exception exception) {
                LogUtil.error("推送F5虚拟服务器失败：" + exception.getMessage() + "； 信息：" + JSONObject.toJSONString(lbLtmVirtualRet));
            }
        }
    }*/

    /*private void executeSyncPhysicalMachine(ICMDBProvider provider, String resourceOperation) {
        List<PhysicalMachineDTO> physicalMachineList = resourceModuleService.getPhysicalMachineList();
        for (PhysicalMachineDTO physicalMachine : physicalMachineList) {
            try {
                provider.onPhysicalMachineChanged(physicalMachine, resourceOperation);
            } catch (Exception exception) {
                LogUtil.error("推送F5虚拟服务器失败：" + exception.getMessage() + "； 信息：" + JSONObject.toJSONString(physicalMachine));
            }
        }
    }*/

    /*private void executeSyncCloudServer(ICMDBProvider provider) {
        executeSyncCloudServer(provider, ResourceOperation.SYNC);
    }*/

    /*private void executeSyncCloudServer(ICMDBProvider provider, String operation) {

        Map<String, Object> params = new HashMap<>();
        List<String> instanceStatus = new ArrayList<>();
        instanceStatus.add("Running");
        instanceStatus.add("Stopped");
        params.put("instanceStatus", instanceStatus);

        List<CloudServerDTO> cloudServerDTOS = cloudServerCommonService.selectCloudServerList(params);
        // 如果支持【先组织数据，再全量推送】

        if (provider.isSupportFastSync()) {
            // 根据参数映射组织数据，调用第三方接口
            // 1.整理接口调用的验证信息
            CMDBProviderPrepare cmdbProviderPrepare = provider.prepareEnv(operation);
            // 2.整理虚拟机数据
            List<CMDBBatchSyncDTO> cmdbBatchSyncDTOList = new ArrayList<>();
            Optional.ofNullable(cloudServerDTOS).orElse(new ArrayList<>()).forEach(cloudServerDTO -> {
                try {
                    JSONObject editCMDBResourceReq = provider.prepareDataForVmSync(cloudServerDTO, operation, cmdbProviderPrepare);
                    CMDBBatchSyncDTO cmdbBatchSyncDTO = new CMDBBatchSyncDTO();
                    cmdbBatchSyncDTO.setEditCMDBResourceReq(editCMDBResourceReq);
                    cmdbBatchSyncDTO.setCloudServer(cloudServerDTO);
                    cmdbBatchSyncDTOList.add(cmdbBatchSyncDTO);
                } catch (Exception exception) {
                    LogUtil.error("组织虚拟机推送数据失败：" + exception.getMessage() + "； 虚拟机信息：" + JSONObject.toJSONString(cloudServerDTO));
                }
            });
            // 3.与CMDB交互
            for (CMDBBatchSyncDTO cmdbBatchSyncDTO : cmdbBatchSyncDTOList) {
                try {
                    provider.communicateToCMDBForVmSync(cmdbBatchSyncDTO.getCloudServer(), operation, cmdbProviderPrepare, cmdbBatchSyncDTO.getEditCMDBResourceReq());
                } catch (Exception exception) {
                    LogUtil.error("推送虚拟机失败：" + exception.getMessage() + "； 虚拟机信息：" + JSONObject.toJSONString(cmdbBatchSyncDTO.getCloudServer()));
                }
            }
        } else {
            // 不支持【先组织数据，再全量推送】
            // 根据参数映射组织数据，调用第三方接口
            Optional.ofNullable(cloudServerDTOS).orElse(new ArrayList<>()).stream().forEach(cloudServerDTO -> {
                try {
                    provider.onCloudServerChanged(cloudServerDTO, operation);
                } catch (Exception exception) {
                    LogUtil.error("推送虚拟机失败：" + exception.getMessage() + "； 虚拟机信息：" + JSONObject.toJSONString(cloudServerDTO));
                }
            });
        }
    }*/


    /*public void syncHost() {
        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();

        // 要执行的Provider实现类
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
        try {
            apiAccountService.validate(pciApiAccountDTO.getId());
        } catch (Exception e) {
            F2CException.throwException(String.format(Translator.get("i18n_host_sync_failed_cause"), String.format(Translator.get("i18n_you_yun_api_not_available"), pciApiAccountDTO.getProviderFactoryName(), pciApiAccountDTO.getName())));
        }

        if (pciApiAccountDTO == null) {
            LogUtil.error("未查询到启用的优云API账号，请检查！");
            return;
        }
        if (!"1".equals(pciApiAccountDTO.getStatus())) {
            F2CException.throwException(String.format(Translator.get("i18n_you_yun_api_not_available"), pciApiAccountDTO.getProviderFactoryName(), pciApiAccountDTO.getName()));
        }

        // 要执行的厂商
        ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());
        ICMDBProvider provider = (ICMDBProvider) providerFactory.getProvider(ICMDBProvider.class, pciApiAccountDTO.getVersion());

        // 执行消费逻辑
        if (provider != null) {
            // 根据参数映射组织数据，调用第三方接口
            Map<String, Object> params = new HashMap<>();

            List<CloudHost> cloudHostList = cloudServerCommonService.selectCloudHostList(params);
            Optional.ofNullable(cloudHostList).orElse(new ArrayList<>()).forEach(cloudHost -> {
                try {
                    provider.onCloudHostChanged(cloudHost, ResourceOperation.SYNC);
                } catch (Exception exception) {
                    LogUtil.error("同步宿主机失败：" + exception.getMessage() + "； 宿主机信息：" + JSONObject.toJSONString(cloudHost));
                }
            });
        } else {
            LogUtil.error("ICMDBProvider 版本 v1.0 的实现类不存在，请检查！");
        }
    }*/
}
