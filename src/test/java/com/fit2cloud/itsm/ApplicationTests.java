package com.fit2cloud.itsm;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.CloudServer;
import com.fit2cloud.commons.server.constants.RabbitmqQueueConstants;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.rabbitmq.RabbitmqResourceMessage;
import com.fit2cloud.commons.server.process.ProcessEventContext;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.server.service.CloudServerCommonService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.integration.ITSMIntegration;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.common.utils.ProviderFactoryUtil;
import com.fit2cloud.itsm.dao.PciApiParameterMappingMapper;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import com.fit2cloud.itsm.service.impl.consume.ResourceConsume;
import com.fit2cloud.itsm.service.IProcessEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private ResourceConsume resourceConsume;
    @Resource
    private CloudServerCommonService cloudServerCommonService;
    @Resource
    private PciApiParameterMappingMapper parameterMappingMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Test
    public void contextLoads() {

    }

    @Test
    public void publish() {
        RabbitmqResourceMessage message = new RabbitmqResourceMessage(ResourceTypeConstants.VIRTUALMACHINE,
                ResourceOperation.CREATE,
                "46a7f0f7-52ec-46bb-9beb-a5f27c8c591b");
        rabbitTemplate.convertAndSend(RabbitmqQueueConstants.CLOUD_RESOURCE_QUEUE, message);
    }


    @Test
    public void testResourceConsume() {

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();

        // 要执行的Provider实现类
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
        if (pciApiAccountDTO == null) {
            LogUtil.error("未查询到启用的优云API账号，请检查！");
            return;
        }

        // 要执行的厂商
        ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());
//        ICMDBProvider provider = (ICMDBProvider) providerFactory.getProvider(ICMDBProvider.class, pciApiAccountDTO.getVersion());

        // 根据参数映射组织数据，调用第三方接口
        CloudServer cloudServer = cloudServerCommonService.get("00329504-a024-47db-9f39-3d91d33ffa37");

//        provider.onCloudServerChanged(cloudServer, ResourceOperation.CREATE);
    }
    @Test
    public void testResourceConsumeUpdate() {

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();

        // 要执行的Provider实现类
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
        if (pciApiAccountDTO == null) {
            LogUtil.error("未查询到启用的优云API账号，请检查！");
            return;
        }

        // 要执行的厂商
        ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());

        // 根据参数映射组织数据，调用第三方接口
        CloudServer cloudServer = cloudServerCommonService.get("531be5eb-b081-4141-98a8-e7d52e290def");

    }

    @Test
    public void testGetSourceFieldValue() {
        // PciApiParameterMapping parameterMapping, String primaryKey, String apiType
        PciApiParameterMapping parameterMapping = parameterMappingMapper.selectByPrimaryKey("PAPM-202206281343-99a4e90f");
//        cmdbProvider.getSourceFieldValue(parameterMapping, "00329504-a024-47db-9f39-3d91d33ffa37", "CMDB_CREATE");

    }


    @Test
    public void testItsmOnProcessEvent() {
        // 获取当前启用的 流程对接的 API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO == null) {
            LogUtil.error("未查询到启用的流程对接API账号，请检查！");
            return;
        }

        // 将获取到的API账号放入当前线程中
        IntegrationManager.getManager().set(new IntegrationManager());
        IntegrationManager integrationManager = IntegrationManager.getManager().get();
        integrationManager.setPciApiAccountDTO(pciApiAccountDTO);

        IProcessEventHandler handler = integrationManager.getProcessEventHandler();
        if (Objects.isNull(handler)) {
            throw new IllegalStateException(Translator.get("i18n_pcis_handler_is_null"));
        }

        // 组织数据
        // CREATE
//        BusinessEventContextDTO businessEventContextDTO = businessEventContextDTOByCREATE();
        // APPLY_VIRTUAL
        BusinessEventContextDTO businessEventContextDTO = businessEventContextDTOByVDI_CREATE();
        try {
            handler.onProcessEvent(businessEventContextDTO);
            return;
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return;
        } finally {
            // 清除数据，防止出现内存泄漏
            IntegrationManager.getManager().remove();
        }
    }

    public BusinessEventContextDTO businessEventContextDTOByCREATE(){
        String testJson = "{\"applicant\":\"admin\",\"businessDetails\":[{\"detailItems\":[{\"key\":\"resource_pool_tag_93ab0cb5-7a8e-48b4-9c0c-202fddfdff9a\",\"label\":\"hyperv资源池\",\"text\":\"hyperv测试\",\"value\":\"2188538f-2dac-434f-a1bc-c028eb4d9889\"},{\"key\":\"instanceTypeOptions\",\"label\":\"可选实例类型\",\"text\":\"vm.1c1g [1核1G];vm.2c2g [2核2G];vm.2c4g [2核4G];vm.4c8g [4核8G];vm.4c16g [4核16G];vm.8c16g [8核16G];vm.16c32g [16核32G];vm.32c64g [32核64G];\",\"value\":\"[12,13,14,15,16,17,18,19]\"},{\"key\":\"instanceType\",\"label\":\"实例类型\",\"text\":\"vm.1c1g [1核1G]\",\"value\":\"12\"},{\"key\":\"cpuCount\",\"label\":\"CPU\",\"text\":\"1\",\"value\":\"1\"},{\"key\":\"memory\",\"label\":\"内存大小\",\"text\":\"1024\",\"value\":\"1024\"},{\"key\":\"vmDes\",\"label\":\"虚机名称\",\"text\":\"test\",\"value\":\"test\"},{\"key\":\"diskSize\",\"label\":\"虚拟盘大小\",\"text\":\"300\",\"value\":\"300\"},{\"key\":\"hostname\",\"label\":\"hostname\",\"text\":\"test123\",\"value\":\"test123\"},{\"key\":\"loginPassword\",\"label\":\"登录密码\",\"text\":\"Calong@2015\",\"value\":\"Calong@2015\"},{\"key\":\"deploy_policy\",\"label\":\"放置策略\",\"text\":\"随机放置\",\"value\":\"RANDOM\"}]}],\"businessKey\":\"vm-201906251854-e182d06c\",\"createTime\":\"2019-06-25 18:54:41\",\"description\":\"\",\"processEventContext\":{\"businessKey\":\"vm-201906251854-e182d06c\",\"process\":{\"businessKey\":\"vm-201906251854-e182d06c\",\"businessType\":\"vm-service\",\"deployId\":\"2bcb5a7d-4b65-493e-b2fe-42a0a8d356f2\",\"module\":\"vm-service\",\"processCreator\":\"admin\",\"processEndTime\":1561460088663,\"processId\":\"8b205ea0-31fd-48a1-b481-4c4430f68a4a\",\"processName\":\"虚拟机申请流程\",\"processStartTime\":1561460082012,\"processStatus\":\"COMPLETED\",\"workspaceId\":\"64ecb79c-c339-485f-8fe4-162ac3ffb533\"},\"processId\":\"8b205ea0-31fd-48a1-b481-4c4430f68a4a\"},\"processName\":\"虚拟机申请流程\",\"resourceType\":\"VIRTUALMACHINE\",\"workspaceId\":\"\"}";
        BusinessEventContextDTO businessEventContextDTO = JSONObject.parseObject(testJson, BusinessEventContextDTO.class);
        businessEventContextDTO.setOrderType("CREATE");
        ProcessEventContext processEventContext = businessEventContextDTO.getProcessEventContext();
        processEventContext.setArguments("SUBMIT");
        return businessEventContextDTO;
    }

    public BusinessEventContextDTO businessEventContextDTOByAPPLY_VIRTUAL(){
        String testJson = "{\"applicant\":\"lijun.zhang\",\"businessDetails\":[{\"detailItems\":[{\"hidden\":false,\"key\":\"count\",\"label\":\"申请数量\",\"text\":\"1\",\"value\":\"1\"},{\"hidden\":false,\"key\":\"deviceId\",\"label\":\"设备名称\",\"text\":\"北京 F5（内网）不可切换为备机，切换备机后会出现数据丢失!\",\"value\":\"52e9e573-af67-45e7-9e42-a62c36c5e048\"},{\"hidden\":false,\"key\":\"part\",\"label\":\"分区\",\"text\":\"Common\",\"value\":\"52ae5952-2263-4100-94e9-a55a0ea4c479\"},{\"hidden\":false,\"key\":\"virtualName\",\"label\":\"Virtual Server 名称\",\"text\":\"zljtest_service_name_L7_Standard_10.11.225.236_443_vs\",\"value\":\"zljtest_service_name_L7_Standard_10.11.225.236_443_vs\"},{\"hidden\":false,\"key\":\"remark\",\"label\":\"备注\",\"text\":\"这是备注\",\"value\":\"这是备注\"},{\"hidden\":false,\"key\":\"vsIpEditable\",\"label\":\"Virtual Server IP分配策略\",\"text\":\"从IP池中选择\",\"value\":\"true\"},{\"hidden\":false,\"key\":\"vsPort\",\"label\":\"Virtual Server 端口\",\"text\":\"443\",\"value\":\"443\"},{\"hidden\":false,\"key\":\"vsAddress\",\"label\":\"Virtual Server 地址(从IP池中选择)\",\"text\":\"10.11.225.236\",\"value\":\"10.11.225.236\"},{\"hidden\":false,\"key\":\"bizName\",\"label\":\"业务名称\",\"text\":\"zljtest_service_name\",\"value\":\"zljtest_service_name\"},{\"hidden\":false,\"key\":\"vsType\",\"label\":\"Virtual Server 类型\",\"text\":\"标准(Standard)\",\"value\":\"standard\"},{\"hidden\":false,\"key\":\"protocol\",\"label\":\"协议\",\"text\":\"TCP\",\"value\":\"TCP\"},{\"hidden\":false,\"key\":\"clientSsl\",\"label\":\"选择SSL证书（Client）\",\"text\":\"carizon.work;\",\"value\":\"[\\\"carizon.work\\\"]\"},{\"hidden\":false,\"key\":\"serverSsl\",\"label\":\"选择SSL证书（Server）\",\"text\":\"[]\",\"value\":\"[]\"},{\"hidden\":false,\"key\":\"clientSslScope\",\"label\":\"选择SSL证书（Client）范围控制\",\"text\":\"baidu.ooo;carizon.com;carizon.work;\",\"value\":\"[\\\"baidu.ooo\\\",\\\"carizon.com\\\",\\\"carizon.work\\\"]\"},{\"hidden\":false,\"key\":\"serverSslScope\",\"label\":\"选择SSL证书（Server）范围控制\",\"text\":\"serverssl;\",\"value\":\"[\\\"serverssl\\\"]\"},{\"hidden\":false,\"key\":\"httpProfile\",\"label\":\"源地址信息保留\",\"text\":\"http\",\"value\":\"http\"},{\"hidden\":false,\"key\":\"SAT\",\"label\":\"源地址转换\",\"text\":\"Auto Map\",\"value\":\"automap\"},{\"hidden\":false,\"key\":\"snatPool\",\"label\":\"SNAT Pool\",\"text\":\"SNAT-133\",\"value\":\"SNAT-133\"},{\"hidden\":false,\"key\":\"rules\",\"label\":\"irules\",\"text\":\"VS-Standard-logs;\",\"value\":\"[\\\"VS-Standard-logs\\\"]\"},{\"hidden\":false,\"key\":\"persistenceProfile\",\"label\":\"会话保持\",\"text\":\"cookie\",\"value\":\"cookie\"},{\"hidden\":false,\"key\":\"persistenceScope\",\"label\":\"会话保持范围控制\",\"text\":\"[]\",\"value\":\"[]\"},{\"hidden\":false,\"key\":\"poolName\",\"label\":\"Pool名称\",\"text\":\"zljtest_service_name_10.11.225.236_443_pool\",\"value\":\"zljtest_service_name_10.11.225.236_443_pool\"},{\"hidden\":false,\"key\":\"monitorSetting\",\"label\":\"健康检查设置\",\"text\":\"使用已有健康检查\",\"value\":\"exist\"},{\"hidden\":false,\"key\":\"monitorName\",\"label\":\"健康检查名称\",\"text\":\"${monitorType}_${bizName}\",\"value\":\"${monitorType}_${bizName}\"},{\"hidden\":false,\"key\":\"monitorTypeScope\",\"label\":\"健康检查类型范围控制\",\"text\":\"http;https;tcp;udp;\",\"value\":\"[\\\"http\\\",\\\"https\\\",\\\"tcp\\\",\\\"udp\\\"]\"},{\"hidden\":false,\"key\":\"monitorType\",\"label\":\"健康检查类型\",\"text\":\"http\",\"value\":\"http\"},{\"hidden\":false,\"key\":\"sendString\",\"label\":\"Send String\",\"text\":\"GET /\\\\r\\\\n\",\"value\":\"GET /\\\\r\\\\n\"},{\"hidden\":false,\"key\":\"receiveString\",\"label\":\"Receive String\",\"text\":\"无\"},{\"hidden\":false,\"key\":\"interval\",\"label\":\"Interval\",\"text\":\"5\",\"value\":\"5\"},{\"hidden\":false,\"key\":\"timeout\",\"label\":\"timeout\",\"text\":\"16\",\"value\":\"16\"},{\"hidden\":false,\"key\":\"monitors\",\"label\":\"健康检查\",\"text\":\"http\",\"value\":\"http\"},{\"hidden\":false,\"key\":\"loadBalancingMode\",\"label\":\"负载均衡算法\",\"text\":\"Round Robin\",\"value\":\"round-robin\"},{\"hidden\":false,\"key\":\"priorityGroup\",\"label\":\"优先组\",\"text\":\"负载均衡方式\",\"value\":\"0\"},{\"hidden\":false,\"key\":\"nodeName\",\"label\":\"Node名称\",\"text\":\"member_192.168.11.11\",\"value\":\"member_192.168.11.11\"},{\"hidden\":false,\"key\":\"memberAddress\",\"label\":\"节点地>址\",\"text\":\"192.168.11.11\",\"value\":\"192.168.11.11\"},{\"hidden\":false,\"key\":\"fqdn\",\"label\":\"FQDN\",\"text\":\"无\"},{\"hidden\":false,\"key\":\"memberPort\",\"label\":\"服务端口\",\"text\":\"80\",\"value\":\"80\"},{\"hidden\":false,\"key\":\"ratio\",\"label\":\"权重\",\"text\":\"1\",\"value\":\"1\"},{\"hidden\":false,\"key\":\"newFqdn\",\"label\":\"新建FQDN\",\"text\":\"否\",\"value\":\"false\"},{\"hidden\":false,\"key\":\"nodeName\",\"label\":\"Node名称\",\"text\":\"member_192.168.111.111\",\"value\":\"member_192.168.111.111\"},{\"hidden\":false,\"key\":\"memberAddress\",\"label\":\"节点地址\",\"text\":\"192.168.111.111\",\"value\":\"192.168.111.111\"},{\"hidden\":false,\"key\":\"fqdn\",\"label\":\"FQDN\",\"text\":\"无\"},{\"hidden\":false,\"key\":\"memberPort\",\"label\":\"服务端口\",\"text\":\"80\",\"value\":\"80\"},{\"hidden\":false,\"key\":\"ratio\",\"label\":\"权重\",\"text\":\"1\",\"value\":\"1\"},{\"hidden\":false,\"key\":\"newFqdn\",\"label\":\"新建FQDN\",\"text\":\"否\",\"value\":\"false\"}]}],\"businessKey\":\"slb-202411182216-be6f70b3\",\"createTime\":\"2024-11-18 22:16:36\",\"description\":\"这是原因\",\"module\":\"loadbalancer-service\",\"orderType\":\"APPLY_VIRTUAL\",\"processEventContext\":{\"arguments\":\"SUBMIT\",\"businessKey\":\"slb-202411182216-be6f70b3\",\"process\":{\"businessKey\":\"slb-202411182216-be6f70b3\",\"businessType\":\"loadbalancer-service\",\"deployId\":\"00e5cef9-fd49-4868-ad9e-62e6e72a8710\",\"module\":\"loadbalancer-service\",\"processCreator\":\"lijun.zhang\",\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"processName\":\"i18n_apply_f5_listener\",\"processStartTime\":1731939396360,\"processStatus\":\"PENDING\",\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"},\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"task\":{\"businessKey\":\"slb-202411182216-be6f70b3\",\"businessType\":\"loadbalancer-service\",\"linkBusinessType\":\"APPROVE\",\"module\":\"loadbalancer-service\",\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"taskAction\":\"SUBMIT\",\"taskActionContent\":\"lijun.zhang提交了一个订单\",\"taskActivity\":\"a2d26895-d395-440a-b342-f401c53774a1\",\"taskAssignee\":\"lijun.zhang\",\"taskChildStep\":0,\"taskEndTime\":1731939396431,\"taskExecutor\":\"lijun.zhang\",\"taskId\":\"50b72453-6143-4b4a-af0a-158738721b23\",\"taskName\":\"i18n_process_submit\",\"taskRemarks\":\"i18n_process_submit\",\"taskStartTime\":1731939396431,\"taskStatus\":\"COMPLETED\",\"taskStep\":0,\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"}},\"processName\":\"i18n_apply_f5_listener\",\"resourceType\":\"LOAD_BALANCER\",\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"}";
        BusinessEventContextDTO businessEventContextDTO = JSONObject.parseObject(testJson, BusinessEventContextDTO.class);
        businessEventContextDTO.setOrderType("APPLY_VIRTUAL");
        ProcessEventContext processEventContext = businessEventContextDTO.getProcessEventContext();
        processEventContext.setArguments("SUBMIT");
        return businessEventContextDTO;
    }

    public BusinessEventContextDTO businessEventContextDTOByVDI_CREATE(){
       String testJson = "{\"applicant\":\"lijun.zhang\",\"businessDetails\":[{\"detailItems\":[{\"hidden\":false,\"key\":\"count\",\"label\":\"申请数量\",\"text\":\"1\",\"value\":\"1\"},{\"hidden\":false,\"key\":\"resource_pool_tag_7bd54a87-2fcf-49cc-8c15-10085898913c\",\"label\":\"Place\",\"text\":\"北京主机房\",\"value\":\"b2469b68-eae8-459e-8172-d7c12d988572\"},{\"hidden\":false,\"key\":\"resource_pool_tag_ea3863f5-df64-4b61-8faa-1adf06c493eb\",\"label\":\"Env\",\"text\":\"TEST\",\"value\":\"2e254b71-82e9-4677-9a30-5c7260afb799\"},{\"hidden\":false,\"key\":\"deploy_policy\",\"label\":\"資源池分配策略\",\"text\":\"随机分配\",\"value\":\"RANDOM\"},{\"hidden\":false,\"key\":\"userSelectResourcePoolIds\",\"label\":\"资源池\",\"text\":\"北京 Win11 CPU 资源池\",\"value\":\"045976b3-b355-4140-a57a-6063ec764eb5\"},{\"hidden\":false,\"key\":\"selectResourcePoolIds\",\"label\":\"资源池\",\"text\":\"北京 Win11 CPU 资源池;\",\"value\":\"[\\\"045976b3-b355-4140-a57a-6063ec764eb5\\\"]\"},{\"hidden\":false,\"key\":\"vmName\",\"label\":\"计算机名称\",\"text\":\"无\"},{\"hidden\":false,\"key\":\"instanceTypeOptions\",\"label\":\"可选实例类型\",\"text\":\"VDI.4C8GB [4核8G];VDI.8C16GB [8核16G];VDI.8C32GB [8核32G];VDI.16C32GB [16核32G];\",\"value\":\"[33,35,36,37]\"},{\"hidden\":false,\"key\":\"instanceType\",\"label\":\"实例类型\",\"text\":\"VDI.4C8GB [4核8G]\",\"value\":\"33\"},{\"hidden\":false,\"key\":\"cores\",\"label\":\"CPU核数\",\"text\":\"4\",\"value\":\"4\"},{\"hidden\":false,\"key\":\"memory\",\"label\":\"内存大小\",\"text\":\"8192\",\"value\":\"8192\"},{\"hidden\":false,\"key\":\"isMacBind\",\"label\":\"Mac绑定\",\"text\":\"否\",\"value\":\"false\"},{\"hidden\":false,\"key\":\"systemDiskSize\",\"label\":\"系统盘大小\",\"text\":\"100\",\"value\":\"100\"},{\"hidden\":false,\"key\":\"dataDiskSize\",\"label\":\"数据盘大小\",\"text\":\"0\",\"value\":\"0\"},{\"hidden\":false,\"key\":\"adUser\",\"label\":\"下发用户\",\"text\":\"lijun.zhang\",\"value\":\"lijun.zhang\"},{\"hidden\":false,\"key\":\"gpuType\",\"label\":\"GPU资源组\",\"text\":\"无\"},{\"hidden\":false,\"key\":\"namingPolicy\",\"label\":\"云桌面命名规则\",\"text\":\"BJCL0001\",\"value\":\"BJCL0001\"},{\"hidden\":false,\"key\":\"templateId\",\"label\":\"\",\"text\":\"i-00000203\",\"value\":\"i-00000203\"}]}],\"businessKey\":\"slb-202411182216-be6f70b3\",\"createTime\":\"2024-11-18 22:16:36\",\"description\":\"这是原因\",\"module\":\"loadbalancer-service\",\"orderType\":\"APPLY_VIRTUAL\",\"processEventContext\":{\"arguments\":\"SUBMIT\",\"businessKey\":\"slb-202411182216-be6f70b3\",\"process\":{\"businessKey\":\"slb-202411182216-be6f70b3\",\"businessType\":\"loadbalancer-service\",\"deployId\":\"00e5cef9-fd49-4868-ad9e-62e6e72a8710\",\"module\":\"loadbalancer-service\",\"processCreator\":\"lijun.zhang\",\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"processName\":\"i18n_apply_f5_listener\",\"processStartTime\":1731939396360,\"processStatus\":\"PENDING\",\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"},\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"task\":{\"businessKey\":\"slb-202411182216-be6f70b3\",\"businessType\":\"loadbalancer-service\",\"linkBusinessType\":\"APPROVE\",\"module\":\"loadbalancer-service\",\"processId\":\"9ff58028-7492-48f5-9df1-358d5b23aaf9\",\"taskAction\":\"SUBMIT\",\"taskActionContent\":\"lijun.zhang提交了一个订单\",\"taskActivity\":\"a2d26895-d395-440a-b342-f401c53774a1\",\"taskAssignee\":\"lijun.zhang\",\"taskChildStep\":0,\"taskEndTime\":1731939396431,\"taskExecutor\":\"lijun.zhang\",\"taskId\":\"50b72453-6143-4b4a-af0a-158738721b23\",\"taskName\":\"i18n_process_submit\",\"taskRemarks\":\"i18n_process_submit\",\"taskStartTime\":1731939396431,\"taskStatus\":\"COMPLETED\",\"taskStep\":0,\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"}},\"processName\":\"i18n_apply_f5_listener\",\"resourceType\":\"LOAD_BALANCER\",\"workspaceId\":\"f0f42898-cce3-4303-bb3a-ea287c7291dd\"}";
        BusinessEventContextDTO businessEventContextDTO = JSONObject.parseObject(testJson, BusinessEventContextDTO.class);
        businessEventContextDTO.setOrderType("VDI_CREATE");
        ProcessEventContext processEventContext = businessEventContextDTO.getProcessEventContext();
        processEventContext.setArguments("SUBMIT");
        return businessEventContextDTO;
    }

}
