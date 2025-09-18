package com.fit2cloud.itsm.model.dto;

import com.fit2cloud.commons.server.model.TreeNode;

public class McOrgTreeNode extends TreeNode {
    private String parentId;
    private int layer;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
