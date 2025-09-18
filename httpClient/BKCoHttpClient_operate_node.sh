### /api/c/compapi/v2/itsm/operate_node/ 单据节点操作接口 执行完成后调用接口
curl -X POST "https://paas-test.carizon.work/api/c/compapi/v2/itsm/operate_node/" \
  -H "Content-Type: application/json" \
  -d '{
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
         }'

### 返回结果 {"code": "OK", "message": "success", "result": true, "request_id": "a77e9fb8145c4e3c91992de0e3ef8e5c", "data": null}