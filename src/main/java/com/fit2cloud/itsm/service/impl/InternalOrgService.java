package com.fit2cloud.itsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.common.constants.InternalConstants;
import org.springframework.stereotype.Service;

/**
 * 此类实现调用管理中心接口维护组织数据
 */
@Service
public class InternalOrgService extends InternalService {

    /**
     *  添加组织
     * */
    public Organization createOrg(String jsonParam){
        ResultHolder resultHolder = invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.CREATE_ORG_URL, jsonParam);
        return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()),Organization.class);
    }

    /**
     * 更新组织
     * */
    public void updateOrg(String jsonParam){
        ResultHolder resultHolder = invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.UPDATE_ORG_URL, jsonParam);
    }

}
