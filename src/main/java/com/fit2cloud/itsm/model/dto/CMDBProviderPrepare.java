package com.fit2cloud.itsm.model.dto;

import java.util.List;

public class CMDBProviderPrepare {
    private List<APICredentialItem> credentialItemList;
    private PciApiEndpointDTO apiEndpointDTO;

    public List<APICredentialItem> getCredentialItemList() {
        return credentialItemList;
    }

    public void setCredentialItemList(List<APICredentialItem> credentialItemList) {
        this.credentialItemList = credentialItemList;
    }

    public PciApiEndpointDTO getApiEndpointDTO() {
        return apiEndpointDTO;
    }

    public void setApiEndpointDTO(PciApiEndpointDTO apiEndpointDTO) {
        this.apiEndpointDTO = apiEndpointDTO;
    }
}
