package com.fit2cloud.itsm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BKCoITSMOrderResp {

    /**
     * {
     *     "result": true,
     *     "code": 0,
     *     "message": "success",
     *     "data": {
     *         "sn": "REQ20241112000007"
     *     },
     *     "request_id": "a7d8e397a2744a15ae4b221172e5298f"
     * }
     */

    private boolean result;

    private int code;

    private String message;

    private String request_id;

    private BKCoITSMOrderInfoResp data;

}
