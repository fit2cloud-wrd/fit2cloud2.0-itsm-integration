### /api/c/compapi/v2/itsm/operate_ticket/ 单据操作接口  已作废订单时调用接口
###  sn	string	是	单号
    #operator	string	是	单据处理人，必须在处理人范围内
    #action_type	string	是	操作类型：SUSPEND（挂起）/UNSUSPEND（恢复）/WITHDRAW（撤销）/TERMINATE（终止）
    #action_message	string	否	操作备注信息（挂起和终止操作必填，其他操作类型选填）
curl -X POST "https://paas-test.carizon.work/api/c/compapi/v2/itsm/operate_ticket/" \
  -H "Content-Type: application/json" \
  -d '{
           "bk_app_code": "bk_cmdb",
           "bk_app_secret": "e4a0f6e3-5f7b-4438-92bf-b3efaf1c5e25",
           "bk_username": "lijun.zhang@carizon.work",
           "sn": "CRQ20250115000001",
           "operator": "admin",
           "action_type": "TERMINATE",
           "action_message": "已作废"
         }'

### 返回结果 {"result": true, "code": 0, "message": "success", "data": [], "request_id": "09dd88e9062b4b22be4f4d5fcbbaab63"}