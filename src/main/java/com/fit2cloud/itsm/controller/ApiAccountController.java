package com.fit2cloud.itsm.controller;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.model.PciApiAccount;
import com.fit2cloud.itsm.model.PciApiParameterMappingDictionary;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;
import com.fit2cloud.itsm.model.dto.PciApiParameterMappingDTO;
import com.fit2cloud.itsm.model.request.PciApiAccountAddRequest;
import com.fit2cloud.itsm.model.request.PciApiAccountRequest;
import com.fit2cloud.itsm.model.request.PciApiEndpointRequest;
import com.fit2cloud.itsm.model.request.PciApiParameterMappingRequest;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequestMapping("api/account")
@RestController
public class ApiAccountController {

    @Resource
    private ApiAccountService apiAccountService;

    /****************************** API 账号 start **********************************/

    /**
     * 获取厂商列表
     * @return
     */
    @GetMapping("/getVendorList")
    public List<Map<String, String>> getVendorList(){
        return apiAccountService.getVendorList();
    }

    /**
     * 获取账号类型列表
     * @return
     */
    @GetMapping("/type/list")
    public List<Map<String, String>> getAccountTypeList(){
        return apiAccountService.getAccountTypeList();
    }

    /**
     * api账号列表
     *
     * @param goPage
     * @param pageSize
     * @param apiAccountRequest
     * @return
     */
    @I18n
    @RequestMapping(value = "/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public Pager<List<PciApiAccountDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciApiAccountRequest apiAccountRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiAccountService.getApiAccountList(apiAccountRequest));
    }

    /**
     * 检查账号名称是否重复
     * @param name
     * @return
     */
    @GetMapping("/check/accountName/{name}")
    @I18n
    public Boolean checkoutAccountName(@PathVariable String name) {
        return apiAccountService.checkoutAccountName(name);
    }

    /**
     * 获取厂商的默认API账号
     * @param providerId
     * @return
     */
    @RequestMapping(value = "/default/apiList/{providerId}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    @I18n
    public String getDefaultApiList(@PathVariable String providerId) {
        return apiAccountService.getDefaultApiList(providerId);
    }

    /**
     * 获取厂商的API账号认证信息模板
     * @param providerId
     * @return
     */
    @RequestMapping(value = "/creditial/{providerId}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    @I18n
    public String getCreditial(@PathVariable String providerId) {
        return apiAccountService.getCreditial(providerId);
    }


    @RequestMapping(value = "/customContent/{accountId}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    @I18n
    public String getCustomContent(@PathVariable String accountId) {
        return apiAccountService.getCustomContent(accountId);
    }

    /**
     * 新增API账号
     * @param request
     */
    @PostMapping(value = "/add")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_EDIT)
    public void apiAccountAdd(@RequestBody PciApiAccountAddRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.addApiAccount(request, applyUser);
    }

    /**
     * 删除API账号
     * @param id
     */
    @RequestMapping("delete/{id}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_EDIT)
    public void delete(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        apiAccountService.delete(id);
    }

    /**
     * 批量删除API账号
     * @param ids
     */
    @PostMapping(value = "/batchDelete")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_DELETE)
    public void batchDelete(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        apiAccountService.batchDelete(ids);
    }

    /**
     * 更新API账号
     * @param request
     */
    @PostMapping(value = "/update")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_EDIT)
    public void apiAccountUpdate(@RequestBody PciApiAccountAddRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.updateApiAccount(request, applyUser);
    }

    /**
     * API账号启用/禁用
     * @param id
     */
    @RequestMapping("enable/{id}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_DELETE)
    public void enable(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_account_not_exist"));
        }
        apiAccountService.enable(id);
    }

    /**
     * API 账号校验
     * @param id
     * @return
     */
    @PostMapping("validate/{id}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public Boolean validate(@PathVariable String id) {
        return apiAccountService.validate(id);
    }

    /**
     * 获取当前厂商支持的版本
     * @param providerId
     * @return
     */
    @GetMapping("/version/{providerId}")
    public List<Map<String, String>> getVersionList(@PathVariable String providerId) {
        return apiAccountService.getVersionList(providerId);
    }

    /**
     * 获取API账号全量数据
     * @return
     */
    @I18n
    @RequestMapping(value = "/getApiAccountAll")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public List<PciApiAccount> getApiAccountAll() {
        return apiAccountService.getApiAccountAll();
    }

    @ApiOperation("是否开启自动同步")
    @PostMapping("autoSync/{id}")
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    public void updateAutoSync(@PathVariable String id, @RequestBody Boolean autoSync) {
        if (Objects.isNull(autoSync)) {
            F2CException.throwException(Translator.get("i18n_param_no_empty"));
        }
        apiAccountService.autoSync(id, autoSync);
    }


    /****************************** API 账号 end **********************************/

    /****************************** API 地址 start **********************************/

    /**
     * api接口列表
     * @param goPage
     * @param pageSize
     * @param apiEndpointRequest
     * @return
     */
    @I18n
    @RequestMapping(value = "/endpoint/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_READ)
    public Pager<List<PciApiEndpointDTO>> listEndpoint(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciApiEndpointRequest apiEndpointRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiAccountService.getApiEndpointList(apiEndpointRequest));
    }

    /**
     * 获取API账号下的API地址列表
     * @param accountId
     * @return
     */
    @RequestMapping(value = "/apiList/{accountId}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.API_ACCOUNT_READ)
    @I18n
    public List<PciApiEndpointDTO> getApiListByAccountId(@PathVariable String accountId) {
        return apiAccountService.getApiListByAccountId(accountId);
    }

    /**
     * 新增API地址
     * @param request
     */
    @PostMapping(value = "/endpoint/add")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_EDIT)
    public void apiEndpointAdd(@RequestBody PciApiEndpointRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.addApiEndpoint(request, applyUser);
    }

    /**
     * 更新API地址
     * @param request
     */
    @PostMapping(value = "/endpoint/update")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_EDIT)
    public void apiEndpointUpdate(@RequestBody PciApiEndpointRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.updateApiEndpoint(request, applyUser);
    }

    /**
     * 删除API地址
     * @param id
     */
    @RequestMapping("/endpoint/delete/{id}")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_DELETE)
    public void deleteEndpoint(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_not_exist"));
        }
        apiAccountService.deleteEndpoint(id);
    }

    /**
     * 批量删除API地址
     * @param ids
     */
    @PostMapping(value = "/endpoint/batchDelete")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_DELETE)
    public void batchDeleteEndpoint(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_not_exist"));
        }
        apiAccountService.batchDeleteEndpoint(ids);
    }

    /**
     * 获取 全量 API功能类型列表
     * @return
     */
    @GetMapping("/apiType/list")
    public List<Map<String, String>> getApiTypeList(){
        return apiAccountService.getApiTypeList();
    }

    /**
     * 获取 全量 API功能类型列表
     * @return
     */
    @GetMapping("/bkCompanyApiType/list")
    public List<Map<String, String>> getBkApiTypeList(){
        return apiAccountService.getBkApiTypeList();
    }

    /**
     * 根据API账号 获取API功能类型列表
     * @return
     */
    @GetMapping("/apiType/list/{apiAccountId}")
    public List<Map<String, String>> getApiTypeListByAccount(@PathVariable String apiAccountId){
        return apiAccountService.getApiTypeListByAccount(apiAccountId);
    }

    /**
     * 获取API地址全量数据
     * @param apiAccountRequest
     * @return
     */
    @I18n
    @RequestMapping(value = "/getApiEndpointAll")
    @RequiresPermissions(PermissionConstants.API_ADDRESS_READ)
    public List<PciApiEndpointDTO> getApiEndpointAll() {
        return apiAccountService.getApiEndpointAll();
    }

    /****************************** API 地址 end **********************************/

    /****************************** API 参数映射 start **********************************/

    /**
     * api接口参数映射列表
     * @param goPage
     * @param pageSize
     * @return
     */
    @I18n
    @RequestMapping(value = "/parameterMapping/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_READ)
    public Pager<List<PciApiParameterMappingDTO>> listParameterMappping(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciApiParameterMappingRequest parameterMappingRequest) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiAccountService.getApiParameterMapppingList(parameterMappingRequest));
    }

    /**
     * 新增API参数映射
     * @param request
     */
    @PostMapping(value = "/parameterMapping/add")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    public void apiParameterMappingAdd(@RequestBody PciApiParameterMappingRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.apiParameterMapping(request, applyUser);
    }

    /**
     * 更新API参数映射
     * @param request
     */
    @PostMapping(value = "/parameterMapping/update")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    public void apiParameterMappingUpdate(@RequestBody PciApiParameterMappingRequest request) {
        String applyUser = SessionUtils.getUser().getId();
        apiAccountService.updateParameterMapping(request, applyUser);
    }

    /**
     * 删除API参数映射
     * @param id
     */
    @RequestMapping("/parameterMapping/delete/{id}")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_DELETE)
    public void deleteParameterMapping(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_not_exist"));
        }
        apiAccountService.deleteParameterMapping(id);
    }

    /**
     * 批量删除API参数映射
     * @param ids
     */
    @PostMapping(value = "/parameterMapping/batchDelete")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_DELETE)
    public void batchDeleteParameterMapping(@RequestBody List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            F2CException.throwException(Translator.get("i18n_ex_api_endpoint_not_exist"));
        }
        apiAccountService.batchDeleteParameterMapping(ids);
    }

    /**
     * 获取API参数映射源字段类型列表
     * @return
     */
    @GetMapping("/originFieldType/list")
    public List<Map<String, String>> getApiOriginFieldTypeList(){
        return apiAccountService.getApiOriginFieldTypeList();
    }

    /**
     * 获取API参数映射映射字段类型列表
     * @return
     */
    @GetMapping("/targetFieldType/list")
    public List<Map<String, String>> getApiTargetFieldTypeList(){
        return apiAccountService.getApiTargetFieldTypeList();
    }

    /**
     * 根据所选接口 获取可选源表
     * @param apiId
     * @param apiId
     * @return
     */
    @GetMapping("/originTable/list/{apiId}")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n(I18nConstants.CLUSTER)
    public List<Map<String, String>> getApiOriginTableList(@PathVariable String apiId) {
        return apiAccountService.getApiOriginTableList(apiId);
    }

    /**
     * 根据所选接口 获取可选订单属性
     * @param apiId
     * @param apiId
     * @return
     */
    @GetMapping("/orderField/list/{apiId}")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n(I18nConstants.CLUSTER)
    public List<JSONObject> getApiOrderFieldList(@PathVariable String apiId) {
        return apiAccountService.getApiOrderFieldList(apiId);
    }

    /**
     * 获取可选标签列表
     *
     * @return
     */
    @RequestMapping("/getOriginFieldTagOptions")
    @ApiOperation("获取可选标签列表")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n(I18nConstants.CLUSTER)
    public List<JSONObject> getOriginFieldTagOptions() {
        return apiAccountService.getOriginFieldTagOptions();
    }

    /**
     * 获取可选资源池标签列表
     *
     * @return
     */
    @RequestMapping("/getOriginFieldResourcePoolTagOptions")
    @ApiOperation("获取可选资源池标签列表")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n(I18nConstants.CLUSTER)
    public List<JSONObject> getOriginFieldResourcePoolTagOptions() {
        return apiAccountService.getOriginFieldResourcePoolTagOptions();
    }

    /**
     * 获取源表属性列表
     *
     * @return
     */
    @RequestMapping("/getApiOriginTableAttr/{originTable}")
    @ApiOperation("获取源表属性列表")
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n(I18nConstants.CLUSTER)
    public List<JSONObject> getApiOriginTableAttr(@PathVariable String originTable) {
        return apiAccountService.getApiOriginTableAttr(originTable);
    }

    /**
     * 获取字段映射的字典映射
     * @param mappingId
     * @return
     */
    @RequestMapping(value = "/dictionaryMappingList/{mappingId}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.API_PARAMETER_MAPPING_EDIT)
    @I18n
    public List<PciApiParameterMappingDictionary> getDictionaryMappingList(@PathVariable String mappingId) {
        return apiAccountService.getDictionaryMappingList(mappingId);
    }

    /****************************** API 参数映射 end **********************************/
}

