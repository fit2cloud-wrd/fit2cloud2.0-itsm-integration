package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BKCoITSMOperateNodeResp {

    private boolean result;

    private String code;

    private String message;

    private JSONObject data;

    private String request_id;

}
