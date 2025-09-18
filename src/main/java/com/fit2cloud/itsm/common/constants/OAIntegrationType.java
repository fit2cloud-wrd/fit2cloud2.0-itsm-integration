package com.fit2cloud.itsm.common.constants;

public enum OAIntegrationType {
    // 外部系统流程: 在 OA 系统中创建一个流程单，然后单击按钮或链接跳转至 CE 平台填写详细申请内容，在 CE 中提交订单后再返回 OA 系统提交流程单。
    // OA创建工单————》点击跳转云管填写订单信息————》提交订单后，给OA已有的工单发送流程对应任务的详细描述————》OA审批后调用云管接口完成审批动作
    ExternalOrderReadyBeforeSubmit,
    // 云管流程: 开发测试人员直接在 CE 中申请订单，过程中无需访问 OA 系统。推荐使用 b 方式，对于开发测试人员来说只需要使用 CE 系统，对于审批人员来说只需要使用 OA 系统。
    // 云管中申请订单————》提交订单后按照云管流程事件通知OA创建工单————》OA审批后调用云管接口完成审批动作
    CreateExternalOrderWhenSubmit;
}
