package com.fit2cloud.itsm.integration;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.BusinessDetail;
import com.fit2cloud.commons.server.process.dto.BusinessDetailItem;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import com.fit2cloud.itsm.model.dto.ITSMOrder;
import com.fit2cloud.itsm.model.dto.ITSMOrderItem;
import com.fit2cloud.itsm.model.dto.ITSMOrderItemAttribute;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.fit2cloud.itsm.model.response.ResponseResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;



@Service
public abstract class BaseITSMIntegration {

    /**
     * 子类通过以下方式注入依赖
     *     @Resource
     *     public void setApiLogService(ApiLogService apiLogService){
     *         super.apiLogService=apiLogService;//父类属性注入
     *     }
     */
    protected ApiLogService apiLogService;

    public <T> ResponseResult<T> sendRequestToITSM(String method, List<PciApiEndpointDTO> apiEndpointDTOS, BusinessEventContextDTO businessEventContextDTO) {
        // 获取Endppoint、获取认证信息、获取调用接口
        PciApiEndpointDTO apiEndpointDTO = getApiEndpointDTO(apiEndpointDTOS, businessEventContextDTO);
        // 封装ITSMOrder
        ITSMOrder itsmOrder = buildITSMOrderModel(businessEventContextDTO, apiEndpointDTO);
        // 插入日志
        String apiLogId = apiLogService.packageAndInsertApiLog(apiEndpointDTO.getId(), apiEndpointDTO.getMethod(), businessEventContextDTO.getModule(),
                itsmOrder.getOrderId(), itsmOrder.getOperDesc(), businessEventContextDTO.getResourceType().name(), businessEventContextDTO.getWorkspaceId());
        // 记录请求体信息
        apiLogService.insertApiRequestLog(apiLogId, JSONObject.toJSONString(businessEventContextDTO));
        ResponseResult<T> result = null;
        try {
            switch (method) {
                case "submitProcess":
                    result = callITSMSubmitProcess(apiEndpointDTO, itsmOrder, businessEventContextDTO);
                    break;
                case "createProcess":
                    result = callITSMCreateProcess(apiEndpointDTO, itsmOrder, businessEventContextDTO);
                    break;
                case "completeProcess":
                    result = callITSMCreateProcess(apiEndpointDTO, itsmOrder, businessEventContextDTO);
                    break;
            }
            // 处理结果
            if (result.isSuccess()) {
                // 更新日志状态
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.SUCESS.getCode(), "");
            } else {
                LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + result + "]");
                // 更新日志状态
                apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), result.getMsg());
            }
        } catch (Exception e) {
            // 更新日志状态
            apiLogService.updateApiLog(apiLogId, ApiType.API_LOG_CODE.ERROR.getCode(), e.getMessage());
            LogUtil.info("==== !【ITSM Order】Notify ITSM Failed：[" + JSONObject.toJSONString(result) + "]");
            LogUtil.info(JSONObject.toJSONString(e));
            e.printStackTrace();
        }
        return result;
    }

    private PciApiEndpointDTO getApiEndpointDTO(List<PciApiEndpointDTO> apiEndpointDTOS, BusinessEventContextDTO businessEventContextDTO) {
        String operation = businessEventContextDTO.getOrderType();
        List<PciApiEndpointDTO> apiList = apiEndpointDTOS.stream()
                .filter((endpoint -> endpoint.getApiType().equals(ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + businessEventContextDTO.getOrderType())))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(apiList)) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口");
            F2CException.throwException("接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能没有对应的API接口，请检查");
        }
        if (apiList.size() > 1) {
            LogUtil.info("当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口");
            F2CException.throwException("接口调用异常，当前API账号下 " + ApiType.ACCOUNT_TYPE.PROCESS.name() + "_" + operation + " 功能对应了多个API接口，请检查");
        }
        return apiList.get(0);
    }
    public ITSMOrder buildITSMOrderModel(BusinessEventContextDTO businessEventContextDTO, PciApiEndpointDTO apiEndpointDTO) {
        LogUtil.info("【buildITSMOrderModel】-【itsmOrder：" + JSONObject.toJSONString(businessEventContextDTO) + "】");
        if (Objects.isNull(businessEventContextDTO)) {
            return null;
        }
        final String orderId = businessEventContextDTO.getBusinessKey();
        final String orderType = businessEventContextDTO.getOrderType();
        // 订单主表的字段是固定映射的，订单明细的映射按照字段映射配置进行封装
        ITSMOrder itsmOrder = new ITSMOrder();
        itsmOrder.setOrderId(orderId);
        itsmOrder.setOperType(orderType);
        itsmOrder.setOperDesc(Translator.get(businessEventContextDTO.getProcessName()));
        customITSMOrderModel(itsmOrder, apiEndpointDTO, businessEventContextDTO);
        // 按照参数映射配置 封装数据
        List<BusinessDetail> businessDetails = businessEventContextDTO.getBusinessDetails();
        if (CollectionUtils.isNotEmpty(businessDetails)) {
            for (BusinessDetail businessDetail : businessDetails) {
                ITSMOrderItem itsmOrderItem = new ITSMOrderItem();
                itsmOrderItem.setDataBase(false);
                customITSMOrderItem(itsmOrderItem, businessDetail, businessEventContextDTO);
                // 单个订单的 属性集
                List<ITSMOrderItemAttribute> itemAttrs = new ArrayList<>();
                Map<String, PciApiParameterMapping> targetFieldParameterMappings = apiEndpointDTO.getTargetFieldParameterMappings();
                for (String targetKey : targetFieldParameterMappings.keySet()) {
                    ITSMOrderItemAttribute attribute = new ITSMOrderItemAttribute();
                    attribute.setName(targetKey);
                    PciApiParameterMapping parameterMapping = targetFieldParameterMappings.get(targetKey);
                    Optional<BusinessDetailItem> detailItemOptional = businessDetail.getDetailItems().stream().filter(item -> Objects.equals(item.getKey(), parameterMapping.getOriginField())).findFirst();
                    customITSMOrderItemAttribute(attribute, detailItemOptional, businessEventContextDTO, parameterMapping);
                    itemAttrs.add(attribute);
                }
                itsmOrderItem.setItemAttrs(itemAttrs);
                itsmOrder.getItems().add(itsmOrderItem);
            }
        }
        LogUtil.info("【buildITSMOrderModel】-【itsmOrder：" + JSONObject.toJSONString(itsmOrder) + "】");
        return itsmOrder;
    }

    /**
     * 自定义账号API参数映射赋值给itsmOrderAttribute
     */
    protected void customITSMOrderItemAttribute(ITSMOrderItemAttribute attribute, Optional<BusinessDetailItem> detailItemOptional, BusinessEventContextDTO businessEventContextDTO, PciApiParameterMapping parameterMapping) {
        // 若字段映射中ITSM 源字段值在 detailItems 中存在对应字段，则取对应字段的值作为目标属性值，否则直接取源字段值作为目标属性值
        if (detailItemOptional.isPresent()) {
            BusinessDetailItem detailItem = detailItemOptional.get();
            attribute.setValue(StringUtils.isNoneBlank(detailItem.getValue()) ? detailItem.getValue() : "Unknown");
        } else {
            attribute.setValue(parameterMapping.getOriginField());
        }
    }

    /**
     * 自定义ITSMOrderItem赋值方法
     */
    protected void customITSMOrderItem(ITSMOrderItem itsmOrderItem, BusinessDetail businessDetail, BusinessEventContextDTO businessEventContextDTO) {

    }

    /**
     * 自定义ITSMOrder赋值方法
     */
    protected void customITSMOrderModel(ITSMOrder itsmOrder, PciApiEndpointDTO apiEndpointDTO, BusinessEventContextDTO businessEventContextDTO) {

    }

    /**
     * 调用ITSM接口创建流程信息
     */
    public abstract <T> ResponseResult<T> callITSMCreateProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO);

    /**
     * 调用ITSM接口给OA流程返回信息
     */
    public abstract <T> ResponseResult<T> callITSMSubmitProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO);

    /**
     * 调用ITSM接口给OA已有工单发送流程对应任务的执行结果
     */
    public abstract <T> ResponseResult<T> callITSMCompleteProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO);

}
