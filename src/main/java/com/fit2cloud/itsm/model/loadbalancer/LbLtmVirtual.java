package com.fit2cloud.itsm.model.loadbalancer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LbLtmVirtual implements Serializable {
    private String id;
    private String name;
    private String remark;
    private String part;
    private String fullPath;
    private String ip;
    private String port;
    private String status;
    private String snatType;
    private String snatPool;
    private String persist;
    private String accountId;
    private String poolId;
    private Long createTime;
    private Long syncTime;
    private Long deletedTime;
    private String bizName;
    private String workspaceId;
    private String template;
    private String orderId;
    private String owner;

}