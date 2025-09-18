package com.fit2cloud.itsm.model.bkcmdb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BkObjectsAttribute {
    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;
    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "bk_obj_id")
    private Integer bkObjId;
    @JSONField(name = "bk_property_id")
    private String bkPropertyId;
    @JSONField(name = "bk_property_name")
    private String bkPropertyName;
    @JSONField(name = "bk_property_group")
    private String bkPropertyGroup;
    @JSONField(name = "bk_property_index")
    private Integer bkPropertyIndex;
    @JSONField(name = "bk_property_group_name")
    private String bkPropertyGroupName;
    @JSONField(name = "isonly")
    private boolean isonly;

}
