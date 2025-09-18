package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BKCoITSMGetOperateNodeReq {

   /*
   *    {
           "bk_app_code": "bk_cmdb",
           "bk_app_secret": "e4a0f6e3-5f7b-4438-92bf-b3efaf1c5e25",
           "bk_username": "lijun.zhang@carizon.work",
           "sn": "CRQ20250205000002",
           "operator": "admin",
           "state_id": 248,
           "action_type": "TRANSITION",
           "fields": [
             {
               "key": "bk_itsm74c07087f7ba28cdabb495055",
               "value": "true"
             },
             {
               "key": "bk_itsm017a6987a89b276813e5809d4",
               "value": "通过备注"
             }
           ]
         }
   * */
    private String bk_app_code;
    private String bk_app_secret;
    private String bk_username;
    private String sn;
    private String operator;
    private int state_id;
    private String action_type = "TRANSITION";
    private JSONArray fields;



}
