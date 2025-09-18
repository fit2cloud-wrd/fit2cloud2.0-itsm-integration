package com.fit2cloud.itsm.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ITSMOrder {

    private String orderId;

    private String itsmOrderId;

    /**
     * 订单类型
     */
    private String operType;

    /**
     * 流程名称
     */
    private String operDesc;

    private String applyUser;

    private List<ITSMOrderItem> items = new ArrayList<>();

    //ITSM中对应流程模型的ID，非必用，目前蓝凌（Landray）OA在使用
    private String itsmModelId;

    public String getItsmModelId() {
        return itsmModelId;
    }

    public void setItsmModelId(String itsmModelId) {
        this.itsmModelId = itsmModelId;
    }

    public ITSMOrder() {
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItsmOrderId() {
        return itsmOrderId;
    }

    public void setItsmOrderId(String itsmOrderId) {
        this.itsmOrderId = itsmOrderId;
    }

    public List<ITSMOrderItem> getItems() {
        return items;
    }

    public void setItems(List<ITSMOrderItem> items) {
        this.items = items;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperDesc() {
        return operDesc;
    }

    public void setOperDesc(String operDesc) {
        this.operDesc = operDesc;
    }
}
