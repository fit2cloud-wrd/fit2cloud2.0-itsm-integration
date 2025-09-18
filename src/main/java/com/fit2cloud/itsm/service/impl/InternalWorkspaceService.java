package com.fit2cloud.itsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.common.constants.InternalConstants;
import org.springframework.stereotype.Service;

/**
 * 此类实现调用管理中心接口维护工作空间数据
 */
@Service
public class InternalWorkspaceService extends InternalService {

    /**
     * 创建工作空间
     * */
    public Workspace createWorkspace(String jsonParam){
        ResultHolder resultHolder = invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.CREATE_WORKSPACE_URL, jsonParam);
        return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), Workspace.class);
    }

    /**
     * 更新工作空间
     * */
    public void updateWorkspace(String jsonParam){
        invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.UPDATE_WORKSPACE_URL, jsonParam);
    }

}
