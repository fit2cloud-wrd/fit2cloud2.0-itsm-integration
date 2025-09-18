package com.fit2cloud.itsm.integration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.process.ProcessEventContext;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.common.utils.ReadConfigFileUtil;
import com.fit2cloud.itsm.common.utils.SpringContextUtils;
import com.fit2cloud.itsm.common.utils.HttpClientUtil;
import com.fit2cloud.itsm.common.constants.ApiSupportVersion;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.model.dto.APICredentialItem;
import com.fit2cloud.itsm.model.dto.ITSMOrder;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import com.fit2cloud.itsm.service.impl.ApiLogService;
import com.fit2cloud.itsm.model.response.ResponseResult;
import com.fit2cloud.itsm.model.response.WeaverResponse;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class WeaverIntegration extends BaseITSMIntegration implements ITSMIntegration {

    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    public void setApiLogService(ApiLogService apiLogService){
        super.apiLogService=apiLogService;
    }



    @Override
    public String getId() {
        return "WEAVER";
    }

    @Override
    public String getName() {
        return "泛微OA";
    }

    @Override
    public String getVersion() {
        return ApiSupportVersion.WEAVER.V3_1_0.getVersion();
    }

    @Override
    public List<String> getSupportVersion() {
        List<String> versionList = new ArrayList<>();
        for (ApiSupportVersion.WEAVER value : ApiSupportVersion.WEAVER.values()) {
            versionList.add(value.getVersion());
        }
        return versionList;
    }

    @Override
    public String getDefaultAPIEndpoint() {
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());
        if (pciApiAccountDTO != null) {
            return pciApiAccountDTO.getApiEndpoint();
        }
        return null;
    }

    @Override
    public String testDefaultAPIEndpoint() {
        return getDefaultAPIEndpoint();
    }

    @Override
    public List<PciApiEndpointDTO> getDefaultApiList() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return apiAccountService.getApiListByAccountId(pciApiAccountDTO.getId());
        }

        return null;
    }

    @Override
    public String testDefaultApiUrl() {
        return "/restcloud/rest/core/auth/login?userName=%s&password=%s";
    }

    @Override
    public String getDefaultApiListJson() {
        return ReadConfigFileUtil.readConfigFile("WeaverDefaultApiList.json");
    }

    @Override
    public List<APICredentialItem> getCredentialTemplate() {
        List<APICredentialItem> credentialItems = new ArrayList<>();

        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.CMDB.name());

        if (pciApiAccountDTO != null) {
            String credential = pciApiAccountDTO.getCredential();
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

    @Override
    public String getCreditialJson() {
        return ReadConfigFileUtil.readConfigFile("WeaverCredential.json");
    }

    @Override
    public <T extends ITSMIntegration> T getProvider(Class<T> clazz, String version) {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(ITSMIntegration.class.getPackage().getName())
                    .addScanners(new SubTypesScanner()));

            Set<Class<? extends ITSMIntegration>> implClass = reflections.getSubTypesOf(ITSMIntegration.class);
            for (Class<? extends ITSMIntegration> subClass : implClass) {
                Class<?> cls = Class.forName(subClass.getName());

                Method getVersionMethod = subClass.getDeclaredMethod("getVersion");
                String clsVersion = (String) getVersionMethod.invoke(cls.newInstance());
                if (StringUtils.equals(clsVersion, version)) {
                    return (T) SpringContextUtils.getBean(subClass);
                }
            }
        } catch (Exception exception) {
        }

        return null;
    }

    @Override
    public String takeOneItsm(BusinessEventContextDTO businessEventContextDTO) {
        ResponseResult<WeaverResponse> result = sendRequestToITSM("createProcess", this.getDefaultApiList(), businessEventContextDTO);
        return result.getData().getCid();
    }

    @Override
    public void putOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        sendRequestToITSM("submitProcess", this.getDefaultApiList(), businessEventContextDTO);
    }

    @Override
    public void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        sendRequestToITSM("completeProcess", this.getDefaultApiList(), businessEventContextDTO);
    }

    @Override
    public ResponseResult<WeaverResponse> callITSMCreateProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("isDone", "0");
        appendCredentialParam(businessEventContextDTO, map);
        return doCallITSM(apiEndpointDTO, itsmOrder, businessEventContextDTO, map);
    }

    @Override
    public ResponseResult<WeaverResponse> callITSMSubmitProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("isDone", "2");
        appendCredentialParam(businessEventContextDTO, map);
        return doCallITSM(apiEndpointDTO, itsmOrder, businessEventContextDTO, map);
    }

    @Override
    public ResponseResult<WeaverResponse> callITSMCompleteProcess(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO) {
        Map<String, Object> map = new HashMap<>();
        FlowProcess process = businessEventContextDTO.getProcessEventContext().getProcess();
        String processStatus = process.getProcessStatus();
        if ("COMPLETED".equals(processStatus)) {
            map.put("isDone", "4");
        } else {
            map.put("isDone", "-2");
        }
        appendCredentialParam(businessEventContextDTO, map);
        return doCallITSM(apiEndpointDTO, itsmOrder, businessEventContextDTO, map);
    }

    private void appendCredentialParam(BusinessEventContextDTO businessEventContextDTO, Map<String, Object> map) {
        List<APICredentialItem> credentialTemplate = this.getCredentialTemplate();
        credentialTemplate.stream().forEach(item -> {
            if ("approveUrl".equals(item.getName())) {
                map.put(item.getName(), item.getDefaultValue() + "=" + businessEventContextDTO.getBusinessKey());
            } else {
                map.put(item.getName(), item.getDefaultValue());
            }
        });
    }

    private ResponseResult<WeaverResponse> doCallITSM(PciApiEndpointDTO apiEndpointDTO, ITSMOrder itsmOrder, BusinessEventContextDTO businessEventContextDTO, Map<String, Object> param) {
        String url = apiEndpointDTO.getApiEndpoint() + "/" + apiEndpointDTO.getEndpoint();
        ProcessEventContext processEventContext = businessEventContextDTO.getProcessEventContext();
        FlowTask task = processEventContext.getTask();
        FlowProcess process = processEventContext.getProcess();
        Map map = new HashMap<>();
        String businessKey = itsmOrder.getOrderId();
        // 流程类型唯一标识
        map.put("workflowid", process.getProcessId());
        // 流程类型名称
        map.put("workflowname", process.getProcessName());
        // 流程实例唯一标识
        map.put("flowid", process.getProcessId());
        // 流程节点唯一标识
        map.put("nodeid", task.getTaskId());
        // 流程节点名称
        map.put("nodename", task.getTaskName());
        // isremark 0待办 2已办 4完成 -2终止
        // isviewtype 0 办件 1 阅件
        // viewtype 0 未读 1 已读
        String isremark = (String) param.get("isremark");
        map.put("isremark", isremark);
        switch (isremark) {
            case "0":
                map.put("isviewtype", 0);
                map.put("viewtype", 0);
                break;
            case "2":
            case "4":
            case "-2":
                map.put("isviewtype", 0);
                map.put("viewtype", 1);
                break;
        }
        // 流程创建人唯一标识
        map.put("creatorloginid", process.getProcessCreator());
        // 流程接收人唯一标识
        map.put("receiverloginid", task.getTaskAssignee());
        Date date = new Date(process.getProcessStartTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("createdatetime", simpleDateFormat.format(date));
        map.put("receivedatetime", simpleDateFormat.format(date));
        map.put("lastupdatetime", simpleDateFormat.format(date));
        map.put("receivets", String.valueOf(System.currentTimeMillis()));
        // 流程在PC端的浏览地址
        map.put("pcurl", param.get("approveUrl"));
        // 第三方对接系统编号-云管在oa系统中的编号
        map.put("syscode", param.get("syscode"));
        // 流程标题
        map.put("requestname", "【云管平台】" + itsmOrder.getOperType() + " " + businessKey);
        String resultStr = HttpClientUtil.post(url, JSONObject.toJSONString(map));
        WeaverResponse weaverResponse = JSONObject.parseObject(resultStr, WeaverResponse.class);
        ResponseResult<WeaverResponse> result = new ResponseResult<>();
        result.setSuccess(StringUtils.isNotEmpty(weaverResponse.getErrorCode()));
        result.setMsg(weaverResponse.getMessage());
        result.setData(weaverResponse);
        return result;
    }

}
