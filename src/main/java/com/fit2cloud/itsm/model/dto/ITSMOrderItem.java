package com.fit2cloud.itsm.model.dto;

import java.util.List;

public class ITSMOrderItem {


    private String orderItemId;

    private Boolean isDataBase;

    private List<ITSMOrderItemAttribute> itemAttrs;


    public ITSMOrderItem() {
    }


    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Boolean getDataBase() {
        return isDataBase;
    }

    public void setDataBase(Boolean dataBase) {
        isDataBase = dataBase;
    }

    public List<ITSMOrderItemAttribute> getItemAttrs() {
        return itemAttrs;
    }

    public void setItemAttrs(List<ITSMOrderItemAttribute> itemAttrs) {
        this.itemAttrs = itemAttrs;
    }

}
