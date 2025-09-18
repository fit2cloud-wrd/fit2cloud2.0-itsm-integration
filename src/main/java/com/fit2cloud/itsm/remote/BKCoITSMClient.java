package com.fit2cloud.itsm.remote;

import com.fit2cloud.itsm.config.FeignConfig;
import com.fit2cloud.itsm.model.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;


//url = "http://172.168.153.14"
@FeignClient(
        name = "bkco-itsm-client",
        url = "https://paas-test.carizon.work",
        configuration = FeignConfig.class
)
public interface BKCoITSMClient {

    /**
     * 新建 ITSM 流程单
     * 请求样例：POST https://paas-test.carizon.work/api/c/compapi/v2/itsm/create_ticket/
     */
    @PostMapping(
            headers = {"Content-Type: application/json"},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    BKCoITSMOrderResp createProcess(URI baseUri, @RequestBody BKCoITSMOrderReq itsmOrder);

    /**
     * 更新 ITSM 流程单
     * 请求样例：http://172.168.153.14/store/openapi/v2/resources/save?apikey=e10adc3949ba59abbe56e057f2gg88dd&tenant_id=e10adc3949ba59abbe56e057f20f88dd
     */
    @PostMapping(
            headers = {"Content-Type: application/json"},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    BKCoITSMOrderResp updateProcess(URI baseUri, @RequestBody BKCoITSMOrderReq itsmOrder);

    /**
     * 查询 ITSM 工单详情
     * 请求url：https://paas-test.carizon.work/api/c/compapi/v2/itsm/get_ticket_info/
     */
    @PostMapping(
            headers = {"Content-Type: application/json"},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    BKCoITSMGetTicketInfoResp getTicketInfo(URI baseUri, @RequestBody BKCoITSMGetTicketInfoReq itsmOrder);

    /**
     * 更新 ITSM 工单状态（已完成）
     * 请求url：https://paas-test.carizon.work/api/c/compapi/v2/itsm/operate_node/
     */
    @PostMapping(
            headers = {"Content-Type: application/json"},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    BKCoITSMOperateNodeResp operateNode(URI baseUri, @RequestBody BKCoITSMGetOperateNodeReq itsmOrder);


}
