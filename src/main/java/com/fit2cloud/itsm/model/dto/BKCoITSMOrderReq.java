package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.JSONObject;
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
public class BKCoITSMOrderReq {

    /**
     * bk_app_code	string	是	应用 ID
     * bk_app_secret	string	是	安全密钥(应用 TOKEN)，可以通过 蓝鲸智云开发者中心 -> 点击应用 ID -> 基本信息 获取
     * bk_token	string	否	当前用户登录态，bk_token 与 bk_username 必须一个有效，bk_token 可以通过 Cookie 获取
     * bk_username	string	否	当前用户用户名，应用免登录态验证白名单中的应用，用此字段指定当前用户
     * service_id	int	否	服务 id
     * creator	string	是	单据创建者
     * fields	array	是	提单字段
     * fast_approval	boolean	否	是否为单点快速审批单
     * meta	dict	否	扩展信息
     *     callback_url	string	否	回调 url，若有则会触发回调
     *     state_processors	object	否	节点处理人，若有则单据流转时会按此处理人设置
     *
     *
     *

        {
            "bk_app_secret": "e4a0f6e3-5f7b-4438-92bf-b3efaf1c5e25",
            "bk_app_code": "bk_cmdb",
            "bk_username": "lijun.zhang@carizon.work",
            "catalog_id": 3,
            "service_id": 10,
            "service_type": "request",
            "creator": "lijun.zhang@carizon.work",
            "fields": [
                {
                    "type": "STRING",
                    "key": "title",
                    "value": "dwr-1114-test-01"
                },
                {
                    "type": "TABLE",
                    "key": "ZIYUANLIEBIAO",
                    "value": [{
                        "vmName": "vm2",
                        "vmIp": "ip2"
                    }],
                    "choice": [
                        {
                            "key": "vmName",
                            "name": "资源名称",
                            "required": false
                        },
                        {
                            "key": "vmIp",
                            "name": "资源地址",
                            "required": false
                        }
                    ]
                }
            ],
            "fast_approval": false
        }

     *
     */
    private String bk_app_code;

    private String bk_app_secret;

    private String bk_username;

    private int service_id = 10;

    private String service_type = "request";

    private String creator;

    private boolean fast_approval = false;

    private List<BKCoITSMOrderFieldReq> fields = new ArrayList<>();

    private Map<String, Object> meta = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BKCoITSMOrderFieldReq {

        private String type = "STRING";

        private String key;

        private Object value;

        /** 仅当 type="TABLE" */
        private List<BKCoITSMOrderFieldReq> choice;

        /** 仅当 type="CUSTOMTABLE" */
        private Map<String, Object> meta;

        /** 仅当 type="TABLE" */
        private boolean required = false;

        /** 仅当 type="TABLE" */
        private String name;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CustomTableMetaColumns {
        //                          "visible": true,
        //                         "required": false,
        //                         "name": "数量",
        //                         "key": "SHULIANG1",
        //                         "display": "input",
        //                         "default_value": "",
        //                         "customRegex": "",
        //                         "desc": "",
        //                         "is_tips": false,
        //                         "tips": "",
        //                         "is_readonly": false,
        //                         "is_text": false,
        //                         "max_line": 6,
        //                         "source_type": "CUSTOM",
        //                         "choice": [],
        //                         "firstOption": false

        private boolean visible = true;
        private boolean required;
        private String name;
        private String key;
        private String display = "input";
        private String default_value;
        private String customRegex;
        private String desc;
        private boolean is_tips;
        private String tips;
        private boolean is_readonly;
        private boolean is_text;
        private int max_line = 6;
        private String source_type = "CUSTOM";
        private List choice = new ArrayList<>();
        private boolean firstOption;
    }

}
