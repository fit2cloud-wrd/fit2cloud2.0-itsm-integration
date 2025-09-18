package com.fit2cloud.itsm.model.response;

import lombok.Data;

@Data
public class ResponseResult<T> {
    private boolean success;
    private int httpCode;
    private String msg;
    private T data;
}
