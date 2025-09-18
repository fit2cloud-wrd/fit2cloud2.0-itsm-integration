package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class LbLtmVirtualRet extends LbLtmVirtual {
    private String accountName;
    private LbLtmPool pool;
    private List<LbLtmMember> members;
    private List workspaces;
    private Integer workspaceCount;
    private Long expiredTime;
    private String ownerName;
    private String organizationId;
    private String projectName;
    private String systemName;
    private String loadBalancingMode;
    private String workspaceName;
    private String organizationName;
    private String poolName; //节点池名字
    private String membersSize;
    //用于邮件内容参数
    private String creationTime;
    private String persist;
}
