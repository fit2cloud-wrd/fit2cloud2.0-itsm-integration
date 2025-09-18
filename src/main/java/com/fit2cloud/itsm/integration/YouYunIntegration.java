package com.fit2cloud.itsm.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.BusinessDetail;
import com.fit2cloud.commons.server.process.dto.BusinessDetailItem;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.itsm.common.utils.ReadConfigFileUtil;
import com.fit2cloud.itsm.common.utils.SpringContextUtils;
import com.fit2cloud.itsm.common.utils.HttpClientConfig;
import com.fit2cloud.itsm.common.utils.HttpClientUtil;
import com.fit2cloud.itsm.common.constants.ApiSupportVersion;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import com.fit2cloud.itsm.model.dto.*;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.fit2cloud.itsm.model.response.ItsmUpdateResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class YouYunIntegration implements ITSMIntegration {

    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ApiLogService apiLogService;



    @Override
    public String getId() {
        return "YOUYUN";
    }

    @Override
    public String getName() {
        return "优云";
    }

    @Override
    public String getVersion() {
        return ApiSupportVersion.YOUYUN.V1.getVersion();
    }

    @Override
    public List<String> getSupportVersion() {
        List<String> versionList = new ArrayList<>();
        for (ApiSupportVersion.YOUYUN value : ApiSupportVersion.YOUYUN.values()) {
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
        return "/store/openapi/v2/models/classes/layers/get_all";
    }

    @Override
    public String getDefaultApiListJson() {
        return ReadConfigFileUtil.readConfigFile("YouYunDefaultApiList.json");
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
        return ReadConfigFileUtil.readConfigFile("YouYunCredential.json");
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
        queryApiInfoAndPushITSM("UNCHECKED", externalProcessId, businessEventContextDTO);
    }

    @Override
    public String takeOneItsm(BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口创建流程，返回外部系统流程 ID
        return queryApiInfoAndCreateITSMProcess(businessEventContextDTO);
    }

    @Override
    public void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部系统反馈执行结果
        queryApiInfoAndPushITSM("FINISHED", externalProcessId, businessEventContextDTO);
    }

    /**
     * 组织ITSM订单数据
     */
    public ITSMOrder buildITSMOrderModel(String externalProcessId, BusinessEventContextDTO businessEventContextDTO, PciApiEndpointDTO apiEndpointDTO) {
        LogUtil.info("【buildITSMOrderModel】-【itsmOrder：" + JSONObject.toJSONString(businessEventContextDTO) + "】");

        if (Objects.isNull(businessEventContextDTO)) return null;

        final String orderId = businessEventContextDTO.getBusinessKey();
        final String orderType = businessEventContextDTO.getOrderType();
        Workspace workspace = workspaceMapper.selectByPrimaryKey(businessEventContextDTO.getWorkspaceId());

        // 订单主表的字段是固定映射的，订单明细的映射按照字段映射配置进行封装
        ITSMOrder itsmOrder = new ITSMOrder();
        itsmOrder.setOrderId(orderId);
        itsmOrder.setOperType(orderType);
        itsmOrder.setOperDesc(Translator.get(businessEventContextDTO.getProcessName()));
        itsmOrder.setItsmOrderId(externalProcessId);

        // 按照参数映射配置 封装数据
        List<BusinessDetail> businessDetails = businessEventContextDTO.getBusinessDetails();
        if (CollectionUtils.isNotEmpty(businessDetails)) {
            for (BusinessDetail businessDetail : businessDetails) {
                ITSMOrderItem itsmOrderItem = new ITSMOrderItem();
                // todo: 待确认 是否需要 OrderItemId
//                itsmOrderItem.setOrderItemId(orderItemInfo.getOrderItemId());
                itsmOrderItem.setDataBase(false);

                // 单个订单的 属性集
                List<ITSMOrderItemAttribute> itemAttrs = new ArrayList<>();
                // 字段封装
                packageITSMRequestBody(apiEndpointDTO, businessDetail.getDetailItems(), itemAttrs);
                itsmOrderItem.setItemAttrs(itemAttrs);
                itsmOrder.getItems().add(itsmOrderItem);
            }
        }


        LogUtil.info("【buildITSMOrderModel】-【itsmOrder：" + JSONObject.toJSONString(itsmOrder) + "】");
        return itsmOrder;
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

    private ITSMOrderItemAttribute produceParamMap(String name, String label, String value, String applyOrConfiguration) {
        ITSMOrderItemAttribute itsm_order_attr = new ITSMOrderItemAttribute();
        itsm_order_attr.setName(name);
        itsm_order_attr.setLabel(label);
        itsm_order_attr.setValue(StringUtils.isNoneBlank(value) ? value : "Unknown");
        itsm_order_attr.setApplyOrConfiguration(applyOrConfiguration);
        LogUtil.info("【OrderService】-【produceParamMap】-【Working ===》》》" + itsm_order_attr.getLabel() + "（" + itsm_order_attr.getName() + "）" + "::" + itsm_order_attr.getValue() + "::" + itsm_order_attr.getApplyOrConfiguration() + "《《《===");
        return itsm_order_attr;
    }

    /**
     * 调用外部接口创建流程，返回外部系统流程 ID
     */
    private String queryApiInfoAndCreateITSMProcess(BusinessEventContextDTO businessEventContextDTO) {
        // 接口地址
        String apiEndPoint = this.getDefaultAPIEndpoint();

        // 认证信息
        List<APICredentialItem> credentialItems = this.getCredentialTemplate();

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

        // 此处实现调用第三方接口推送数据
        return resolveCredicialAndCreateITSM(credentialItems, apiList.get(0), businessEventContextDTO);
    }

    /**
     * 发送请求更行ITSM流程单
     */
    private String resolveCredicialAndCreateITSM(List<APICredentialItem> credentialItems, PciApiEndpointDTO apiEndpointDTO, BusinessEventContextDTO businessEventContextDTO) {
        String itsmApiKey = null, itsmTenantId = null, modelId = null;

        // 认证信息
        for (APICredentialItem item : credentialItems) {
            if (Objects.equals(item.getName(), "itsmApiKey")) {
                itsmApiKey = item.getDefaultValue();
            }
            if (Objects.equals(item.getName(), "itsmTenantId")) {
                itsmTenantId = item.getDefaultValue();
            }
            if (Objects.equals(item.getName(), "modelId")) {
                modelId = item.getDefaultValue();
            }
        }

        if (StringUtils.isAnyBlank(itsmApiKey, itsmTenantId, modelId)) {
            LogUtil.info("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
            F2CException.throwException("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
        }

        // 组织ITSM订单数据
        ITSMOrder itsmOrder = buildITSMOrderModel(modelId, businessEventContextDTO, apiEndpointDTO);

        return notifyITSM(itsmOrder, itsmApiKey, itsmApiKey, apiEndpointDTO, "UNCHECKED", businessEventContextDTO);
    }

    /**
     * 调用外部接口发送流程详情
     * 调用外部系统反馈执行结果
     */
    private String queryApiInfoAndPushITSM(String cmpOrderStatus, String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 接口地址
        String apiEndPoint = this.getDefaultAPIEndpoint();

        // 认证信息
        List<APICredentialItem> credentialItems = this.getCredentialTemplate();

        // 接口列表
        List<PciApiEndpointDTO> apiEndpointDTOS = this.getDefaultApiList();
        // 过滤 ApiType.API_TYPE
        String operation = businessEventContextDTO.getOrderType();
        List<PciApiEndpointDTO> apiList = apiEndpointDTOS.stream()
                .filter((endpoint -> endpoint.getApiType().equals(ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(apiList)) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口");
            F2CException.throwException("CMDB资源接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口，请检查");
        }
        if (apiList.size() > 1) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口");
            F2CException.throwException("CMDB资源接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口，请检查");
        }

        // 此处实现调用第三方接口推送数据
        return resolveCredicialAndPushITSM(externalProcessId, cmpOrderStatus, credentialItems, apiList.get(0), businessEventContextDTO);
    }

    /**
     * 发送请求更新ITSM流程单
     */
    private String resolveCredicialAndPushITSM(String externalProcessId, String cmpOrderStatus,
                                               List<APICredentialItem> credentialItems, PciApiEndpointDTO apiEndpointDTO, BusinessEventContextDTO businessEventContextDTO) {
        String itsmApiKey = null, itsmTenantId = null;

        // 认证信息
        for (APICredentialItem item : credentialItems) {
            if (Objects.equals(item.getName(), "itsmApiKey")) {
                itsmApiKey = item.getDefaultValue();
            }
            if (Objects.equals(item.getName(), "itsmTenantId")) {
                itsmTenantId = item.getDefaultValue();
            }
        }

        if (StringUtils.isAnyBlank(itsmApiKey, itsmTenantId)) {
            LogUtil.info("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
            F2CException.throwException("当前API账号下 认证信息配置错误，请联系管理员检查API账号配置！");
        }

        // 组织ITSM订单数据
        ITSMOrder itsmOrder = buildITSMOrderModel(externalProcessId, businessEventContextDTO, apiEndpointDTO);

        return notifyITSM(itsmOrder, itsmApiKey, itsmApiKey, apiEndpointDTO, cmpOrderStatus, businessEventContextDTO);
    }

    private String notifyITSM(ITSMOrder itsmOrder, String itsmApiKey, String itsmTenantId,
                              PciApiEndpointDTO apiEndpointDTO, String cmpOrderStatus, BusinessEventContextDTO businessEventContextDTO) {
        String re = "unknown";
        // 构建发送ITSM请求参数
        Map<String, Object> requestBody = buildItsmRequestParam(itsmOrder, cmpOrderStatus);

        // 插入日志
        String apiLogId = apiLogService.packageAndInsertApiLog(apiEndpointDTO.getId(), apiEndpointDTO.getMethod(), "VM",
                itsmOrder.getOrderId(), itsmOrder.getOperDesc(), "VIRTUALMACHINE", businessEventContextDTO.getWorkspaceId());

        // 记录请求体信息
        apiLogService.insertApiRequestLog(apiLogId, JSONObject.toJSONString(businessEventContextDTO.getProcessEventContext()));

        try {
            // 请求样例：http://172.168.153.14/itsm/openapi/v3/tickets/update?apikey=e10adc3949ba59abbe56e057f2gg88dd&tenant_id=e10adc3949ba59abbe56e057f20f88dd
            String url = apiEndpointDTO.getApiEndpoint() + "/" + apiEndpointDTO.getEndpoint() + "?apikey=" + itsmApiKey + "&tenant_id=" + itsmTenantId;
            LogUtil.info("【editCMDBResource】Request url：" + url);

            // 执行请求
            String result = callClientAPI(url, apiEndpointDTO, requestBody);
            if (Objects.isNull(result)) {
                F2CException.throwException("【ITSM Order】更新 ITSM 流程订单，updateITSMFlow 调用结果为 null");
            }
            LogUtil.info("==== 【ITSM Order】Notify ITSM End：[" + JSONObject.toJSONString(result) + "]");

            // 处理结果
            if (result.contains("Success")) {
                re = result;
            } else {
                ItsmUpdateResult itsmUpdateResult = JSONObject.parseObject(result, ItsmUpdateResult.class);
                re = JSONObject.toJSONString(itsmUpdateResult.getError());
                // 更新日志状态
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), re);
                LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + re + "]");
            }
        } catch (Exception e) {
            // 更新日志状态
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), e.getMessage());
            LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + JSONObject.toJSONString(requestBody) + "]");
            LogUtil.info(JSONObject.toJSONString(e));
            e.printStackTrace();
        }
        // 更新日志状态
        apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.SUCESS.getCode(), "");
        return re;
    }

    /**
     * 构建ITSM流程单更新请求参数
     */
    public Map<String, Object> buildItsmRequestParam(ITSMOrder itsmOrder, String cmpOrderStatus) {
        String operType = itsmOrder.getOperType();
        String operDesc = itsmOrder.getOperDesc();

        Map<String, Object> formMap = new HashMap<>();
        formMap.put("title", operDesc);
        formMap.put("urgentLevel", "3");
        formMap.put("ticketDesc", operType);
        formMap.put("operType", operType);
        formMap.put("cmpOrderStatus", cmpOrderStatus.toLowerCase());

        // 订单明细项字段
        List<Map<String, Object>> itemList = new ArrayList<>();
        List<ITSMOrderItem> items = itsmOrder.getItems();
        for (ITSMOrderItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("rowId", UUIDUtil.newUUID());
            List<ITSMOrderItemAttribute> itemAttrs = item.getItemAttrs();
            for (ITSMOrderItemAttribute itemAttr : itemAttrs) {
                itemMap.put(itemAttr.getName(), itemAttr.getValue());
            }
            itemList.add(itemMap);
        }

        String formKey = operType.replaceAll("_", "");
        formMap.put(formKey, itemList);

        Map<String, Object> reMap = new HashMap<>();
        reMap.put("ticket_id", itsmOrder.getItsmOrderId());
        reMap.put("form", formMap);
        return reMap;
    }

    public String callClientAPI(String callBackUrl, PciApiEndpointDTO apiEndpointDTO, Map<String, Object> requestBody) {
        String resultJson = "";

//        /*请求体转换为x-www-form-urlencoded请求的格式*/
//        Map<String, Object> params = new HashMap<>();
//        params.put("requestBody", requestBody);

        /*执行回调请求*/
        resultJson = callBack(callBackUrl, apiEndpointDTO.getMethod(), requestBody);

        /*返回结果*/
        return resultJson;
    }

    private String callBack(String url, String requestMethod, Object params) {
        String responseJson;

        HttpClientConfig config = auth();
        if (requestMethod.equals(RequestMethod.GET.name())) {
            responseJson = HttpClientUtil.get(url, config);
        } else {
            responseJson = HttpClientUtil.postFormUrlEncoded(url, JSON.toJSONString(params), config);
        }

        LogUtil.info("responseJson is:" + responseJson);

        return responseJson;
    }

    private HttpClientConfig auth() {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        httpClientConfig.addHeader("Accept", "application/json;charset=UTF-8");
        httpClientConfig.addHeader("Content-type", "application/json");
        return httpClientConfig;
    }

}
