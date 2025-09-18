package com.fit2cloud.itsm.model.request;

import java.util.List;

/**
 *
 */
public class QueryConditions {

    /**
     * 查询条件
     */
    private List<QueryUnit> conditions;


    public QueryConditions() {
    }

    public List<QueryUnit> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryUnit> conditions) {
        this.conditions = conditions;
    }

}
