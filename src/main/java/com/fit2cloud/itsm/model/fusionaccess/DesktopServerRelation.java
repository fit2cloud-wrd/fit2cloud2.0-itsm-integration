package com.fit2cloud.itsm.model.fusionaccess;


import lombok.Data;

import java.io.Serializable;

@Data
public class DesktopServerRelation implements Serializable {
    private Long id;
    private String desktopServerId;
    private String desktopServerSid;
    private String resourceType;
    private String desktopServerVmId;
    private String resourceData;
}