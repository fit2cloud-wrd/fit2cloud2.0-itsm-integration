package com.fit2cloud.itsm.model.response;

import com.fit2cloud.itsm.model.PciSyncUserSetting;

public class PciSyncUserSettingRep extends PciSyncUserSetting {
    private String createTimeText;
    private String updateTimeText;

    public String getCreateTimeText() {
        return createTimeText;
    }

    public void setCreateTimeText(String createTimeText) {
        this.createTimeText = createTimeText;
    }

    public String getUpdateTimeText() {
        return updateTimeText;
    }

    public void setUpdateTimeText(String updateTimeText) {
        this.updateTimeText = updateTimeText;
    }
}