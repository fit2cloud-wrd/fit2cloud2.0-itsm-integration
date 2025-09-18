package com.fit2cloud.itsm.integration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.TagMapper;
import com.fit2cloud.commons.server.base.mapper.TagMappingMapper;
import com.fit2cloud.commons.server.base.mapper.TagValueMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.CommonOrganizationDTO;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.process.dto.BusinessDetail;
import com.fit2cloud.commons.server.process.dto.BusinessDetailItem;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.server.service.SystemCommonParameterService;
import com.fit2cloud.commons.server.service.TagService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.itsm.common.utils.ReadConfigFileUtil;
import com.fit2cloud.itsm.common.utils.SpringContextUtils;
import com.fit2cloud.itsm.common.utils.SyncExternalOrgAndUserUtils;
import com.fit2cloud.itsm.common.constants.ApiOriginOrderField;
import com.fit2cloud.itsm.common.constants.ApiSupportVersion;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import com.fit2cloud.itsm.model.dto.*;
import com.fit2cloud.itsm.remote.BKCoITSMClient;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;



@Component
public class BKCompanyIntegration implements ITSMIntegration {

    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ApiLogService apiLogService;
    @Autowired
    private SystemCommonParameterService systemCommonParameterService;
    @Resource
    private TagService tagService;
    @Resource
    private TagMappingMapper tagMappingMapper;
    @Resource
    private TagValueMapper tagValueMapper;
    @Resource
    private TagMapper tagMapper;
    @Autowired
    private BKCoITSMClient bKCoITSMClient;



    @Override
    public String getId() {
        return "LANJINGCOMPANY";
    }

    @Override
    public String getName() {
        return "蓝鲸CMDB(企业版)";
    }

    @Override
    public String getVersion() {
        return ApiSupportVersion.BKCompany.V2.getVersion();
    }

    @Override
    public List<String> getSupportVersion() {
        List<String> versionList = new ArrayList<>();
        for (ApiSupportVersion.LANJINGCOMPANY value : ApiSupportVersion.LANJINGCOMPANY.values()) {
            versionList.add(value.getVersion());
        }
        return versionList;
    }

    @Override
    public String getDefaultAPIEndpoint() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return pciApiAccountDTO.getApiEndpoint();
        }
        return null;
    }

    @Override
    public String testDefaultAPIEndpoint() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return pciApiAccountDTO.getTestApiEndpoint();
        }
        return null;
    }

    @Override
    public List<PciApiEndpointDTO> getDefaultApiList() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return apiAccountService.getApiListByAccountId(pciApiAccountDTO.getId());
        }
        return null;
    }

    @Override
    public String testDefaultApiUrl() {
        return "api/c/compapi/v2/cc";
    }

    @Override
    public String getDefaultApiListJson() {
        return ReadConfigFileUtil.readConfigFile("BKCompanyApiList.json");
    }

    /**
     * 获取账号认证信息
     */
    @Override
    public List<APICredentialItem> getCredentialTemplate() {
        List<APICredentialItem> credentialItems = new ArrayList<>();

        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());

        if (pciApiAccountDTO != null) {
            String credential = pciApiAccountDTO.getCredential();
            JSONArray jsonArray = JSONArray.parseArray(credential);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                APICredentialItem item = new APICredentialItem();
                item.setName(jsonObject.getString("name"));
                item.setLabel(jsonObject.getString("label"));
                if (StringUtils.equalsAnyIgnoreCase(jsonObject.getString("type"), "text", "password")) {
                    item.setType(APICredentialItem.InputType.text);
                } else {
                    item.setType(APICredentialItem.InputType.number);
                }
                item.setDefaultValue(jsonObject.getString("defaultValue"));
                credentialItems.add(item);
            }
        }

        return credentialItems;
    }

    @Override
    public String getCreditialJson() {
        return ReadConfigFileUtil.readConfigFile("BKCompanyCredential.json");
    }


    @Override
    public <T extends ITSMIntegration> T getProvider(Class<T> clazz, String version) {

        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(ITSMIntegration.class.getPackage().getName())
                    .addScanners(new SubTypesScanner()));

            Set<Class<? extends ITSMIntegration>> implClass = reflections.getSubTypesOf(ITSMIntegration.class);
            for (Class<? extends ITSMIntegration> subClass : implClass) {
                Class<?> cls = Class.forName(subClass.getName());

                Method getVersionMethod = subClass.getDeclaredMethod("getVersion");
                String clsVersion = (String) getVersionMethod.invoke(cls.newInstance());
                if (StringUtils.equals(clsVersion, version)) {
                    return (T) SpringContextUtils.getBean(subClass);
                }
            }
        } catch (Exception exception) {
        }

        return null;
    }

    @Override
    public void putOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口发送流程详情
//        queryApiInfoAndPushITSM("UNCHECKED", externalProcessId, businessEventContextDTO);
    }

    // 调用外部接口创建流程，返回外部系统流程 ID
    @Override
    public String takeOneItsm(BusinessEventContextDTO businessEventContextDTO) {
        String re = "unknown";

        // 认证信息
        List<APICredentialItem> credentialItems = this.getCredentialTemplate();
        // 校验认证信息
        Map<String, String> authMap = credentialItems.stream().collect(Collectors.toMap(APICredentialItem::getName, APICredentialItem::getDefaultValue, (v1, v2) -> v1));
        if (MapUtils.isEmpty(authMap)) {
            LogUtil.info("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
            F2CException.throwException("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
        }

        // 接口列表
        List<PciApiEndpointDTO> apiEndpointDTOS = this.getDefaultApiList();
        // 过滤 ApiType.API_TYPE
        String operation = businessEventContextDTO.getOrderType();
        List<PciApiEndpointDTO> apiList = apiEndpointDTOS.stream()
                .filter((endpoint -> endpoint.getApiType().equals(ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + businessEventContextDTO.getOrderType())))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(apiList)) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口");
            F2CException.throwException("CMDB资源接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口，请检查");
        }
        if (apiList.size() > 1) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口");
            F2CException.throwException("CMDB资源接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口，请检查");
        }
        PciApiEndpointDTO apiEndpointDTO = apiList.get(0);

        // 插入日志
        String apiLogId = apiLogService.packageAndInsertApiLog(
                apiEndpointDTO.getId(),
                apiEndpointDTO.getMethod(),
                "VM",
                businessEventContextDTO.getBusinessKey(),
                businessEventContextDTO.getResourceType().getResourceName(),
                "VIRTUALMACHINE",
                businessEventContextDTO.getWorkspaceId()
        );
        // 记录请求体信息
        apiLogService.insertApiRequestLog(apiLogId, JSONObject.toJSONString(businessEventContextDTO.getProcessEventContext()));

        BKCoITSMOrderResp bKCoITSMOrderResp = null;
        // 组织ITSM流程单数据
        BKCoITSMOrderReq itsmOrder = buildITSMOrderModel(authMap, businessEventContextDTO, apiEndpointDTO);

        LogUtil.info("==【调用蓝鲸API创建流程单】=================================================================================");
        try {
            URI uri = new URI(apiEndpointDTO.getApiEndpoint() + "" + apiEndpointDTO.getEndpoint());
            LogUtil.info("  Url：" + uri.getPath());
            LogUtil.info("  Body：" + new Gson().toJson(itsmOrder));
            bKCoITSMOrderResp = bKCoITSMClient.createProcess(uri, itsmOrder);
            LogUtil.info("  Success：" + new Gson().toJson(bKCoITSMOrderResp));
        } catch (URISyntaxException e) {
            LogUtil.info("  Failed：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.info("  Failed：" + e.getMessage());
            e.printStackTrace();
        }
        LogUtil.info("=========================================================================================================");

        if(Objects.isNull(bKCoITSMOrderResp) || !bKCoITSMOrderResp.isResult()) {
            // 更新日志状态
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), bKCoITSMOrderResp.getMessage());
        } else {
            re = bKCoITSMOrderResp.getData().getSn();
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.SUCESS.getCode(), "ProcessId: " + re);
        }

        return re;
    }

    /*
    * 订单完成回调ITSM
    * */
    @Override
    public void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        LogUtil.info("completeProcess 工单号："+externalProcessId);
        //1、查询当前工单详情；2、更新当前工单状态
        // 认证信息
        List<APICredentialItem> credentialItems = this.getCredentialTemplate();
        // 校验认证信息
        Map<String, String> authMap = credentialItems.stream().collect(Collectors.toMap(APICredentialItem::getName, APICredentialItem::getDefaultValue, (v1, v2) -> v1));
        if (MapUtils.isEmpty(authMap)) {
            LogUtil.info("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
            F2CException.throwException("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
        }

        // 接口列表
        List<PciApiEndpointDTO> apiEndpointDTOS = this.getDefaultApiList();
        // 过滤 ApiType.API_TYPE
        String operation = businessEventContextDTO.getOrderType();
        List<PciApiEndpointDTO> apiList = apiEndpointDTOS.stream()
                .filter((endpoint -> endpoint.getApiType().equals(ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" +ApiType.API_TYPE.PROCESS_GET_TICKET_INFO.getApiType())))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(apiList)) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有 PROCESS_GET_TICKET_INFO 接口");
            F2CException.throwException("ITSM 接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有 PROCESS_GET_TICKET_INFO 接口，请检查");
        }
        if (apiList.size() > 1) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个 PROCESS_GET_TICKET_INFO 接口");
            F2CException.throwException("ITSM 接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个 PROCESS_GET_TICKET_INFO 接口，请检查");
        }
        PciApiEndpointDTO apiEndpointGetTicketInfo = apiList.get(0);

        // 插入日志
        String apiLogId = apiLogService.packageAndInsertApiLog(
                apiEndpointGetTicketInfo.getId(),
                apiEndpointGetTicketInfo.getMethod(),
                businessEventContextDTO.getModule(),
                businessEventContextDTO.getBusinessKey(),
                businessEventContextDTO.getResourceType().getResourceName(),
                businessEventContextDTO.getResourceType().name(),
                businessEventContextDTO.getWorkspaceId()
        );
        // 记录请求体信息
        apiLogService.insertApiRequestLog(apiLogId, JSONObject.toJSONString(businessEventContextDTO.getProcessEventContext()));

        BKCoITSMGetTicketInfoResp ticketInfo = null;
        // 组织ITSM流程单数据
        BKCoITSMGetTicketInfoReq itsmOrder = buildITSMGetTicketModel(authMap, externalProcessId);
        LogUtil.info("查询工单详情参数："+JSONObject.toJSONString(itsmOrder));
        try {
            URI uri = new URI(apiEndpointGetTicketInfo.getApiEndpoint() + "" + apiEndpointGetTicketInfo.getEndpoint());
            ticketInfo = bKCoITSMClient.getTicketInfo(uri, itsmOrder);
            LogUtil.info("查询工单详情结果："+JSONObject.toJSONString(ticketInfo));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Objects.isNull(ticketInfo) || !ticketInfo.isResult()) {
            // 更新日志状态
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), ticketInfo.getMessage());
        } else {
            //查询成功，构建更新工单状态的参数
            //查询api
            List<PciApiEndpointDTO> apiOperateNodeList = apiEndpointDTOS.stream()
                    .filter((endpoint -> endpoint.getApiType().equals(ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + ApiType.API_TYPE.PROCESS_OPERATE_NODE.getApiType())))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(apiOperateNodeList)) {
                LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有 PROCESS_OPERATE_NODE 接口");
                F2CException.throwException("ITSM 接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有 PROCESS_OPERATE_NODE 接口，请检查");
            }
            if (apiOperateNodeList.size() > 1) {
                LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个 PROCESS_OPERATE_NODE 接口");
                F2CException.throwException("ITSM 接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个 PROCESS_OPERATE_NODE 接口，请检查");
            }
            PciApiEndpointDTO apiOperationEndpointDTO = apiOperateNodeList.get(0);
            //构造参数
            BKCoITSMGetOperateNodeReq bkCoITSMGetOperateNodeReq = buildITSMOperateNodeModel(authMap, externalProcessId,ticketInfo,apiOperationEndpointDTO);
            LogUtil.info("更新工单已完成状态参数："+JSONObject.toJSONString(bkCoITSMGetOperateNodeReq));
            BKCoITSMOperateNodeResp bkCoITSMOperateNodeResp = null;
            try {
                URI uri = new URI(apiOperationEndpointDTO.getApiEndpoint() + "" + apiOperationEndpointDTO.getEndpoint());
                bkCoITSMOperateNodeResp = bKCoITSMClient.operateNode(uri, bkCoITSMGetOperateNodeReq);
                LogUtil.info("更新工单已完成状态结果："+JSONObject.toJSONString(bkCoITSMOperateNodeResp));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(Objects.isNull(bkCoITSMOperateNodeResp) || !bkCoITSMOperateNodeResp.isResult()) {
                // 更新日志状态
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), ticketInfo.getMessage());
            } else {
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.SUCESS.getCode(), "ProcessId: " + externalProcessId);
            }
        }
    }

    /**
     * 组织ITSM订单数据
     */
    public BKCoITSMOrderReq buildITSMOrderModel(Map<String, String> authMap, BusinessEventContextDTO businessEventContextDTO, PciApiEndpointDTO apiEndpointDTO) {
        LogUtil.info("  => 构建蓝鲸ITSM流程单数据。。。");
        if (Objects.isNull(businessEventContextDTO)) return null;

        // 订单主表的字段是固定映射的，订单明细的映射按照字段映射配置进行封装
        BKCoITSMOrderReq itsmOrder = new BKCoITSMOrderReq();

        final String orderId = businessEventContextDTO.getBusinessKey();
        final String orderType = businessEventContextDTO.getOrderType();
        itsmOrder.setBk_app_code(authMap.get("appCode"));
        itsmOrder.setBk_app_secret(authMap.get("appSecret"));
        itsmOrder.setBk_username(authMap.get("username"));

        //约定使用参数映射列表中 映射字段 为 常量 _bk_itsm_service_id 做为服务id 如果不存在则使用系统参数配置中的数据
        String service_id = Optional.ofNullable(apiEndpointDTO.getTargetFieldParameterMappings())
                .map(e -> e.get("_bk_itsm_service_id"))
                .filter(e -> StringUtils.equals(e.getOriginFieldSource(), "constant"))
                .map(PciApiParameterMapping::getOriginField)
                .orElse(null);

        if(StringUtils.isEmpty(service_id)) {
            service_id = systemCommonParameterService.selectParameterByKey("itsm.bkcompany.service.id");
        }

        if(Objects.nonNull(service_id)) {
            itsmOrder.setService_id(Integer.parseInt(service_id));
        }
        itsmOrder.setCreator(itsmOrder.getBk_username());
//        itsmOrder.setFast_approval();
//        itsmOrder.setMeta();

        List<BKCoITSMOrderReq.BKCoITSMOrderFieldReq> fields = itsmOrder.getFields();

        //设置表单 标题
        BKCoITSMOrderReq.BKCoITSMOrderFieldReq titleField = itsmOrder.new BKCoITSMOrderFieldReq(
                "STRING",
                "title",
                "[" + orderType + "]" + orderId,
                null,
                null,
                true,
                null
        );
        fields.add(titleField);
        LogUtil.info("设置表单标题完成："+JSONObject.toJSONString(itsmOrder));
        //设置表单 表格 通过常量配置字段获取表格字段
        String customTable = Optional.ofNullable(apiEndpointDTO.getOriginFieldParameterMappings())
                .map(e -> e.get("CUSTOMTABLE"))
                .filter(e -> StringUtils.equals(e.getOriginFieldSource(), "constant"))
                .map(PciApiParameterMapping::getTargetField)
                .orElse(null);
        BKCoITSMOrderReq.BKCoITSMOrderFieldReq tableField = itsmOrder.new BKCoITSMOrderFieldReq(
                "CUSTOMTABLE",
                customTable,
                null,
                null,
                new HashMap<>(),
                false,
                null
        );
        fields.add(tableField);
        LogUtil.info("设置表单表格完成："+JSONObject.toJSONString(itsmOrder));
        //通过反射的形式设置订单信息字段;遍历映射键值对，匹配需要传参的字段和businessEventContextDTO做匹配，然后赋值
        //查询所属组织、组织管理员和系统管理员信息
        String orgCollect ="";
        List<String> orgUserIds = new ArrayList<>();
        if (!"root".equals(businessEventContextDTO.getWorkspaceId())){
            Workspace workspace = workspaceMapper.selectByPrimaryKey(businessEventContextDTO.getWorkspaceId());
            CommonOrganizationDTO orgInfo = SyncExternalOrgAndUserUtils.getOrgInfo(workspace.getOrganizationId());
            List<User> orgAdminList = orgInfo.getOrgAdminList();
            orgUserIds = orgAdminList.stream().map(User::getId).collect(Collectors.toList());
            orgCollect = orgUserIds.stream().map(str -> str + "@carizon.work").collect(Collectors.joining(","));
        }
        //蓝鲸域用户拼接
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("roleId","ADMIN");
        Pager<List<UserDTO>> userList = SyncExternalOrgAndUserUtils.getUserList(JSONObject.toJSONString(jsonObject), "1", "100");
        List<String> admins = userList.getListObject().stream().map(User::getId).collect(Collectors.toList());
        //蓝鲸域用户拼接
        String adminCollect = admins.stream().map(str -> str + "@carizon.work").collect(Collectors.joining(","));
        //管理员角色提单（回收订单）组织审批人为管理员
        if ("root".equals(businessEventContextDTO.getWorkspaceId())){
            orgCollect = adminCollect;
        }
        LogUtil.info("查询组织和管理员信息完成，user："+JSONObject.toJSONString(orgUserIds)+"；admin："+JSONObject.toJSONString(admins));
        Class<?> clazz = businessEventContextDTO.getClass();
        Map<String, PciApiParameterMapping> originFieldParameterMappings = apiEndpointDTO.getOriginFieldParameterMappings();
        for (String s : originFieldParameterMappings.keySet()) {
            for (Field field : clazz.getDeclaredFields()) {
                // 确保可以访问私有字段
                field.setAccessible(true);
                try {
                    // 获取字段名和字段值
                    String fieldName = field.getName();
                    LogUtil.info("获取字段名和字段值："+fieldName);
                    if (fieldName.equals(s) && !fieldName.equals("createTime")){
                        BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                                "STRING",
                                originFieldParameterMappings.get(s).getTargetField(),
                                (String) field.get(businessEventContextDTO),
                                null,
                                null,
                                true,
                                null
                        );
                        if ("applicant".equals(fieldName)){
                            orderField.setValue(field.get(businessEventContextDTO)+"@carizon.work");
                            orderField.setType("MEMBERS");
                        }
                        LogUtil.info("添加订单字段："+JSONObject.toJSONString(orderField));
                        fields.add(orderField);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (ApiOriginOrderField.ORIGIN_FIELD_TYPE.vm_create_orgAdmins.getKey().equals(s)){
                if (StringUtils.isNotEmpty(orgCollect)){
                    BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                            "MEMBERS",
                            originFieldParameterMappings.get(s).getTargetField(),
                            orgCollect,
                            null,
                            null,
                            true,
                            null
                    );
                    fields.add(orderField);
                    LogUtil.info("添加组织字段："+JSONObject.toJSONString(orderField));
                }
            }
            if (ApiOriginOrderField.ORIGIN_FIELD_TYPE.vm_create_admins.getKey().equals(s)){
                BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                        "MEMBERS",
                        originFieldParameterMappings.get(s).getTargetField(),
                        adminCollect,
                        null,
                        null,
                        true,
                        null
                );
                fields.add(orderField);
                LogUtil.info("添加admin字段："+JSONObject.toJSONString(orderField));
            }
        }

        // 按照参数映射配置 封装数据
        LogUtil.info("--------------按照参数映射配置 封装数据开始--------------");
        Map<String, BKCoITSMOrderReq.CustomTableMetaColumns> customTableMetaColumns = new HashMap<>();

        List<BusinessDetail> businessDetails = businessEventContextDTO.getBusinessDetails();
        if (CollectionUtils.isNotEmpty(businessDetails)) {
            //存多组item类型数据
            List<Map<String, Object>> choiceValues = new ArrayList<>();
            for (BusinessDetail businessDetail : businessDetails) {
                List<BusinessDetailItem> resItems = businessDetail.getDetailItems();
                Map<String, String> reBaseKV = resItems.stream().collect(Collectors.toMap(
                        k -> {
                            String kye = k.getKey();
                            if (k.getKey().contains("resource_pool_tag")) {
                                kye = k.getLabel();
                            }
                            return kye;
                        },
                        v -> {
                            return v.getText();
                        },
                        (v1, v2) -> v1)
                );
                LogUtil.info("  => 【可选配（推送到）ITSM的属性（愿数据）】" + new Gson().toJson(reBaseKV));

                Map<String, Object> choiceValue = new HashMap<>();
                Map<String, PciApiParameterMapping> targetFieldParameterMappings = apiEndpointDTO.getTargetFieldParameterMappings();

                for (String targetKey : targetFieldParameterMappings.keySet()) {
                    // 若字段映射中ITSM源字段值在detailItems中存在对应字段，则取对应字段的值作为目标属性值，否则直接取源字段值作为目标属性值
                    PciApiParameterMapping parameterMapping = targetFieldParameterMappings.get(targetKey);
                    String targetField = parameterMapping.getTargetField();
                    LogUtil.info("表格数据目标字段数据："+JSONObject.toJSONString(parameterMapping));
                    String originField = parameterMapping.getOriginField();
                    if(reBaseKV.containsKey(originField)) {
                        String value = reBaseKV.get(originField);
                        if ("UPDATE".equals(businessEventContextDTO.getOrderType()) && !originField.contains("instanceType")){
                            //配置变更等不能多机变更的单子机器信息配置到外面
                            BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                                    "STRING",
                                    targetField,
                                    value,
                                    null,
                                    null,
                                    true,
                                    null
                            );
                            LogUtil.info("添加订单字段："+JSONObject.toJSONString(orderField));
                            fields.add(orderField);
                        }
                        else if ("DISK_UPDATE".equals(businessEventContextDTO.getOrderType())){
                            //磁盘变更的单子机器信息配置到外面
                            if (!originField.contains("diskConfig")){
                                BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                                        "STRING",
                                        targetField,
                                        value,
                                        null,
                                        null,
                                        true,
                                        null
                                );
                                LogUtil.info("添加订单字段："+JSONObject.toJSONString(orderField));
                                fields.add(orderField);
                            }
                            else {
                                //磁盘列表数据都存到自定义表格中
                                JSONArray diskList =  JSONArray.parseArray(value);
                                //一次性加多个
                                for (int i = 0; i < diskList.size(); i++) {
                                    JSONObject diskConfig = (JSONObject) diskList.get(i);
                                    JSONArray diskConfigJSONArray = diskConfig.getJSONArray("value");
                                    Map<String, Object> choiceValueDisk;
                                    if (choiceValues.size() == i){
                                        choiceValueDisk =  new HashMap<>();
                                        choiceValues.add(choiceValueDisk);
                                    }else {
                                        choiceValueDisk = choiceValues.get(i);
                                    }
                                    for (int i1 = 0; i1 < diskConfigJSONArray.size(); i1++) {
                                        JSONObject diskConfigJSONObj = (JSONObject) diskConfigJSONArray.get(i1);
                                        if (diskConfigJSONObj.get("key").equals(originField)){
                                            choiceValueDisk.put(targetField, diskConfigJSONObj.getString("text"));
                                            LogUtil.info("表格数据字段添加："+JSONObject.toJSONString(choiceValueDisk));
                                            if(!customTableMetaColumns.containsKey(targetField)) {
                                                BKCoITSMOrderReq.CustomTableMetaColumns customTableMetaColumn = new BKCoITSMOrderReq().new CustomTableMetaColumns();
                                                customTableMetaColumn.setKey(targetField);
                                                customTableMetaColumn.setName(parameterMapping.getDescription());
                                                customTableMetaColumns.put(targetField, customTableMetaColumn);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            choiceValue.put(targetField, value);
                            LogUtil.info("表格数据字段添加："+JSONObject.toJSONString(choiceValue));
                            if(!customTableMetaColumns.containsKey(targetField)) {
                                BKCoITSMOrderReq.CustomTableMetaColumns customTableMetaColumn = new BKCoITSMOrderReq().new CustomTableMetaColumns();
                                customTableMetaColumn.setKey(targetField);
                                customTableMetaColumn.setName(parameterMapping.getDescription());
                                customTableMetaColumns.put(targetField, customTableMetaColumn);
                            }
                        }
                    }
                    //标签数据获取
                    if("tag".equals(parameterMapping.getOriginFieldSource())) {
                        //申请机器类型标签数据获取
                        if("CREATE".equals(businessEventContextDTO.getOrderType())){
                            LogUtil.info("CREATE 类型订单添加标签数据");
                            TagDTO tagDTO = tagService.getTagDtoById(parameterMapping.getOriginField());
                            if (tagDTO != null) {
                                List<TagValue> tagValues = tagDTO.getTagValues();
                                for (TagValue tagValue : tagValues) {
                                    if (reBaseKV.containsValue(tagValue.getId())){
                                        choiceValue.put(targetField, tagValue.getTagValueAlias());
                                        if(!customTableMetaColumns.containsKey(targetField)) {
                                            BKCoITSMOrderReq.CustomTableMetaColumns customTableMetaColumn = new BKCoITSMOrderReq().new CustomTableMetaColumns();
                                            customTableMetaColumn.setKey(targetField);
                                            customTableMetaColumn.setName(parameterMapping.getDescription());
                                            customTableMetaColumns.put(targetField, customTableMetaColumn);
                                        }
                                    }
                                }
                            }
                        }else {
                            // 变更类型标签字段映射处理
                            LogUtil.info("变更类型订单添加标签数据");
                            String resourceId = reBaseKV.get("id");
                            String tagFieldValue = getTagFieldValue(parameterMapping, resourceId);
                            if ("UPDATE".equals(businessEventContextDTO.getOrderType()) || "DISK_UPDATE".equals(businessEventContextDTO.getOrderType())){
                                //配置变更等不能多机变更的单子机器信息配置到外面
                                BKCoITSMOrderReq.BKCoITSMOrderFieldReq orderField = itsmOrder.new BKCoITSMOrderFieldReq(
                                        "STRING",
                                        targetField,
                                        StringUtils.isEmpty(tagFieldValue) ? "无" : tagFieldValue,
                                        null,
                                        null,
                                        true,
                                        null
                                );
                                LogUtil.info("添加订单字段："+JSONObject.toJSONString(orderField));
                                fields.add(orderField);
                            }else {
                                choiceValue.put(targetField, tagFieldValue);
                                LogUtil.info("表格数据字段添加："+JSONObject.toJSONString(choiceValue));
                                if(!customTableMetaColumns.containsKey(targetField)) {
                                    BKCoITSMOrderReq.CustomTableMetaColumns customTableMetaColumn = new BKCoITSMOrderReq().new CustomTableMetaColumns();
                                    customTableMetaColumn.setKey(targetField);
                                    customTableMetaColumn.setName(parameterMapping.getDescription());
                                    customTableMetaColumns.put(targetField, customTableMetaColumn);
                                }
                            }
                        }
                    }
                }
                //自定义表格添加多条数据就存到这个choiceValues数组中，数组的数量就是表格中的行数
                if (!"DISK_UPDATE".equals(businessEventContextDTO.getOrderType())){
                    choiceValues.add(choiceValue);
                }
            }
            tableField.setValue(choiceValues);
            if(MapUtils.isNotEmpty(customTableMetaColumns)) {
                List<BKCoITSMOrderReq.CustomTableMetaColumns> columns = new ArrayList<>();
                customTableMetaColumns.forEach((k, v) -> columns.add(v));
                tableField.getMeta().put("columns",columns);
            }
        }

        return itsmOrder;
    }

    /**
     * 构建查询工单请求参数
     */
    public BKCoITSMGetTicketInfoReq buildITSMGetTicketModel(Map<String, String> authMap, String sn) {
        LogUtil.info("  => 构建查询工单请求参数。。。");
        BKCoITSMGetTicketInfoReq itsmOrder = new BKCoITSMGetTicketInfoReq();
        itsmOrder.setBk_app_code(authMap.get("appCode"));
        itsmOrder.setBk_app_secret(authMap.get("appSecret"));
        itsmOrder.setBk_username(authMap.get("username"));
        itsmOrder.setSn(sn);
        return itsmOrder;
    }
    /**
     * 构建更新工单请求参数（已完成）
     */
    public BKCoITSMGetOperateNodeReq buildITSMOperateNodeModel(Map<String, String> authMap, String sn,BKCoITSMGetTicketInfoResp ticketInfo,PciApiEndpointDTO apiEndpointDTO) {
        LogUtil.info("  => 构建更新工单请求参数（已完成）。。。");
        BKCoITSMGetOperateNodeReq itsmOrder = new BKCoITSMGetOperateNodeReq();
        itsmOrder.setBk_app_code(authMap.get("appCode"));
        itsmOrder.setBk_app_secret(authMap.get("appSecret"));
        itsmOrder.setBk_username(authMap.get("username"));
        itsmOrder.setSn(sn);
        JSONObject data = ticketInfo.getData();
        JSONArray current_steps = data.getJSONArray("current_steps");
        if (current_steps == null || current_steps.size() == 0){
            throw new RuntimeException("工单查询的当前节点为空！");
        }
        JSONObject currentStepsJSONObject = current_steps.getJSONObject(0);
        String processors = currentStepsJSONObject.getString("processors");
        String[] split = processors.split(",");
        if (split.length > 0){
            itsmOrder.setOperator(split[0]);
        }else {
            throw new RuntimeException("工单查询的当前节点执行人为空！");
        }
        itsmOrder.setState_id(currentStepsJSONObject.getInteger("state_id"));
        JSONArray fields = new JSONArray();
        itsmOrder.setFields(fields);
        Map<String, PciApiParameterMapping> originFieldParameterMappings = apiEndpointDTO.getOriginFieldParameterMappings();
        for (String s : originFieldParameterMappings.keySet()) {
            if ("state".equals(s)){
                //状态
                JSONObject stateObject = new JSONObject();
                stateObject.put("key",originFieldParameterMappings.get(s).getTargetField());
                stateObject.put("value","true");
                fields.add(stateObject);
            }
            if ("remark".equals(s)){
                //备注
                JSONObject remarkObject = new JSONObject();
                remarkObject.put("key",originFieldParameterMappings.get(s).getTargetField());
                remarkObject.put("value","订单已执行完成");
                fields.add(remarkObject);
            }
        }
        return itsmOrder;
    }

    public String getTagFieldValue(PciApiParameterMapping parameterMapping, String primaryKey) {
        String result = "";

        if (StringUtils.isEmpty(primaryKey)) {
            return null;
        }
        // 查询标签
        Tag tag = tagMapper.selectByPrimaryKey(parameterMapping.getOriginField());

        if (tag != null) {
            TagMappingExample example = new TagMappingExample();
            example.createCriteria().andResourceIdEqualTo(primaryKey).andTagKeyEqualTo(tag.getTagKey());
            // 查询资源的标签映射关系
            List<TagMapping> tagMappings = tagMappingMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(tagMappings)) {
                return result;
            }
            // 根据映射关系查询标签值
            TagValue tagValue = tagValueMapper.selectByPrimaryKey(tagMappings.get(0).getTagValueId());
            if (tagValue != null) {
                result = StringUtils.isNotEmpty(tagValue.getTagValueAlias()) ? tagValue.getTagValueAlias() : tagValue.getTagValue();
            }
        }
        return result;
    }

    /**
     * 按照字段映射配置 封装订单明细字段集合
     */
    private void packageITSMRequestBody(PciApiEndpointDTO apiEndpointDTO, List<BusinessDetailItem> detailItems, List<ITSMOrderItemAttribute> itemAttrs) {
        JSONObject jsonObject = new JSONObject();
        Map<String, PciApiParameterMapping> targetFieldParameterMappings = apiEndpointDTO.getTargetFieldParameterMappings();
        for (String targetKey : targetFieldParameterMappings.keySet()) {
            ITSMOrderItemAttribute attribute = new ITSMOrderItemAttribute();
            attribute.setName(targetKey);

            // 若字段映射中ITSM 源字段值在 detailItems 中存在对应字段，则取对应字段的值作为目标属性值，否则直接取源字段值作为目标属性值
            PciApiParameterMapping parameterMapping = targetFieldParameterMappings.get(targetKey);
            Optional<BusinessDetailItem> detailItemOptional = detailItems.stream().filter(item -> Objects.equals(item.getKey(), parameterMapping.getOriginField())).findFirst();
            if (detailItemOptional.isPresent()) {
                BusinessDetailItem detailItem = detailItemOptional.get();
                attribute.setValue(StringUtils.isNoneBlank(detailItem.getValue()) ? detailItem.getValue() : "Unknown");
            } else {
                attribute.setValue(parameterMapping.getOriginField());
            }
            itemAttrs.add(attribute);
        }
    }



}
