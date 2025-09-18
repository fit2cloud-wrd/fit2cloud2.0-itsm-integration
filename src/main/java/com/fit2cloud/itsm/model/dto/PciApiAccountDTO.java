package com.fit2cloud.itsm.model.dto;

import com.fit2cloud.itsm.model.PciApiAccount;

import java.io.Serializable;

public class PciApiAccountDTO extends PciApiAccount implements Serializable {

    private String providerFactoryName;

    private String systemTypeName;

    private Integer apiEndpointCount;

    public Integer getApiEndpointCount() {
        return apiEndpointCount;
    }

    public void setApiEndpointCount(Integer apiEndpointCount) {
        this.apiEndpointCount = apiEndpointCount;
    }

    public String getProviderFactoryName() {
        return providerFactoryName;
    }

    public void setProviderFactoryName(String providerFactoryName) {
        this.providerFactoryName = providerFactoryName;
    }

    public String getSystemTypeName() {
        return systemTypeName;
    }

    public void setSystemTypeName(String systemTypeName) {
        this.systemTypeName = systemTypeName;
    }
}