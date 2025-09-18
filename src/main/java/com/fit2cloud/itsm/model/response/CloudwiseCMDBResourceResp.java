package com.fit2cloud.itsm.model.response;

public class CloudwiseCMDBResourceResp {
    private String status;
    private String msg;
    private Long code;
    private CloudwiseCMDBResourceItemResp data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public CloudwiseCMDBResourceItemResp getData() {
        return data;
    }

    public void setData(CloudwiseCMDBResourceItemResp data) {
        this.data = data;
    }
}
