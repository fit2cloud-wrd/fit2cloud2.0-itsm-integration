package com.fit2cloud.itsm.model.response;


import com.fit2cloud.itsm.model.PciExternalSystemSyncDetailLog;

/**
 * 插入CMDB的资源模型
 */
public class ExternalSystemSyncDetailLogRep extends PciExternalSystemSyncDetailLog {
    private String createTimeText;

    public String getCreateTimeText() {
        return createTimeText;
    }

    public void setCreateTimeText(String createTimeText) {
        this.createTimeText = createTimeText;
    }
}
