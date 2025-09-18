package com.fit2cloud.itsm.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BKCoCMDBBusinessRes {


    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;
    @JSONField(name = "bk_host_id")
    private Integer bkHostId;
    @JSONField(name = "bk_supplier_account")
    private String bkSupplierAccount;
    @JSONField(name = "bk_set_id")
    private Integer bkSetId;
    @JSONField(name = "bk_module_id")
    private Integer bkModuleId;
}
