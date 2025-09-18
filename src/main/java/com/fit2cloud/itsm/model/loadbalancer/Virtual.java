package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Virtual extends ModuleDto {
    private String addressStatus;
    private String autoLasthop;
    private String cmpEnabled;
    private Integer connectionLimit;
    private String description;
    private String destination;
    private Boolean enabled;
    private Integer gtmScore;
    private String ipProtocol;
    private String mask;
    private String mirror;
    private String mobileAppTunnel;
    private String nat64;
    private String pool;
    private PoolReference reference;
    private String rateLimit;
    private Integer rateLimitDstMask;
    private String rateLimitMode;
    private Integer rateLimitSrcMask;
    private String serviceDownImmediateAction;
    private String source;
    private SourceAddressTranslation sourceAddressTranslation;
    private String sourcePort;
    private String synCookieStatus;
    private String translateAddress;
    private String translatePort;
    private List<String> rules;
    private Boolean vlansDisabled;
    private Integer vsIndex;
    private PoliciesReference policiesReference;
    private List<Persist> persist;
    private List<Reference> rulesReference;
    private Reference poolReference;
    private PoliciesReference profilesReference;
    private List<VirtualProfile> profiles;
    private Date creationTime;
    private String address;
    private Boolean disabled;
    private Profiles profile;
    

}
