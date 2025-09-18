package com.fit2cloud.itsm.dao;

import com.fit2cloud.itsm.model.PciApiAccount;
import com.fit2cloud.itsm.model.PciApiEndpoint;
import com.fit2cloud.itsm.model.PciApiParameterMapping;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;
import com.fit2cloud.itsm.model.dto.PciApiParameterMappingDTO;
import com.fit2cloud.itsm.model.request.PciApiAccountRequest;
import com.fit2cloud.itsm.model.request.PciApiEndpointRequest;
import com.fit2cloud.itsm.model.request.PciApiParameterMappingRequest;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ExtPciApiAccountMapper {
    List<PciApiAccountDTO> getApiAccountList(@Param("record") PciApiAccountRequest apiAccountRequest);

    List<PciApiEndpointDTO> getApiEndpointList(@Param("record") PciApiEndpointRequest apiEndpointRequest);

    List<PciApiParameterMappingDTO> getApiParameterMapppingList(@Param("record") PciApiParameterMappingRequest parameterMappingRequest);

    List<PciApiAccount> getApiAccountAll(@Param("record") PciApiAccountRequest apiAccountRequest);

    List<PciApiEndpoint> getApiEndPointListByApiAccountId(@Param("accountId") String accountId);

    List<PciApiParameterMapping> getApiParamaterMappingByApiId(@Param("apiId") String apiId);

    void deleteApiParamaterMappingByApiId(@Param("apiId") String apiId);

    /**
     * 通用查询
     *
     * @return
     */
    List<Map<String, Object>> selectPublicItemList(@Param(value = "sqlStr") String sqlStr);

}