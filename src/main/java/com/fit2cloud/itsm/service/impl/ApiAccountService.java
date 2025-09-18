package com.fit2cloud.itsm.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.CommonResourcePoolTagMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.server.service.TagService;
import com.fit2cloud.commons.server.utils.IDGenerator;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.DateUtil;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.common.constants.*;
import com.fit2cloud.itsm.dao.*;
import com.fit2cloud.itsm.common.utils.HttpClientConfig;
import com.fit2cloud.itsm.common.utils.HttpClientUtil;
import com.fit2cloud.itsm.common.utils.ProviderFactoryUtil;
import com.fit2cloud.itsm.integration.ITSMIntegration;
import com.fit2cloud.itsm.model.*;
import com.fit2cloud.itsm.model.dto.*;
import com.fit2cloud.itsm.model.request.PciApiAccountAddRequest;
import com.fit2cloud.itsm.model.request.PciApiAccountRequest;
import com.fit2cloud.itsm.model.request.PciApiEndpointRequest;
import com.fit2cloud.itsm.model.request.PciApiParameterMappingRequest;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import javax.xml.namespace.QName;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.*;

@Component
public class ApiAccountService {

    @Resource
    private PciApiAccountMapper pciApiAccountMapper;

    @Resource
    private PciApiEndpointMapper pciApiEndpointMapper;

    @Resource
    private PciApiParameterMappingMapper pciApiParameterMappingMapper;

    @Resource
    private ExtPciApiAccountMapper extPciApiAccountMapper;

    @Resource
    private PciApiParameterMappingDictionaryMapper pciApiParameterMappingDictionaryMapper;

    @Resource
    private TagService tagService;

    @Resource
    private CommonResourcePoolTagMapper commonResourcePoolTagMapper;

    /****************************** API 账号 start **********************************/

    /**
     * 获取厂商列表
     * @return
     */
    public List<Map<String, String>> getVendorList() {
        List<Map<String, String>> result = new ArrayList<>();

        List<VendorDTO> vendorDTOList = ProviderFactoryUtil.getVendorDTOList();
        if (CollectionUtils.isNotEmpty(vendorDTOList)) {
            vendorDTOList.forEach(vendor -> {
                Map<String, String> d = new HashMap<>();
                d.put("name", vendor.getName());
                d.put("id", vendor.getId());
                result.add(d);
            });
        }

        return result;
    }

    /**
     * 获取API账号类型
     * @return
     */
    public List<Map<String, String>> getAccountTypeList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (ApiType.ACCOUNT_TYPE type:ApiType.ACCOUNT_TYPE.values()) {
            Map<String, String> d = new HashMap<>();
            d.put("value", type.name());
            d.put("lable", type.getName());
            d.put("systemType", type.name());
            result.add(d);
        }
        return result;
    }

    /**
     * 获取当前厂商支持的版本
     * @param providerId
     * @return
     */
    public List<Map<String, String>> getVersionList(String providerId) {
        /*待返回数据*/
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();
        if (providerMap.containsKey(providerId)) {
            ITSMIntegration providerFactory = providerMap.get(providerId);
            if (providerFactory != null) {
                List<String> versionList = providerFactory.getSupportVersion();
                for (String version:versionList) {
                    Map<String, String> d = new HashMap<>();
                    d.put("value", version);
                    d.put("lable", version);
                    result.add(d);
                }
            }
        }

        /*返回数据*/
        return result;
    }

    /**
     * api账号列表
     * @param apiAccountRequest
     * @return
     */
    public List<PciApiAccountDTO> getApiAccountList(PciApiAccountRequest apiAccountRequest) {
        List<PciApiAccountDTO> pciApiAccountDTOList = extPciApiAccountMapper.getApiAccountList(apiAccountRequest);

        Optional.ofNullable(pciApiAccountDTOList).orElse(new ArrayList<>()).forEach(apiAccount -> {
            apiAccount.setSystemTypeName(ApiType.ACCOUNT_TYPE.getNameByType(apiAccount.getSystemType()));
            Map<String, VendorDTO> vendorDTOMap = ProviderFactoryUtil.getVendorDTOMap();
            VendorDTO vendorDTO = vendorDTOMap.get(apiAccount.getProviderFactoryId());
            if (vendorDTO != null) {
                apiAccount.setProviderFactoryName(vendorDTO.getName());
            }
        });

        return pciApiAccountDTOList;
    }


    public PciApiAccount getApiAccountById(String apiAccountId) {
        return pciApiAccountMapper.selectByPrimaryKey(apiAccountId);
    }

    /**
     * 获取API账号全量数据
     * @return
     */
    public List<PciApiAccount> getApiAccountAll() {
        PciApiAccountRequest apiAccountRequest = new PciApiAccountRequest();
        return extPciApiAccountMapper.getApiAccountAll(apiAccountRequest);
    }

    public void autoSync(String id, Boolean autoSync) {
        PciApiAccount pciApiAccount = new PciApiAccount();
        pciApiAccount.setId(id);
        pciApiAccount.setAutoSync(autoSync);
        pciApiAccount.setSyncStatus("WAIT");
        pciApiAccountMapper.updateByPrimaryKeySelective(pciApiAccount);
    }

    /**
     * 检查账号名称是否重复
     * @param name
     * @return
     */
    public Boolean checkoutAccountName(String name) {
        PciApiAccountExample pciApiAccountExample = new PciApiAccountExample();
        pciApiAccountExample.createCriteria().andNameEqualTo(name);
        if (pciApiAccountMapper.countByExample(pciApiAccountExample) > 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取账号的默认接口列表
     * @param providerId
     * @return
     */
    public String getDefaultApiList(String providerId) {
        String jsonContent = "";

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();
        if (providerMap.containsKey(providerId)) {
            ITSMIntegration providerFactory = providerMap.get(providerId);
            if (providerFactory != null) {
                jsonContent = providerFactory.getDefaultApiListJson();
            }
        }

        return jsonContent;
    }

    /**
     * 获取厂商的API账号认证信息模板
     * @param providerId
     * @return
     */
    public String getCreditial(String providerId) {
        String jsonContent = "";

        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();
        if (providerMap.containsKey(providerId)) {
            ITSMIntegration providerFactory = providerMap.get(providerId);
            if (providerFactory != null) {
                jsonContent = providerFactory.getCreditialJson();
            }
        }

        return jsonContent;
    }

    /**
     * 根据API账号类型 获取当前启用的API账号
     * @param systemType
     * @return
     */
    public PciApiAccountDTO getDefaultApiAccount(String systemType) {
        // 获取默认启用的API账号
        PciApiAccountRequest request = new PciApiAccountRequest();
        request.setSystemType(systemType);
        request.setEnableFlag(ApiType.ENABLE_STATUS.ENABLE.getCode());
        List<PciApiAccountDTO> pciApiAccountDTOList = this.getApiAccountList(request);
        if (CollectionUtils.isNotEmpty(pciApiAccountDTOList)) {
            return pciApiAccountDTOList.get(0);
        }

        return null;
    }

    /**
     * 新增API账号
     * @param request
     * @param applyUser
     */
    @Transactional
    public void addApiAccount(PciApiAccountAddRequest request, String applyUser) {
        JSONArray defaultApiList = JSONArray.parseArray(request.getDefaultApiList());

        // 根据选择的厂商及版本查询内置的 测试地址
        Map<String, ITSMIntegration> providerFactoryMap = ProviderFactoryUtil.getProviderMap();
        if (providerFactoryMap.containsKey(request.getProviderFactoryId())) {
            ITSMIntegration providerFactory = providerFactoryMap.get(request.getProviderFactoryId());
            request.setTestApiEndpoint(providerFactory.testDefaultApiUrl());
        }

        // 插入账号数据
        PciApiAccount apiAccount = new PciApiAccount();
        BeanUtils.copyBean(apiAccount, request);
        String apiAccountId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_ACCOUNT_ID_PREFIX, SessionUtils.getUser().getWorkspaceId());
        apiAccount.setId(apiAccountId);
        apiAccount.setStatus(ApiType.ACCOUNT_STATUS.UN_VALIDATE.getValue());
        apiAccount.setEnableFlag(ApiType.ENABLE_STATUS.DISABLE.getCode());
        pciApiAccountMapper.insert(apiAccount);

        // 插入 api 数据
        if (!defaultApiList.isEmpty()) {
            for (int i = 0; i < defaultApiList.size(); i++) {
                JSONObject defaultApi = defaultApiList.getJSONObject(i);
                PciApiEndpoint pciApiEndpoint = new PciApiEndpoint();
                String apiEndpointId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_ENDPOINT_ID_PREFIX, SessionUtils.getUser().getWorkspaceId());
                pciApiEndpoint.setId(apiEndpointId);
                pciApiEndpoint.setApiType(defaultApi.getString("apiType"));
                pciApiEndpoint.setApiAccount(apiAccountId);
                pciApiEndpoint.setEndpoint(defaultApi.getString("endpoint"));
                pciApiEndpoint.setMethod(defaultApi.getString("method"));
                pciApiEndpoint.setUpdateTime(new Date());
                pciApiEndpointMapper.insert(pciApiEndpoint);

                // 插入API参数 数据
                JSONArray parameterJsonArray = defaultApi.getJSONArray("parameter");
                if (!parameterJsonArray.isEmpty()) {
                    parameterJsonArray.stream().forEach(item -> {
                        JSONObject parameter = (JSONObject) item;
                        PciApiParameterMapping pciApiParameterMapping = new PciApiParameterMapping();
                        pciApiParameterMapping.setId(IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_PARAMETER_MAPPING_ID_PREFIX, SessionUtils.getUser().getWorkspaceId()));
                        pciApiParameterMapping.setApiId(apiEndpointId);
                        pciApiParameterMapping.setOriginField(parameter.getString("origin_field"));
                        pciApiParameterMapping.setOriginFieldType(parameter.getString("oirigin_field_type"));
                        pciApiParameterMapping.setTargetField(parameter.getString("target_field"));
                        pciApiParameterMapping.setTargetFieldType(parameter.getString("target_field_type"));
                        pciApiParameterMapping.setDescription(parameter.getString("description"));
                        pciApiParameterMapping.setOriginFieldSource(parameter.getString("origin_field_source"));
                        pciApiParameterMapping.setOriginFieldTable(parameter.getString("origin_field_table"));
                        pciApiParameterMapping.setOriginFieldName(parameter.getString("origin_field_name"));
                        pciApiParameterMapping.setUpdateTime(new Date());
                        pciApiParameterMappingMapper.insert(pciApiParameterMapping);
                    });
                }
            }
        }
    }

    /**
     * API账号启用/禁用
     * @param id
     */
    public void enable(String id) {
        // 删除账号数据
        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(id);

        // 校验当前类型下是否已存在启用的API账号，若已存在则返回异常
        if (pciApiAccount == null) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }

        PciApiAccount updateAccount = new PciApiAccount();
        updateAccount.setId(id);
        if (!Objects.equals(pciApiAccount.getEnableFlag(), ApiType.ENABLE_STATUS.ENABLE.getCode())) {
            PciApiAccount enableApiAccount = getDefaultApiAccount(pciApiAccount.getSystemType());
            if (enableApiAccount != null) {
                F2CException.throwException(Translator.get("i18n_ex_already_exist_enable_api_account"));
            }
            // 启用
            updateAccount.setEnableFlag(ApiType.ENABLE_STATUS.ENABLE.getCode());
        } else {
            // 禁用
            updateAccount.setEnableFlag(ApiType.ENABLE_STATUS.DISABLE.getCode());
        }

        pciApiAccountMapper.updateByPrimaryKeySelective(updateAccount);
    }

    public Boolean validate(String apiId) {
        Boolean result = Boolean.FALSE;

        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(apiId);
        // 通过测试接口校验账号的有效性
        if (pciApiAccount != null) {
            switch (pciApiAccount.getProviderFactoryId()){
                case "ULTRAPOWER":
                    Service service = new Service();
                    Call call = null;
                    try {
                        call = (Call) service.createCall();
                        call.setTargetEndpointAddress(pciApiAccount.getApiEndpoint() + "/" + pciApiAccount.getTestApiEndpoint());
                        call.setOperationName(QName.valueOf("getResourceCount"));
                        call.setReturnType(XMLType.XSD_STRING);
                        call.addParameter("categorys",XMLType.XSD_STRING, ParameterMode.IN);
                        Object[] parAry = new Object[] {"服务器"};
                        call.invoke(parAry);
                        break;
                    } catch (ServiceException | RemoteException e) {
                        throw new RuntimeException(e);
                    }
                case "YOUYUN":
                case "YOUWEI":
                    String itsmApiKey = null, itsmTenantId = null;
                    for (APICredentialItem item : getCredentialTemplate(pciApiAccount)) {
                        if (Objects.equals(item.getName(), "itsmApiKey")) {
                            itsmApiKey = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "itsmTenantId")) {
                            itsmTenantId = item.getDefaultValue();
                        }
                    }
                    try {
                        /*执行请求*/
                        String url = pciApiAccount.getApiEndpoint() + "/" + pciApiAccount.getTestApiEndpoint() + "?apikey=" + itsmApiKey + "&tenant_id=" + itsmTenantId;
                        LogUtil.info(" Request url：" + url);
                        HttpClientConfig config = HttpClientUtil.auth();
                        String resultJson = HttpClientUtil.get(url, config);
                        result = Boolean.TRUE;
                        break;
                    } catch (Exception exception) {
                        updateApiStatusById("2", apiId);
                        throw new RuntimeException(exception.getMessage());
                    }
                case "CLOUDWISE":
                    String accountId = null, userId = null;
                    for (APICredentialItem item : getCredentialTemplate(pciApiAccount)) {
                        if (Objects.equals(item.getName(), "cloudwiseAccountId")) {
                            accountId = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "cloudwiseUserId")) {
                            userId = item.getDefaultValue();
                        }
                    }
                    try {
                        /*执行请求*/
                        String url = pciApiAccount.getApiEndpoint() + "/" + pciApiAccount.getTestApiEndpoint();
                        LogUtil.info(" Request url：" + url);
                        HttpClientConfig config = HttpClientUtil.auth();
                        config.addHeader("accountId", accountId);
                        config.addHeader("userId", userId);
                        String resultJson = HttpClientUtil.get(url, config);
                        result = Boolean.TRUE;
                        break;
                    } catch (Exception exception) {
                        updateApiStatusById("2", apiId);
                        throw new RuntimeException(exception.getMessage());
                    }
                case "Lenovo":
                    String token = null;
                    for (APICredentialItem item : getCredentialTemplate(pciApiAccount)) {
                        if (Objects.equals(item.getName(), "token")) {
                            token = item.getDefaultValue();
                        }
                    }
                    try {
                        /*执行请求*/
                        String url = pciApiAccount.getApiEndpoint() + "/" + pciApiAccount.getTestApiEndpoint();
                        LogUtil.info(" Request url：" + url);
                        HttpClientConfig config = HttpClientUtil.auth();

                        config.addHeader("Authorization", String.format("Bearer %s",token));
                        String resultJson = null;
                        try{
                            resultJson = HttpClientUtil.post(url, "",config);
                        }catch (Exception e){
                            resultJson = e.getMessage();
                        }

                        //联想，没有校验token是否有效接口；反馈：调用后返回没有请求参数就是token有效，反之无效；
                        if (StringUtils.isNotBlank(resultJson) && resultJson.contains("BAD_REQUEST")){
                            result = Boolean.TRUE;
                        }else{
                            result = Boolean.FALSE;
                        }

                        break;
                    } catch (Exception exception) {
                        updateApiStatusById("2", apiId);
                        throw new RuntimeException(exception.getMessage());
                    }
                case "LANDRAY":
                    try {
                        /*执行请求*/
                        String url = pciApiAccount.getApiEndpoint() + pciApiAccount.getTestApiEndpoint();
                        LogUtil.info(" Request url：" + url);
                        String resultJson = HttpClientUtil.get(url, new HttpClientConfig());
                        result = Boolean.TRUE;
                        break;
                    } catch (Exception e) {
                        updateApiStatusById("2", apiId);
                        throw new RuntimeException(e);
                    }
                case "LANJINGCOMPANY":
                    String bk_username = null, appCode = null,appSecret = null;
                    for (APICredentialItem item : getCredentialTemplate(pciApiAccount)) {
                        if (Objects.equals(item.getName(), "username")) {
                            bk_username = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "appCode")) {
                            appCode = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "appSecret")) {
                            appSecret = item.getDefaultValue();
                        }
                    }
                    try {
                        String url = pciApiAccount.getApiEndpoint() + "/" + pciApiAccount.getTestApiEndpoint()+ "/list_hosts_without_biz/";
                        LogUtil.info(" Request url：" + url);
                        HttpClientConfig config = HttpClientUtil.auth();
                        JSONObject editCMDBResourceReq = new JSONObject();
                        editCMDBResourceReq.put("bk_app_code", appCode);
                        editCMDBResourceReq.put("bk_app_secret",appSecret);
                        editCMDBResourceReq.put("bk_username", bk_username);
                        editCMDBResourceReq.put("page",new JSONObject(){{
                            put("start",0);
                            put("limit",10);
                        }});
                        String resultJson = HttpClientUtil.post(url,editCMDBResourceReq.toJSONString(), config);
                        JSONObject resultObj = JSONObject.parseObject(resultJson);
                        LogUtil.info(" 蓝鲸企业版本校验返回 ：" + resultObj);
                        /*if(Objects.nonNull(resultObj.getJSONObject("data")) && resultObj.getJSONObject("data").getIntValue("count") > 0 && resultObj.getBoolean("result")){
                            result = Boolean.TRUE;
                        }else{
                            result = Boolean.FALSE;
                        }*/
                        result = Boolean.TRUE;
                        break;
                    } catch (Exception exception) {
                        updateApiStatusById("2", apiId);
                        throw new RuntimeException(exception.getMessage());
                    }
                case "LANJING":
                    String username = null, password = null,globalPath = null,authPath = null,tokenLimit = null;
                    for (APICredentialItem item : getCredentialTemplate(pciApiAccount)) {
                        if (Objects.equals(item.getName(), "username")) {
                            username = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "password")) {
                            password = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "globalPath")) {
                            globalPath = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "authPath")) {
                            authPath = item.getDefaultValue();
                        }
                        if (Objects.equals(item.getName(), "tokenLimit")) {
                            tokenLimit = item.getDefaultValue();
                        }
                    }
                default:break;
            }
        }

        // 更新API账号状态
        if (result) {
            updateApiStatusById("1", apiId);
        } else {
            updateApiStatusById("2", apiId);
        }

        return result;
    }

    private void updateApiStatusById(String status, String apiId) {
        PciApiAccount pciApiAccount = new PciApiAccount();
        pciApiAccount.setId(apiId);
        pciApiAccount.setStatus(status);

        pciApiAccountMapper.updateByPrimaryKeySelective(pciApiAccount);
    }

    public void updatePciApiAccount(PciApiAccountDTO pciApiAccount) {
        pciApiAccountMapper.updateByPrimaryKeySelective(pciApiAccount);
    }


    public List<APICredentialItem> getCredentialTemplate(PciApiAccount pciApiAccount) {
        List<APICredentialItem> credentialItems = new ArrayList<>();

        if (pciApiAccount != null) {
            String credential = pciApiAccount.getCredential();
            JSONArray jsonArray = JSONArray.parseArray(credential);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                APICredentialItem item = new APICredentialItem();
                item.setName(jsonObject.getString("name"));
                item.setLabel(jsonObject.getString("label"));
                if (StringUtils.equalsAnyIgnoreCase(jsonObject.getString("type"), "text", "password")) {
                    item.setType(APICredentialItem.InputType.text);
                } else {
                    item.setType(APICredentialItem.InputType.number);
                }
                item.setDefaultValue(jsonObject.getString("defaultValue"));
                credentialItems.add(item);
            }
        }

        return credentialItems;
    }

    /**
     * 删除API账号
     * @param id
     */
    @Transactional
    public void delete(String id) {
        // 删除账号数据
        pciApiAccountMapper.deleteByPrimaryKey(id);

        // 查询 账号下的 api数据
        List<PciApiEndpoint> pciApiEndpointList = extPciApiAccountMapper.getApiEndPointListByApiAccountId(id);
        if (CollectionUtils.isNotEmpty(pciApiEndpointList)) {
            pciApiEndpointList.forEach(pciApiEndpoint -> {
                // 删除api数据下的 参数数据
                extPciApiAccountMapper.deleteApiParamaterMappingByApiId(pciApiEndpoint.getId());

                // 删除账号下的 api数据
                pciApiEndpointMapper.deleteByPrimaryKey(pciApiEndpoint.getId());
            });
        }
    }

    /**
     * 批量删除API账号
     * @param ids
     */
    @Transactional
    public void batchDelete(List<String> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            delete(id);
        });
    }

    /**
     * 更新API账号
     *
     * @param request
     * @param applyUser
     */
    @Transactional
    public void updateApiAccount(PciApiAccountAddRequest request, String applyUser) {
        // 根据选择的厂商及版本查询内置的 测试地址
        Map<String, ITSMIntegration> providerFactoryMap = ProviderFactoryUtil.getProviderMap();
        if (providerFactoryMap.containsKey(request.getProviderFactoryId())) {
            ITSMIntegration providerFactory = providerFactoryMap.get(request.getProviderFactoryId());
            request.setTestApiEndpoint(providerFactory.testDefaultApiUrl());
        }

        // 更新账号数据
        PciApiAccount apiAccount = new PciApiAccount();
        BeanUtils.copyBean(apiAccount, request);
        apiAccount.setStatus(ApiType.ACCOUNT_STATUS.UN_VALIDATE.getValue());
        pciApiAccountMapper.updateByPrimaryKeySelective(apiAccount);
    }

    /****************************** API 账号 end **********************************/

    /****************************** API 地址 start **********************************/

    /**
     * 根据API账号 以及 功能类型获取 对应的API地址（最多只有一个）
     * @param accountId
     * @param apiType
     * @return
     */
    public PciApiEndpointDTO getApiEndPointByApiType(String accountId, String apiType) {
        // 获取默认启用的API账号
        PciApiEndpointRequest request = new PciApiEndpointRequest();
        request.setApiAccount(accountId);
        request.setApiType(apiType);
        List<PciApiEndpointDTO> apiEndpointDTOS = this.getApiEndpointList(request);
        if (CollectionUtils.isNotEmpty(apiEndpointDTOS)) {
            return apiEndpointDTOS.get(0);
        }

        return null;
    }

    /**
     * 获取API账号下的API地址列表
     * @param accountId
     * @return
     */
    public List<PciApiEndpointDTO> getApiListByAccountId(String accountId) {
        /*待返回数据*/
        List<PciApiEndpointDTO> pciApiEndpointDTOList = new ArrayList<>();

        /*组织数据*/
        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(accountId);

        List<PciApiEndpoint> pciApiEndpointList = extPciApiAccountMapper.getApiEndPointListByApiAccountId(accountId);
        if (CollectionUtils.isNotEmpty(pciApiEndpointList)) {
            pciApiEndpointList.forEach(pciApiEndpoint->{
                PciApiEndpointDTO pciApiEndpointDTO = new PciApiEndpointDTO();
                BeanUtils.copyBean(pciApiEndpointDTO, pciApiEndpoint);
                List<PciApiParameterMapping> pciApiParameterMappings = extPciApiAccountMapper.getApiParamaterMappingByApiId(pciApiEndpoint.getId());
                pciApiEndpointDTO.setParameterMappingList(pciApiParameterMappings);
                pciApiEndpointDTO.setSystemType(pciApiAccount.getSystemType());
                pciApiEndpointDTO.setSystemTypeName(ApiType.ACCOUNT_TYPE.getNameByType(pciApiAccount.getSystemType()));
                pciApiEndpointDTO.setApiEndpoint(pciApiAccount.getApiEndpoint());
                if(StringUtils.isNotEmpty(pciApiAccount.getProviderFactoryId()) && "LANJINGCOMPANY".equals(pciApiAccount.getProviderFactoryId())){
                    pciApiEndpointDTO.setApiTypeName(BKCompanyApiType.BK_COMPANY_API_TYPE.getNameByType(pciApiEndpoint.getApiType()));
                }else{
                    pciApiEndpointDTO.setApiTypeName(ApiType.API_TYPE.getNameByType(pciApiEndpoint.getApiType()));
                }
                pciApiEndpointDTOList.add(pciApiEndpointDTO);
            });
        }

        /*返回数据*/
        return pciApiEndpointDTOList;
    }

    /**
     * 获取 全量 API功能类型列表
     * @return
     */
    public List<Map<String, String>> getApiTypeList() {
        /*待返回数据*/
        List<Map<String, String>> result = new ArrayList<>();

        for (ApiType.API_TYPE type:ApiType.API_TYPE.values()) {
            Map<String, String> d = new HashMap<>();
            d.put("value", type.name());
            d.put("lable", type.getName());
            d.put("apiType", type.name());
            result.add(d);
        }

        /*返回数据*/
        return result;
    }

    /**
     * 获取 蓝鲸企业版全量 API功能类型列表
     * @return
     */
    public List<Map<String, String>> getBkApiTypeList() {
        /*待返回数据*/
        List<Map<String, String>> result = new ArrayList<>();

        for (BKCompanyApiType.BK_COMPANY_API_TYPE type:BKCompanyApiType.BK_COMPANY_API_TYPE.values()) {
            Map<String, String> d = new HashMap<>();
            d.put("value", type.name());
            d.put("lable", type.getName());
            d.put("apiType", type.name());
            result.add(d);
        }

        /*返回数据*/
        return result;
    }

    /**
     * 根据API账号 获取API功能类型列表
     * @return
     */
    public List<Map<String, String>> getApiTypeListByAccount(String apiAccountId) {
        /*待返回数据*/
        List<Map<String, String>> result = new ArrayList<>();

        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(apiAccountId);
        if (pciApiAccount != null) {
            // 过滤API_TYPE 中包含 systemType 的数据
            String systemType = pciApiAccount.getSystemType();
            if(StringUtils.isNotEmpty(pciApiAccount.getProviderFactoryId()) && "LANJINGCOMPANY".equals(pciApiAccount.getProviderFactoryId())){
                for (BKCompanyApiType.BK_COMPANY_API_TYPE type : BKCompanyApiType.BK_COMPANY_API_TYPE.values()) {
                    if (type.name().contains(systemType)) {
                        Map<String, String> d = new HashMap<>();
                        d.put("value", type.name());
                        d.put("lable", type.getName());
                        d.put("apiType", type.name());
                        result.add(d);
                    }
                }
            }else{
                for (ApiType.API_TYPE type : ApiType.API_TYPE.values()) {
                    if (type.name().contains(systemType)) {
                        Map<String, String> d = new HashMap<>();
                        d.put("value", type.name());
                        d.put("lable", type.getName());
                        d.put("apiType", type.name());
                        result.add(d);
                    }
                }
            }
        }
        /*返回数据*/
        return result;
    }

    /**
     * api接口列表
     * @param apiEndpointRequest
     * @return
     */
    public List<PciApiEndpointDTO> getApiEndpointList(PciApiEndpointRequest apiEndpointRequest) {
        List<PciApiEndpointDTO> apiEndpointList = extPciApiAccountMapper.getApiEndpointList(apiEndpointRequest);

        Optional.ofNullable(apiEndpointList).orElse(new ArrayList<>()).forEach(apiEndpoint -> {
            apiEndpoint.setSystemTypeName(ApiType.ACCOUNT_TYPE.getNameByType(apiEndpoint.getSystemType()));
            if(StringUtils.isNotEmpty(apiEndpoint.getProviderFactoryId())&& "LANJINGCOMPANY".equals(apiEndpoint.getProviderFactoryId())){
                apiEndpoint.setApiTypeName(BKCompanyApiType.BK_COMPANY_API_TYPE.getNameByType(apiEndpoint.getApiType()));
            }else{
                apiEndpoint.setApiTypeName(ApiType.API_TYPE.getNameByType(apiEndpoint.getApiType()));
            }
            apiEndpoint.setTime(DateUtil.getDate2String(apiEndpoint.getUpdateTime()));
        });

        for (PciApiEndpointDTO pciApiEndpointDTO: apiEndpointList){
            PciApiParameterMappingExample pciApiParameterMappingExample = new PciApiParameterMappingExample();
            pciApiParameterMappingExample.createCriteria().andApiIdEqualTo(pciApiEndpointDTO.getId());
            List<PciApiParameterMapping> pciApiParameterMappingList =  pciApiParameterMappingMapper.selectByExample(pciApiParameterMappingExample);
            if (pciApiParameterMappingList.size() > 0){
                pciApiEndpointDTO.setParameterMappingList(pciApiParameterMappingList);
            }
        }
        return apiEndpointList;
    }

    /**
     * 获取API地址全量数据
     * @return
     */
    public List<PciApiEndpointDTO> getApiEndpointAll() {
        PciApiEndpointRequest apiEndpointRequest = new PciApiEndpointRequest();
        List<PciApiEndpointDTO> apiEndpointList = extPciApiAccountMapper.getApiEndpointList(apiEndpointRequest);
        Optional.ofNullable(apiEndpointList).orElse(new ArrayList<>()).forEach(apiEndpoint -> {
            if(StringUtils.isNotEmpty(apiEndpoint.getProviderFactoryId())&& "LANJINGCOMPANY".equals(apiEndpoint.getProviderFactoryId())){
                apiEndpoint.setApiTypeName(BKCompanyApiType.BK_COMPANY_API_TYPE.getNameByType(apiEndpoint.getApiType()));
            }else{
                apiEndpoint.setApiTypeName(ApiType.API_TYPE.getNameByType(apiEndpoint.getApiType()));
            }
            apiEndpoint.setEndpoint(apiEndpoint.getEndpoint() + "(" + apiEndpoint.getApiTypeName() + ")");
        });
        return apiEndpointList;
    }

    /**
     * 新增API地址
     * @param request
     * @param applyUser
     */
    @Transactional
    public void addApiEndpoint(PciApiEndpointRequest request, String applyUser) {
        // 插入账号数据
        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(request.getApiAccount());

        // 校验同API账号下、同API功能的API地址是否已存在
        PciApiEndpointRequest apiEndpointRequest = new PciApiEndpointRequest();
        apiEndpointRequest.setApiAccount(request.getApiAccount());
        apiEndpointRequest.setApiType(request.getApiType());
        List<PciApiEndpointDTO> apiEndpointDTOS = getApiEndpointList(apiEndpointRequest);
        if (CollectionUtils.isNotEmpty(apiEndpointDTOS)) {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_already_exist"));
        }

        // 插入 api 数据
        if (pciApiAccount != null) {
            PciApiEndpoint pciApiEndpoint = new PciApiEndpoint();
            BeanUtils.copyBean(pciApiEndpoint, request);
            String apiEndpointId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_ENDPOINT_ID_PREFIX, SessionUtils.getUser().getWorkspaceId());
            pciApiEndpoint.setId(apiEndpointId);
            pciApiEndpoint.setUpdateTime(new Date());
            pciApiEndpointMapper.insert(pciApiEndpoint);
        } else {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
    }

    /**
     * 更新API地址
     * @param request
     * @param applyUser
     */
    @Transactional
    public void updateApiEndpoint(PciApiEndpointRequest request, String applyUser) {
        PciApiEndpoint pciApiEndpoint = new PciApiEndpoint();
        pciApiEndpoint.setId(request.getId());
        pciApiEndpoint.setEndpoint(request.getEndpoint());
        pciApiEndpoint.setMethod(request.getMethod());
        pciApiEndpoint.setRequestTemplate(request.getRequestTemplate());
        pciApiEndpoint.setCustomContent(request.getCustomContent());
        pciApiEndpoint.setUpdateTime(new Date());
        pciApiEndpointMapper.updateByPrimaryKeySelective(pciApiEndpoint);
    }

    /**
     * 删除API地址
     * @param id
     */
    @Transactional
    public void deleteEndpoint(String id) {
        // 删除api
        pciApiEndpointMapper.deleteByPrimaryKey(id);

        // 删除api数据下的 参数数据
        extPciApiAccountMapper.deleteApiParamaterMappingByApiId(id);
    }

    /**
     * 批量删除API地址
     * @param ids
     */
    @Transactional
    public void batchDeleteEndpoint(List<String> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            deleteEndpoint(id);
        });
    }

    /****************************** API 地址 end **********************************/

    /****************************** API 参数映射 start **********************************/

    /**
     * api接口参数映射列表
     * @param
     * @return
     */
    public List<PciApiParameterMappingDTO> getApiParameterMapppingList(PciApiParameterMappingRequest parameterMappingRequest) {
        List<PciApiParameterMappingDTO> parameterMappingList = extPciApiAccountMapper.getApiParameterMapppingList(parameterMappingRequest);
        Optional.ofNullable(parameterMappingList).orElse(new ArrayList<>()).forEach(mappingDTO -> {
            mappingDTO.setTime(DateUtil.getDate2String(mappingDTO.getUpdateTime()));
            mappingDTO.setOriginFieldName(mappingDTO.getOriginField());
            if (StringUtils.equals(mappingDTO.getOriginFieldSource(), "tag")) {
                TagDTO tagDTO = tagService.getTagDtoById(mappingDTO.getOriginField());
                if (tagDTO != null) {
                    mappingDTO.setOriginFieldName(tagDTO.getTagAlias());
                }
            }

            if (StringUtils.isNotEmpty(mappingDTO.getEndpoint())) {
                StringBuffer apiEndpoint = new StringBuffer();
                apiEndpoint.append(mappingDTO.getEndpoint());
                String apiTypeName = StringUtils.isNotEmpty(mappingDTO.getApiType()) ? "(" + ApiType.API_TYPE.getNameByType(mappingDTO.getApiType()) + ")" : "";
                if(StringUtils.isNotEmpty(mappingDTO.getProviderFactoryId())&& "LANJINGCOMPANY".equals(mappingDTO.getProviderFactoryId())){
                    apiTypeName = StringUtils.isNotEmpty(mappingDTO.getApiType()) ? "(" + BKCompanyApiType.BK_COMPANY_API_TYPE.getNameByType(mappingDTO.getApiType()) + ")" : "";
                }
                apiEndpoint.append(apiTypeName);
                mappingDTO.setApiEndpoint(apiEndpoint.toString());
            }
        });
        return parameterMappingList;
    }

    /**
     * 新增API参数映射
     * @param request
     * @param applyUser
     */
    @Transactional
    public void apiParameterMapping(PciApiParameterMappingRequest request, String applyUser) {
        // 插入账号数据
        PciApiEndpoint pciApiEndpoint = pciApiEndpointMapper.selectByPrimaryKey(request.getApiId());

        // 插入 api地址 参数数据
        if (pciApiEndpoint != null) {
            PciApiParameterMapping parameterMapping = new PciApiParameterMapping();
            BeanUtils.copyBean(parameterMapping, request);
            String parameterMappingId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_PARAMETER_MAPPING_ID_PREFIX, SessionUtils.getUser().getWorkspaceId());
            parameterMapping.setId(parameterMappingId);
            parameterMapping.setUpdateTime(new Date());
            pciApiParameterMappingMapper.insert(parameterMapping);
            insertMappingDictionaryList(request.getMappingDictionaryList(), parameterMappingId);
        } else {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_not_exist"));
        }
    }

    /**
     * 插入字段映射下的字典值映射
     * @param mappingDictionaryList
     * @param parameterMappingId
     */
    private void insertMappingDictionaryList(List<PciApiParameterMappingDictionary> mappingDictionaryList, String parameterMappingId) {
        Optional.ofNullable(mappingDictionaryList).orElse(new ArrayList<>()).forEach(mappingDictionary -> {
            mappingDictionary.setId(IDGenerator.newBusinessId(PciTableIdConstants.MAPPING_DICTIONARY_ID_PREFIX, SessionUtils.getUser().getWorkspaceId()));
            mappingDictionary.setParameterMappingId(parameterMappingId);
            pciApiParameterMappingDictionaryMapper.insert(mappingDictionary);
        });
    }

    /**
     * 更新API参数映射
     * @param request
     * @param applyUser
     */
    @Transactional
    public void updateParameterMapping(PciApiParameterMappingRequest request, String applyUser) {
        PciApiParameterMapping parameterMapping = new PciApiParameterMapping();
        BeanUtils.copyBean(parameterMapping, request);
        parameterMapping.setUpdateTime(new Date());
        pciApiParameterMappingMapper.updateByPrimaryKeySelective(parameterMapping);
        updateMappingDictionaryList(request.getMappingDictionaryList(), request.getId());
    }

    /**
     * 更新字段映射下的字典值映射
     * @param mappingDictionaryList
     * @param parameterMappingId
     */
    private void updateMappingDictionaryList(List<PciApiParameterMappingDictionary> mappingDictionaryList, String parameterMappingId) {
        // 删除旧的字典映射
        deleteParameterMappingDictionary(parameterMappingId);
        // 插入新的字典映射
        insertMappingDictionaryList(mappingDictionaryList, parameterMappingId);
    }

    /**
     * 删除字段映射下的字典映射
     * @param parameterMappingId
     */
    public void deleteParameterMappingDictionary(String parameterMappingId) {
        PciApiParameterMappingDictionaryExample example = new PciApiParameterMappingDictionaryExample();
        example.createCriteria().andParameterMappingIdEqualTo(parameterMappingId);
        pciApiParameterMappingDictionaryMapper.deleteByExample(example);
    }

    /**
     * 删除API参数映射
     * @param id
     */
    public void deleteParameterMapping(String id) {
        // 删除旧的字典映射
        deleteParameterMappingDictionary(id);
        // 删除api
        pciApiParameterMappingMapper.deleteByPrimaryKey(id);
    }

    /**
     * 批量删除API参数映射
     * @param ids
     */
    @Transactional
    public void batchDeleteParameterMapping(List<String> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            deleteParameterMapping(id);
        });
    }

    /**
     * 获取API参数映射源字段类型列表
     * @return
     */
    public List<Map<String, String>> getApiOriginFieldTypeList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (ParameterType.ORIGIN_FIELD_TYPE type: ParameterType.ORIGIN_FIELD_TYPE.values()) {
            Map<String, String> d = new HashMap<>();
            d.put("value", type.name());
            d.put("lable", type.getDesc());
            result.add(d);
        }
        return result;
    }

    /**
     * 获取API参数映射映射字段类型列表
     * @return
     */
    public List<Map<String, String>> getApiTargetFieldTypeList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (ParameterType.TARGET_FIELD_TYPE type: ParameterType.TARGET_FIELD_TYPE.values()) {
            Map<String, String> d = new HashMap<>();
            d.put("value", type.name());
            d.put("lable", type.getDesc());
            result.add(d);
        }
        return result;
    }

    /**
     * 根据所选接口 获取可选源表
     * @param apiId
     * @return
     */
    public List<Map<String, String>> getApiOriginTableList(String apiId) {
        List<Map<String, String>> result = new ArrayList<>();

        PciApiEndpoint pciApiEndpoint = pciApiEndpointMapper.selectByPrimaryKey(apiId);
        if (pciApiEndpoint != null) {
            PciApiAccount apiAccount = pciApiAccountMapper.selectByPrimaryKey(pciApiEndpoint.getApiAccount());
            List<String> tableList = ApiOriginSourceTable.API_ORIGIN_TABLE.getTableList(apiAccount.getSystemType());
            for (String table : tableList) {
                Map<String, String> d = new HashMap<>();
                d.put("value", table);
                d.put("lable", table);
                result.add(d);
            }
        }

        return result;
    }

    /**
     * 根据所选接口 获取可选订单属性
     * @param apiId
     * @return
     */
    public List<JSONObject> getApiOrderFieldList(String apiId) {
        List<JSONObject> result = new ArrayList<>();
        PciApiEndpoint pciApiEndpoint = pciApiEndpointMapper.selectByPrimaryKey(apiId);
        String apiType = pciApiEndpoint.getApiType();
        result = ApiOriginOrderField.ORIGIN_FIELD_TYPE.getOrderFieldByApiType(apiType);
        return result;
    }

    /**
     * 获取可选标签列表
     * @return
     */
    public List<JSONObject> getOriginFieldTagOptions() {
        List<JSONObject> tagList = new ArrayList<>();

        try {
            // 查询所有可选单级标签
            TagExample tagExample = new TagExample();
            tagExample.createCriteria().andEnableEqualTo(Boolean.TRUE).andTagTypeNotEqualTo("CUSTOM_SYSTEM_TYPE");
            // 启用的、序号大的靠前
            tagExample.setOrderByClause("enable desc,_index desc,tag_type asc");
            List<TagDTO> tagDTOList = tagService.selectAllTags(tagExample);
            Optional.ofNullable(tagDTOList).orElse(new ArrayList<>()).forEach(tag->{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", tag.getTagAlias());
                jsonObject.put("lable", tag.getTagKey());
                jsonObject.put("value", tag.getTagId());
                tagList.add(jsonObject);
            });
        } catch (Exception e) {
            F2CException.throwException(e.getMessage());
        }

        return tagList;
    }

    /**
     * 获取可选资源池标签列表
     * @return
     */
    public List<JSONObject> getOriginFieldResourcePoolTagOptions() {
        List<JSONObject> tagList = new ArrayList<>();
        try {
            // 查询所有可选单级标签
            CommonResourcePoolTagExample commonResourcePoolTagExample = new CommonResourcePoolTagExample();
            commonResourcePoolTagExample.createCriteria().andParentIsNull();
            // 启用的、序号大的靠前
            commonResourcePoolTagExample.setOrderByClause("_index desc");
            List<CommonResourcePoolTag> resourcePoolTagList =  commonResourcePoolTagMapper.selectByExample(commonResourcePoolTagExample);
            Optional.ofNullable(resourcePoolTagList).orElse(new ArrayList<>()).forEach(tag->{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", tag.getTagValue());
                jsonObject.put("lable", tag.getTagValue());
                jsonObject.put("value", "resource_pool_tag_"+tag.getId());
                tagList.add(jsonObject);
            });
        } catch (Exception e) {
            F2CException.throwException(e.getMessage());
        }

        return tagList;
    }



    /**
     * 获取源表属性列表
     *
     * @return
     */
    public List<JSONObject> getApiOriginTableAttr(String originTable) {
        List<JSONObject> attrList = new ArrayList<>();

        try {
            attrList = getClassAttr(originTable);
        } catch (Exception e) {
            F2CException.throwException(e.getMessage());
        }
        return attrList;
    }

    private List<JSONObject> getClassAttr(String originTable) {
        List<JSONObject> attrList = new ArrayList<>();

        Class<?> tableClass = ApiOriginSourceTable.API_ORIGIN_TABLE.getTableClass(originTable);
        Field[] allFields = FieldUtils.getAllFields(tableClass);
        for (Field field : allFields) {
            boolean hasAnnotation = field.isAnnotationPresent(ApiModelProperty.class);
            JSONObject jsonObject = new JSONObject();
            if (hasAnnotation) {
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                String value = annotation.value();
                jsonObject.put("name", StringUtils.isEmpty(value) ? field.getName() : value);
                jsonObject.put("value", field.getName());
                attrList.add(jsonObject);
            }
        }
        return attrList;
    }

    /**
     * 获取字段映射的字典映射
     * @param parameterMappingId
     * @return
     */
    public List<PciApiParameterMappingDictionary> getDictionaryMappingList(String parameterMappingId) {
        PciApiParameterMappingDictionaryExample example = new PciApiParameterMappingDictionaryExample();
        example.createCriteria().andParameterMappingIdEqualTo(parameterMappingId);
        return pciApiParameterMappingDictionaryMapper.selectByExample(example);
    }

    public String getCustomContent(String accountId) {
        PciApiAccount pciApiAccount = pciApiAccountMapper.selectByPrimaryKey(accountId);
        return Optional.ofNullable(pciApiAccount).map(PciApiAccount::getCustomContent).orElse(null);
    }

    /****************************** API 参数映射 end **********************************/
}
