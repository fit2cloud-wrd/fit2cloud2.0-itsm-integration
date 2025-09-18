package com.fit2cloud.itsm.model.bkcmdb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BkInstanceAssociation {

    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "bk_obj_asst_id")
    private String bkObjAsstId;
    @JSONField(name = "bk_obj_id")
    private String bkObjId;
    @JSONField(name = "bk_asst_obj_id")
    private String bkAsstObjId;
    @JSONField(name = "bk_inst_id")
    private Integer bkInstId;
    @JSONField(name = "bk_asst_inst_id")
    private Integer bkAsstInstId;
}
