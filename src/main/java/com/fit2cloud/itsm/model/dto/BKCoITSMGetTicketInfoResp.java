package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BKCoITSMGetTicketInfoResp {

   /*
   *   详见 BKCoHttpClient_get_ticket_info.json
   * */
    private boolean result;

    private int code;

    private String message;

    private JSONObject data;

    private String request_id;

}
