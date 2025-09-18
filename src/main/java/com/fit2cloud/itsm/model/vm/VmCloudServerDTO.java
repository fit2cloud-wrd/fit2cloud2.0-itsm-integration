package com.fit2cloud.itsm.model.vm;

import com.fit2cloud.commons.server.base.domain.CloudServerCredential;
import com.fit2cloud.commons.server.base.domain.TagMapping;
import com.fit2cloud.commons.server.model.CloudServerDTO;
import com.fit2cloud.commons.server.model.request.ExpirePolicyRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class VmCloudServerDTO extends CloudServerDTO {
    @ApiModelProperty("产品ID")
    private String productId;
    private String productIcon;
    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("申请人")
    private String applyUser;
    @ApiModelProperty("申请人名称")
    private String applyUserName;
    @ApiModelProperty("归属人")
    private String owner;
    @ApiModelProperty("归属人名称")
    private String ownerName;
    @ApiModelProperty("到期时间")
    private Long expiredTime;
    @ApiModelProperty("回收策略")
    private int recyclePolicy;
    @ApiModelProperty("所属集群")
    private String appCluster ;
    @ApiModelProperty("所属主机组")
    private String appRole;
    @ApiModelProperty("所属系统")
    private String systemNames;
    @ApiModelProperty("所属系统别名")
    private String systemAlias;
    @ApiModelProperty("[安全组id] 安全组名称")
    private String securityGroupName;
    @ApiModelProperty("所属应用")
    private String applicationNames;
    @ApiModelProperty("所属应用别名")
    private String applicationAlias;
    @ApiModelProperty("自动续费")
    private Boolean autoRenew;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("到期策略")
    private ExpirePolicyRequest.ExpireOperation expireOperation;
    // 密码
    private String password;
    //账单调用配置变更确认单 传过来的新的配置
    private Map<String,Object> newInstanceType;

    @ApiModelProperty("豁免，0代表未生效，1代表生效中")
    private Boolean exempt;

    @ApiModelProperty("豁免原因")
    private String exemptReason;

    @ApiModelProperty("豁免截止时间")
    private Long exemptTime;

    @ApiModelProperty("代理IP")
    private String proxyIp;

    @ApiModelProperty("是否提交回收订单标识;0:未提交;1:已提交,尚未通过;2:已提交,已通过")
    private String isApplyRecycle;
    
    @ApiModelProperty("回收时间")
    private Long recycleTime;

    @ApiModelProperty("所属项目")
    private String projectName;

    @ApiModelProperty("连接VNC实例控制台密码")
    private String vncPassword;

    @ApiModelProperty("停机时长")
    private String stopTimeStr;

    @ApiModelProperty("停机时间差")
    private String stopTimeBetween;

    @ApiModelProperty("存储器名称")
    private String dataStoreName;
    @ApiModelProperty
    private CloudServerCredential  credential;

    @ApiModelProperty("监控代理安装状态")
    private String monitoringAgentStatus;

    @ApiModelProperty("监控代理版本")
    private String monitoringAgentVersion;

    @ApiModelProperty("标签tagMappings")
    private List<TagMapping> tagMappings;
}
