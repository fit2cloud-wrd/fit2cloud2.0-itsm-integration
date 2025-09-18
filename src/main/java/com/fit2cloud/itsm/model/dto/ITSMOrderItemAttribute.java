package com.fit2cloud.itsm.model.dto;

public class ITSMOrderItemAttribute {

    /* 订单 属性名id */
    private String name;

    /* 订单 属性名称 */
    private String label;

    /* 订单 属性值 */
    private String value;

    /* 订单 属性配置 */
    private String applyOrConfiguration; //apply configuration


    public ITSMOrderItemAttribute() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getApplyOrConfiguration() {
        return applyOrConfiguration;
    }

    public void setApplyOrConfiguration(String applyOrConfiguration) {
        this.applyOrConfiguration = applyOrConfiguration;
    }

}
