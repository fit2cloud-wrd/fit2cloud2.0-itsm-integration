package com.fit2cloud.itsm.service.impl;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.utils.IDGenerator;
import com.fit2cloud.commons.utils.DateUtil;
import com.fit2cloud.itsm.common.constants.PciTableIdConstants;
import com.fit2cloud.itsm.dao.*;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.common.constants.BKCompanyApiType;
import com.fit2cloud.itsm.model.*;
import com.fit2cloud.itsm.model.request.ExternalSystemSyncDetailRequest;
import com.fit2cloud.itsm.model.request.PciApiLogRequest;
import com.fit2cloud.itsm.model.response.ExternalSystemSyncDetailLogRep;
import com.fit2cloud.itsm.model.response.PciApiLogRep;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ApiLogService {

    @Resource
    private PciApiLogMapper pciApiLogMapper;

    @Resource
    private ExtPciApiLogMapper extPciApiLogMapper;

    @Resource
    private WorkspaceMapper workspaceMapper;

    @Resource
    private PciApiLogContentMapper pciApiLogContentMapper;

    @Resource
    private SystemParameterMapper parameterMapper;

    @Resource
    private PciApiRequestLogMapper apiRequestLogMapper;

    @Resource
    private PciExternalSystemSyncDetailLogMapper pciExternalSystemSyncDetailLogMapper;

    public List<PciApiLogRep> getApiLogList(PciApiLogRequest apiLogRequest) {
        List<PciApiLogRep> pciApiLogList = extPciApiLogMapper.getApiLogList(apiLogRequest);

        Optional.ofNullable(pciApiLogList).orElse(new ArrayList<>()).forEach(apiLog -> {
            String apiTypeName = StringUtils.isNotEmpty(apiLog.getApiType())? ApiType.API_TYPE.getNameByType(apiLog.getApiType()):"";
            if(StringUtils.isNotEmpty(apiLog.getApiType()) && StringUtils.isNotEmpty(apiLog.getProviderFactoryId()) && "LANJINGCOMPANY".equals(apiLog.getProviderFactoryId())){
                apiTypeName = BKCompanyApiType.BK_COMPANY_API_TYPE.getNameByType(apiLog.getApiType());
            }
            apiLog.setApiTypeName(apiTypeName);
            apiLog.setStartExecuteTime(DateUtil.getLong2ShortString(apiLog.getExecuteTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
            if (StringUtils.isNotEmpty(apiLog.getWorkspace())) {
                Workspace workspace = workspaceMapper.selectByPrimaryKey(apiLog.getWorkspace());
                if (workspace != null)
                    apiLog.setWorkspaceName(workspace.getName());
            }
        });

        return pciApiLogList;
    }

    public int countInPushLogs(String systemType) {
        Map<String, String> params = new HashMap<>();
        if (StringUtils.isNotEmpty(systemType)) {
            params.put("systemType", systemType);
        }
        return extPciApiLogMapper.countInPushLogs(params);
    }

    public String getLogMessage(String logId) {
        PciApiLogContent pciApiLogContent = pciApiLogContentMapper.selectByPrimaryKey(logId);
        if (pciApiLogContent != null) {
            return pciApiLogContent.getMessage();
        }
        return Translator.get("i18n_ex_api_log_message_not_exist");
    }
    
    /**
     * 插入日志
     * @param apiLog
     */
    public String insertApiLog(PciApiLog apiLog) {
        String apiLogId = IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_LOG_ID_PREFIX, apiLog.getWorkspace());;
        apiLog.setId(apiLogId);
        apiLog.setExecuteTime(System.currentTimeMillis());
        pciApiLogMapper.insert(apiLog);

        return apiLogId;
    }

    public String packageAndInsertApiLog(String apiId, String method, String module, String resourceId,
                                         String resourceName, String resourceType, String workspace) {
        PciApiLog apiLog = new PciApiLog();

        apiLog.setApiId(apiId);
        apiLog.setMethod(method);
        apiLog.setModule(module);
        apiLog.setResourceId(resourceId);
        apiLog.setResourceName(resourceName);
        apiLog.setResourceType(resourceType);
        apiLog.setWorkspace(workspace);
        // 新建的日志默认为进行中，执行时间为0
        apiLog.setCode(ApiType.API_LOG_CODE.EXCUTION.getCode());
        apiLog.setExpendedTime(0);

        return insertApiLog(apiLog);
    }

    public void updateApiLog(String apiLogId, Integer code, String message) {
        PciApiLog apiLog = pciApiLogMapper.selectByPrimaryKey(apiLogId);
        apiLog.setCode(code);
        apiLog.setExpendedTime((int) (System.currentTimeMillis() - apiLog.getExecuteTime()));
        pciApiLogMapper.updateByPrimaryKey(apiLog);

        if (StringUtils.isNotEmpty(message)) {
            PciApiLogContent pciApiLogContent = new PciApiLogContent();
            pciApiLogContent.setPciApiLogId(apiLogId);
            pciApiLogContent.setMessage(message);
            pciApiLogContentMapper.insert(pciApiLogContent);
        }
    }

    public String getValue(String key) {
        SystemParameter systemParameter = parameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            // 不存在则插入
            systemParameter = new SystemParameter();
            systemParameter.setParamKey(key);
            systemParameter.setType("text");
            parameterMapper.insert(systemParameter);
        }
        return systemParameter.getParamValue();
    }

    public void updateSyncStatus(String key, String status) {
        SystemParameter systemParameter = new SystemParameter();
        systemParameter.setParamKey(key);
        systemParameter.setType("text");
        systemParameter.setParamValue(status);
        parameterMapper.updateByPrimaryKey(systemParameter);
    }

    public List<ExternalSystemSyncDetailLogRep> getSyncDetailLogs(ExternalSystemSyncDetailRequest request) {
        List<ExternalSystemSyncDetailLogRep> syncDetailLogs = extPciApiLogMapper.getSyncDetailLogs(request);

        Optional.ofNullable(syncDetailLogs).orElse(new ArrayList<>()).forEach(apiLog -> {
            apiLog.setCreateTimeText(DateUtil.getLong2ShortString(apiLog.getCreateTime(),DateUtil.YYYY_MM_DD_HH_MM_SS));
        });

        return syncDetailLogs;
    }


    public void deleteSyncDetailLogByApiLogId(String type) {
        PciExternalSystemSyncDetailLogExample example = new PciExternalSystemSyncDetailLogExample();
        example.createCriteria().andTypeEqualTo(Objects.equals(type, "orgSync"));
        pciExternalSystemSyncDetailLogMapper.deleteByExample(example);
    }

    public void insertSyncDetailLog(String type, String name, String soaId, String errmsg) {
        PciExternalSystemSyncDetailLog detailLog = new PciExternalSystemSyncDetailLog();
        detailLog.setId(IDGenerator.newBusinessId(PciTableIdConstants.PCI_API_SYNC_DETAIL_LOG_ID));
        detailLog.setCreateTime(System.currentTimeMillis());
        detailLog.setType(Objects.equals(type, "orgSync"));
        detailLog.setName(name);
        detailLog.setSoaId(soaId);
        detailLog.setErrMsg(errmsg);
        pciExternalSystemSyncDetailLogMapper.insert(detailLog);
    }

    public void insertApiRequestLog(String apiLogId, String requestBodyJson) {
        PciApiLog apiLog = pciApiLogMapper.selectByPrimaryKey(apiLogId);
        if (apiLog != null) {
            PciApiRequestLog apiRequestLog = new PciApiRequestLog();
            apiRequestLog.setPciApiLogId(apiLogId);
            apiRequestLog.setRequestBody(requestBodyJson);
            apiRequestLogMapper.insert(apiRequestLog);
        }
    }
}
