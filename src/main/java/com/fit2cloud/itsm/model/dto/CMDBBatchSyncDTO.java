package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.CloudServer;

public class CMDBBatchSyncDTO {
    private JSONObject editCMDBResourceReq;
    private CloudServer cloudServer;

    public JSONObject getEditCMDBResourceReq() {
        return editCMDBResourceReq;
    }

    public void setEditCMDBResourceReq(JSONObject editCMDBResourceReq) {
        this.editCMDBResourceReq = editCMDBResourceReq;
    }

    public CloudServer getCloudServer() {
        return cloudServer;
    }

    public void setCloudServer(CloudServer cloudServer) {
        this.cloudServer = cloudServer;
    }
}
