package com.fit2cloud.itsm.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.CommonOrganizationDTO;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.model.request.RoleInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class SyncExternalOrgAndUserUtils {
    private static final String ADMIN_USER_ID = "admin";

    private static MicroService microService;
    private static UserCommonService userCommonService;
    private static DiscoveryClient discoveryClient;


    private static final String SERVICE_ID = "management-center";
    private static final String ADD_ORG_API_URL = "/organization/add";
    private static final String UPDATE_ORG_API_URL = "/organization/update";
    private static final String ADD_USER_API_URL = "/user/add";
    private static final String UPDATE_USER_API_URL = "/user/update";
    private static final String GET_USER_INFO_API_URL = "/user/role/info/{userId}";
    private static final String UPDATE_USER_ROLE_API_URL = "/workspace/updateUserRoleByUser/{workspaceId}";
    private static final String GET_ORG_INFO_API_URL = "/organization/{id}";
    private static final String GET_USER_LIST_API_URL = "/user/{page}/{rows}";


    @Resource
    public void setMicroService(MicroService microService){
        SyncExternalOrgAndUserUtils.microService = microService;
    }

    @Resource
    public void setUserCommonService(UserCommonService userCommonService) {
        SyncExternalOrgAndUserUtils.userCommonService = userCommonService;
    }

    @Resource
    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        SyncExternalOrgAndUserUtils.discoveryClient = discoveryClient;
    }

    public static Organization triggerCreateOrg(String requestJson) {
        ResultHolder resultHolder = microService.postForResultHolder(SERVICE_ID, ADD_ORG_API_URL, requestJson);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }

        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), Organization.class);
        }
        return null;
    }

    public static Organization triggerUpdateOrg(String requestJson) {
        ResultHolder resultHolder = microService.postForResultHolder(SERVICE_ID, UPDATE_ORG_API_URL, requestJson);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), Organization.class);
        }
        return null;
    }

    public static void triggerCreateOrgByAdmin(String requestJson){
        triggerCreateOrgByUser(requestJson, ADMIN_USER_ID);
    }

    public static void triggerCreateOrgByUser(String requestJson, String userId){
        User user = userCommonService.getUserById(userId);
        if (Objects.isNull(user)) {
            F2CException.throwException(String.format("Cannot find user with id [%s]", userId));
        }
        microService.runAsUser(user);
        triggerCreateOrg(requestJson);
    }

    public static UserDTO triggerCreateUser(String requestJson) {
        ResultHolder resultHolder = microService.postForResultHolder(SERVICE_ID, ADD_USER_API_URL, requestJson);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), UserDTO.class);
        }
        return null;
    }

    public static UserDTO triggerUpdateUser(String requestJson) {
        ResultHolder resultHolder = microService.postForResultHolder(SERVICE_ID, UPDATE_USER_API_URL, requestJson);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), UserDTO.class);
        }
        return null;
    }

    public static List<RoleInfo> getRoleInfo(String userId) {
        String url = StringUtils.replace(GET_USER_INFO_API_URL, "{userId}", userId);
        ResultHolder resultHolder = microService.getForResultHolder(SERVICE_ID, url);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONArray.parseArray(JSONArray.toJSONString(resultHolder.getData()), RoleInfo.class);
        }
        return null;
    }

    public static CommonOrganizationDTO getOrgInfo(String orgId) {
        LogUtil.info("getOrgInfo："+JSONObject.toJSONString(orgId));
        String url = StringUtils.replace(GET_ORG_INFO_API_URL, "{id}", orgId);
        User user = userCommonService.getUserById(ADMIN_USER_ID);
        ResultHolder resultHolder = microService.runAsUser(user).getForResultHolder(SERVICE_ID, url);
        LogUtil.info("getOrgInfo 结果："+JSONObject.toJSONString(resultHolder));
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), CommonOrganizationDTO.class);
        }
        return null;
    }

    public static Pager<List<UserDTO>> getUserList(String requestJson,String page,String rows) {
        LogUtil.info("getUserList："+JSONObject.toJSONString(requestJson));
        String url = StringUtils.replace(GET_USER_LIST_API_URL, "{page}", page);
        url = StringUtils.replace(url, "{rows}", rows);
        User user = userCommonService.getUserById(ADMIN_USER_ID);
        ResultHolder resultHolder = microService.runAsUser(user).postForResultHolder(SERVICE_ID, url,requestJson);
        LogUtil.info("getUserList 结果："+JSONObject.toJSONString(resultHolder));
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), new TypeReference<Pager<List<UserDTO>>>(){});
        }
        return null;
    }

    public static UserDTO triggerUpdateUserRoleByUser(String workspaceId, Map<String, Object> map) {
        String url = StringUtils.replace(UPDATE_USER_ROLE_API_URL, "{workspaceId}", workspaceId);
        ResultHolder resultHolder = microService.postForResultHolder(SERVICE_ID, url, map);
        if (!resultHolder.isSuccess()) {
            F2CException.throwException(StringUtils.isBlank(resultHolder.getMessage())
                    ? Translator.get("i18n_process_integration_exception")
                    : resultHolder.getMessage());
        }
        if (resultHolder.getData() != null) {
            return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), UserDTO.class);
        }
        return null;
    }

    /**
     * 判断流程 CMDB 模块是否运行
     * @return 流程 CMDB 模块是否运行
     */
    public static boolean isCreateOrgServiceReady() {
        return CollectionUtils.isNotEmpty(discoveryClient.getInstances(SERVICE_ID));
    }
}
