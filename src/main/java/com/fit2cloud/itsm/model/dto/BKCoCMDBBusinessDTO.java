package com.fit2cloud.itsm.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BKCoCMDBBusinessDTO {
    Integer bkBizId;
    String bkBizName;

    Integer bkRegionId;
    String bkRegionName;

    Integer bkSetId;
    String bkSetName;

    Integer bkModuleId;
    String bkModuleName;

}
