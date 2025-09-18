package com.fit2cloud.itsm.model.dto;

import com.fit2cloud.itsm.model.PciApiParameterMapping;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;

public class PciApiParameterMappingDTO extends PciApiParameterMapping {
    private String time;

    private String originFieldName;

    private String endpoint;

    private String apiType;

    private String apiEndpoint;

    private String providerFactoryId;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOriginFieldName() {
        return originFieldName;
    }

    public void setOriginFieldName(String originFieldName) {
        this.originFieldName = originFieldName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getProviderFactoryId() {
		return providerFactoryId;
	}

    public void setProviderFactoryId(String providerFactoryId) {
		this.providerFactoryId = providerFactoryId;
	}
}
