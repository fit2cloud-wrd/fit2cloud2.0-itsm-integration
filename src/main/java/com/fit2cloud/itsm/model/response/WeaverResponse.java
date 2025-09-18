package com.fit2cloud.itsm.model.response;

import lombok.Data;

@Data
public class WeaverResponse {

    private String syscode;
    private String operResult;
    private String dataType;
    private String errorCode;
    private String operType;
    private String message;
    private String cid;
}
