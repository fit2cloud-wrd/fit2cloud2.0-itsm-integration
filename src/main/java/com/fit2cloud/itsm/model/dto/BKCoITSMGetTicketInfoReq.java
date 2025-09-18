package com.fit2cloud.itsm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BKCoITSMGetTicketInfoReq {

   /*
   *    {
           "bk_app_code": "bk_cmdb",
           "bk_app_secret": "e4a0f6e3-5f7b-4438-92bf-b3efaf1c5e25",
           "bk_username": "lijun.zhang@carizon.work",
           "sn": "CRQ20250205000002"
         }
   * */
    private String bk_app_code;

    private String bk_app_secret;

    private String bk_username;

    private String sn;

}
