package com.fit2cloud.itsm.model.bkcmdb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BkObjEntity {

    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "bk_classification_id")
    private String bkClassificationId;
    @JSONField(name = "bk_obj_id")
    private String bkObjId;
    @JSONField(name = "bk_obj_name")
    private String bkObjName;

}
