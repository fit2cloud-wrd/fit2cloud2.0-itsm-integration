package com.fit2cloud.itsm.integration;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.webservice.SoapClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.mapper.UserMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.BusinessDetail;
import com.fit2cloud.commons.server.process.dto.BusinessDetailItem;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.xml.soap.SOAPElement;
import javax.xml.xpath.XPathConstants;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class LandrayIntegration implements ITSMIntegration {

    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ApiLogService apiLogService;
    @Resource
    private UserMapper userMapper;



    @Override
    public String getId() {
        return "LANDRAY";
    }

    @Override
    public String getName() {
        return "蓝凌";
    }

    @Override
    public String getVersion() {
        return ApiSupportVersion.LANDRAY.V1.getVersion();
    }

    @Override
    public List<String> getSupportVersion() {
        List<String> versionList = new ArrayList<>();
        for (ApiSupportVersion.LANDRAY value : ApiSupportVersion.LANDRAY.values()) {
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
    // todo
    public String testDefaultApiUrl() {
        return "?wsdl";
    }

    @Override
    public String getDefaultApiListJson() {
        return ReadConfigFileUtil.readConfigFile("LandrayDefaultApiList.json");
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
        return ReadConfigFileUtil.readConfigFile("LandrayCredential.json");
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
    public String takeOneItsm(BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口创建流程，返回外部系统流程 ID
        return queryApiInfoAndCreateITSMProcess(businessEventContextDTO);
    }

    @Override
    public void putOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口发送流程详情
    }

    @Override
    public void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部系统反馈执行结果
    }

    /**
     * 组织ITSM订单数据
     */
    public ITSMOrder buildITSMOrderModel(BusinessEventContextDTO businessEventContextDTO, PciApiEndpointDTO apiEndpointDTO) {
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
        //itsmOrder.setItsmOrderId(externalProcessId);
        PciApiParameterMapping fdIdMapping = apiEndpointDTO.getTargetFieldParameterMappings().get("fdId");
        if (!ObjectUtils.isEmpty(fdIdMapping)) {
            String fdId = fdIdMapping.getOriginField();
            itsmOrder.setItsmModelId(fdId);
        }
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
            if (detailItemOptional.isPresent() && !detailItemOptional.get().getKey().contains("resource_pool_tag")) {
                BusinessDetailItem detailItem = detailItemOptional.get();
                attribute.setValue(StringUtils.isNoneBlank(detailItem.getValue()) ? detailItem.getValue() : "Unknown");
                attribute.setLabel(StringUtils.isNoneBlank(detailItem.getLabel()) ? detailItem.getLabel() : "Unknown");
                itemAttrs.add(attribute);
            } else if (detailItemOptional.isPresent() && detailItemOptional.get().getKey().contains("resource_pool_tag")){
                BusinessDetailItem detailItem = detailItemOptional.get();
                attribute.setValue(StringUtils.isNoneBlank(detailItem.getText()) ? detailItem.getText() : "Unknown");
                attribute.setLabel(StringUtils.isNoneBlank(detailItem.getLabel()) ? detailItem.getLabel() : "Unknown");
                itemAttrs.add(attribute);
            }else {
                attribute.setValue(parameterMapping.getOriginField());

            }

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
     * 发送请求更新ITSM流程单
     */
    private String resolveCredicialAndCreateITSM(List<APICredentialItem> credentialItems, PciApiEndpointDTO apiEndpointDTO, BusinessEventContextDTO businessEventContextDTO) {
        // 组织ITSM订单数据
        ITSMOrder itsmOrder = buildITSMOrderModel(businessEventContextDTO, apiEndpointDTO);

        return notifyITSM(itsmOrder, apiEndpointDTO, "UNCHECKED", businessEventContextDTO);
    }

    private String notifyITSM(ITSMOrder itsmOrder, PciApiEndpointDTO apiEndpointDTO, String cmpOrderStatus, BusinessEventContextDTO businessEventContextDTO) {
        try {
            //businessEventContextDTO.setApplicant("1112169");
            String result = "unknown";
            String returnStr = "";
            // 构建发送ITSM请求参数
            JSONObject formValues = new JSONObject();
            formValues = buildItsmRequestParam(itsmOrder, cmpOrderStatus, businessEventContextDTO);
            //订单层面的数据封装
            PciApiParameterMapping OrderIdFdIdMapping = apiEndpointDTO.getOriginFieldParameterMappings().get("orderId");
            if (!ObjectUtils.isEmpty(OrderIdFdIdMapping)) {
                String orderId = itsmOrder.getOrderId();
                formValues.put(OrderIdFdIdMapping.getTargetField(), orderId);
            }
            PciApiParameterMapping applyUserIdFdIdMapping = apiEndpointDTO.getOriginFieldParameterMappings().get("applyUserId");
            if (!ObjectUtils.isEmpty(applyUserIdFdIdMapping)) {
                String userId = businessEventContextDTO.getApplicant();
                formValues.put(applyUserIdFdIdMapping.getTargetField(), userId);
            }
            PciApiParameterMapping applyUserNameFdIdMapping = apiEndpointDTO.getOriginFieldParameterMappings().get("applyUserName");
            if (!ObjectUtils.isEmpty(applyUserNameFdIdMapping)) {
                String userName = userMapper.selectByPrimaryKey(businessEventContextDTO.getApplicant()).getName();
                formValues.put(applyUserNameFdIdMapping.getTargetField(), userName);
            }
            PciApiParameterMapping descriptionFdIdMapping = apiEndpointDTO.getOriginFieldParameterMappings().get("orderDescription");
            if (!ObjectUtils.isEmpty(descriptionFdIdMapping)) {
                String discription = businessEventContextDTO.getDescription();
                formValues.put(descriptionFdIdMapping.getTargetField(), discription);
            }
            LogUtil.info("formValues是" + formValues.toString());

            // 插入日志
            String apiLogId = apiLogService.packageAndInsertApiLog(apiEndpointDTO.getId(), apiEndpointDTO.getMethod(), "VM",
                    itsmOrder.getOrderId(), itsmOrder.getOperDesc(), "VIRTUALMACHINE", businessEventContextDTO.getWorkspaceId());

            // 记录请求体信息
            apiLogService.insertApiRequestLog(apiLogId, JSONObject.toJSONString(businessEventContextDTO.getProcessEventContext()));

            try {
                String url = apiEndpointDTO.getApiEndpoint();
                String namespace = "";
                List<APICredentialItem> credentialItems = this.getCredentialTemplate();
                for (APICredentialItem item : credentialItems) {
                    if (Objects.equals(item.getName(), "namespace")) {
                        namespace = item.getDefaultValue();
                        break;
                    }
                }
                if (ObjectUtils.isEmpty(namespace)) {
                    throw new RuntimeException("蓝凌OA账号缺少namespace");
                }

                LogUtil.info("【creatITSM】Request url：" + url);
                // 新建客户端
                SoapClient client = SoapClient.create(url)
                        // 设置要请求的方法，此接口方法前缀为web，传入对应的命名空间
                        .setMethod("web:"+apiEndpointDTO.getEndpoint(), namespace);
                SOAPElement arg0 = client.getMethodEle().addChildElement("arg0");
                JSONObject creator = new JSONObject();
                creator.put("PersonNo", businessEventContextDTO.getApplicant());
                arg0.addChildElement("docCreator").setValue(creator.toJSONString());
                arg0.addChildElement("docStatus").setValue("20");
                arg0.addChildElement("docSubject").setValue("虚拟机申请");
                arg0.addChildElement("fdTemplateId").setValue(itsmOrder.getItsmModelId());
                arg0.addChildElement("formValues").setValue(formValues.toJSONString());

                // 设置参数，此处自动添加方法的前缀：web
//                        .setParam("docContent", "tyhtest", false)
//                                .setParam("docCreator", "{\"PersonNo\":\"1112169\"}", false)
//                                        .setParam("docStatus", 20, false)
//                                                .setParam("docSubject", "虚拟机申请", false)
//                                                        .setParam("fdTemplateId", "1886b21f9df712da8fe2b47486cad3db", false)
//                                                                .setParam("formValues", "{}", false);

                // 发送请求，参数true表示返回一个格式化后的XML内容
                // 返回内容为XML字符串，可以配合XmlUtil解析这个响应
                HttpResponse response = client.sendForResponse();
                result = response.body();
                //result = client.send(true);
                //client.sendForResponse();
                String msg = client.getMsgStr(true);
                Document document = XmlUtil.parseXml(result);
                returnStr = (String)XmlUtil.getByXPath("//soap:Envelope//soap:Body//ns1:addReviewResponse//return", document, XPathConstants.STRING);
                //Object object = XmlUtil.readObjectFromXml(result);
                LogUtil.info("向oa发送流程启动，返回是:" + returnStr);

                LogUtil.info("==== 【ITSM Order】Notify ITSM End：[" + JSONObject.toJSONString(returnStr) + "]");

                // 处理结果
                if (!ObjectUtils.isEmpty(returnStr)) {
                } else {
                    // 更新日志状态
                    apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), returnStr);
                    LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + returnStr + "]");
                }
            } catch (Exception e) {
                // 更新日志状态
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), e.getMessage());
                LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + JSONObject.toJSONString(returnStr) + "]");
                LogUtil.info(JSONObject.toJSONString(e));
                e.printStackTrace();
            }
            // 更新日志状态
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.SUCESS.getCode(), "");
            return returnStr;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 构建ITSM流程单更新请求参数
     */
    public JSONObject buildItsmRequestParam(ITSMOrder itsmOrder, String cmpOrderStatus, BusinessEventContextDTO businessEventContextDTO) {
        // 创建参数对象

        JSONObject valueObject = new JSONObject();
        List<BusinessDetail> businessDetailList = businessEventContextDTO.getBusinessDetails();
        List<ITSMOrderItem> orderItemList = itsmOrder.getItems();
        if (orderItemList.size()<1) {
            return new JSONObject();
        }
        for (ITSMOrderItem orderItem:orderItemList){
            for (ITSMOrderItemAttribute attribute: orderItem.getItemAttrs()) {
                //如果key不在表格中，类似"fd_3be280342ece9e"
                if (!attribute.getName().contains(".")) {
                    valueObject.put(attribute.getName(), attribute.getValue());
                }
                //如果key在表格中，类似"fd_3be280342ece9e.fd_3be33a1304b820"
                else {
                    String[] fdids = new String[2];
                    fdids = attribute.getName().split("\\.");
                    if (valueObject.containsKey(fdids[0])) {
                        JSONObject tableObject = valueObject.getJSONObject(fdids[0]);
                        if (tableObject.containsKey(attribute.getName())) {
                            JSONArray tableColumn = tableObject.getJSONArray(attribute.getName());
                            tableColumn.add(attribute.getValue());
                            tableObject.put(attribute.getName(), tableColumn);
                        }else {
                            JSONArray tableColumn = new JSONArray();
                            tableColumn.add(attribute.getValue());
                            tableObject.put(attribute.getName(), tableColumn);
                        }
                        valueObject.put(fdids[0], tableObject);
                    }else {
                        JSONObject tableObject = new JSONObject();
                        JSONArray tableColumn = new JSONArray();
                        tableColumn.add(attribute.getValue());
                        tableObject.put(attribute.getName(), tableColumn);
                        valueObject.put(fdids[0], tableObject);
                    }
                }
            }
        }

        //for () {}
        //String values = "{\"" + subjectId + "\":\"" + subject + "\",\"" + contentId + "\":\"" + content + "\",\"" + reasonId + "\":\"" + description + "\"}";
        return valueObject;
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
