package com.fit2cloud.itsm.model.fusionaccess;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
public class DesktopServerDTO extends DesktopServer {
    private String workspaceName;
    private String userOrGroupName;
    private List<DesktopServerRelation> relationList;
    private String vmQosInfo;
    private String accountName;
    private String applyUser;
    private String applyUserName;
    private String owner;
    private String ownerName;
}
