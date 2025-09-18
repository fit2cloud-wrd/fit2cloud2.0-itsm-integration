package com.fit2cloud.itsm.model.dto;

import com.fit2cloud.itsm.model.PciApiEndpoint;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PciApiEndpointDTO extends PciApiEndpoint {
    private String systemType;

    private String systemTypeName;

    private String accountName;

    private String apiTypeName;

    private Integer parameterMappingCount;

    private String time;

    private String providerFactoryId;

    private Map<String, PciApiParameterMapping> originFieldParameterMappings = new HashMap<>();

    private Map<String, PciApiParameterMapping> targetFieldParameterMappings = new HashMap<>();

    private List<PciApiParameterMapping> parameterMappingList;

    private String apiEndpoint;

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemTypeName() {
        return systemTypeName;
    }

    public void setSystemTypeName(String systemTypeName) {
        this.systemTypeName = systemTypeName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getApiTypeName() {
        return apiTypeName;
    }

    public void setApiTypeName(String apiTypeName) {
        this.apiTypeName = apiTypeName;
    }

    public Integer getParameterMappingCount() {
        return parameterMappingCount;
    }

    public void setParameterMappingCount(Integer parameterMappingCount) {
        this.parameterMappingCount = parameterMappingCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setParameterMappingList(List<PciApiParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
        if (CollectionUtils.isNotEmpty(parameterMappingList)) {
            this.parameterMappingList.forEach(pciApiParameterMapping -> {
                originFieldParameterMappings.put(pciApiParameterMapping.getOriginField(), pciApiParameterMapping);
                targetFieldParameterMappings.put(pciApiParameterMapping.getTargetField(), pciApiParameterMapping);
            });
        }
    }

    public Map<String, PciApiParameterMapping> getOriginFieldParameterMappings() {
        return originFieldParameterMappings;
    }

    public Map<String, PciApiParameterMapping> getTargetFieldParameterMappings() {
        return targetFieldParameterMappings;
    }

    public List<PciApiParameterMapping> getParameterMappingList() {
        return parameterMappingList;
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
