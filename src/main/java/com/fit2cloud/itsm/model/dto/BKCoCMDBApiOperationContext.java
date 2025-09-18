package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.itsm.common.constants.ApiType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Builder
public class BKCoCMDBApiOperationContext {
    private ApiType.API_TYPE apiType;
    private List<Integer> bkInstIds;
    private List<PciApiEndpointDTO> apiEndpointDTOS;
    private JSONObject resourceJsonObj;
    private Map<String, String> credentialMap;
    private Function<PciApiEndpointDTO, String> createLogFun;


    private List<APICredentialItem> credentialItems;

}
