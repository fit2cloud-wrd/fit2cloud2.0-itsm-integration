curl -X POST "https://paas-test.carizon.work/api/c/compapi/v2/itsm/create_ticket/" \
     -H "Content-Type: application/json" \
     -d '{
           "bk_app_code": "bk_cmdb",
           "bk_app_secret": "e4a0f6e3-5f7b-4438-92bf-b3efaf1c5e25",
           "bk_username": "lijun.zhang@carizon.work",
           "service_id": 29,
           "service_type": "request",
           "creator": "lijun.zhang@carizon.work",
           "fast_approval": false,
           "fields": [
             {
               "type": "STRING",
               "key": "title",
               "value": "[CREATE]vm-202501122241-83bd0383",
               "required": true
             },
             {
               "type": "CUSTOMTABLE",
               "key": "PEIZHIXINXIBIAOGE",
               "value": [
                 {
                   "JIQIMING1": "fit2cloud-ae1d7b75",
                   "SHIFOUJIARUYU1": "否",
                   "CAOZUOXITONG1": "redhat",
                   "ZUQI1": "一天",
                   "SHUJUPANDAXIAO(GB)1": "100",
                   "SHILILEIXING1": "vm.4C8G [4核8G]",
                   "CAOZUOXITONGBANBEN1": "8.7",
                   "SHUJUPANGUAZAIDIAN1": "/opt",
                   "XITONGPANDAXIAO(GB)1": "100",
                   "SHULIANG1": "1"
                 },
                 {
                   "JIQIMING1": "fit2cloud-d9c08c19",
                   "SHIFOUJIARUYU1": "否",
                   "CAOZUOXITONG1": "redhat",
                   "ZUQI1": "一天",
                   "SHUJUPANDAXIAO(GB)1": "100",
                   "SHILILEIXING1": "vm.4C8G [4核8G]",
                   "CAOZUOXITONGBANBEN1": "8.7",
                   "SHUJUPANGUAZAIDIAN1": "/opt",
                   "XITONGPANDAXIAO(GB)1": "100",
                   "SHULIANG1": "1"
                 }
               ],
               "choice": [],
               "required": false,
               "meta": {
                   "columns": [
                      {
                        "visible": true,
                        "required": false,
                        "display": "input",
                        "is_readonly": false,
                        "is_text": false,
                        "firstOption": false,
                        "key": "JIQIMING1",
                        "required": false,
                        "name": "机器名"
                      },
                      {
                        "type": "BOOLEAN",
                        "key": "SHIFOUJIARUYU1",
                        "required": false,
                        "name": "是否加域"
                      },
                      {
                        "type": "STRING",
                        "key": "CAOZUOXITONG1",
                        "required": false,
                        "name": "操作系统"
                      },
                      {
                        "type": "STRING",
                        "key": "ZUQI1",
                        "required": false,
                        "name": "租期"
                      },
                      {
                        "type": "STRING",
                        "key": "SHUJUPANDAXIAO(GB)1",
                        "required": false,
                        "name": "数据盘大小"
                      },
                      {
                        "type": "STRING",
                        "key": "SHILILEIXING1",
                        "required": false,
                        "name": "实例类型"
                      },
                      {
                        "type": "STRING",
                        "key": "CAOZUOXITONGBANBEN1",
                        "required": false,
                        "name": "操作系统版本"
                      },
                      {
                        "type": "STRING",
                        "key": "SHUJUPANGUAZAIDIAN1",
                        "required": false,
                        "name": "数据盘挂载点"
                      },
                      {
                        "type": "STRING",
                        "key": "XITONGPANDAXIAO(GB)1",
                        "required": false,
                        "name": "系统盘大小"
                      },
                      {
                         "visible": true,
                         "required": false,
                         "name": "数量",
                         "key": "SHULIANG1",
                         "display": "input",
                         "default_value": "",
                         "customRegex": "",
                         "desc": "",
                         "is_tips": false,
                         "tips": "",
                         "is_readonly": false,
                         "is_text": false,
                         "max_line": 6,
                         "source_type": "CUSTOM",
                         "choice": [],
                         "firstOption": false
                      }
                   ]
               }
             },
             {
               "type": "STRING",
               "key": "SHENQINGYUANYIN",
               "value": "test",
               "required": true
             },
             {
               "type": "STRING",
               "key": "SHENQINGREN",
               "value": "lijun.zhang",
               "required": true
             },
             {
               "type": "STRING",
               "key": "DINGDANBIANHAO",
               "value": "vm-202501122241-83bd0383",
               "required": true
             },
             {
               "type": "MEMBERS",
               "key": "ZUZHIGUANLIYUAN",
               "value": "lijun.zhang",
               "required": true
             },
             {
               "type": "MEMBERS",
               "key": "XITONGGUANLIYUAN",
               "value": "jinli.zhang,lijun.zhang",
               "required": true
             }
           ],
           "meta": {}
         }'
