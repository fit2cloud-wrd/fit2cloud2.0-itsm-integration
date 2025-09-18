package com.fit2cloud.itsm.model.dto;

public class ProcessRequest {

    private String externalProcessId;
    private String assign;
    private String remark;

    public String getExternalProcessId() {
        return externalProcessId;
    }

    public void setExternalProcessId(String externalProcessId) {
        this.externalProcessId = externalProcessId;
    }

    public String getAssign() {
        return assign;
    }

    public void setAssign(String assign) {
        this.assign = assign;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
