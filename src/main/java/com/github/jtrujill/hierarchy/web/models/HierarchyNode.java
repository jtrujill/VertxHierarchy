package com.github.jtrujill.hierarchy.web.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class HierarchyNode {
    private String id;
    private String parentId;
    private String group;

    public HierarchyNode(JsonObject json) {
        HierarchyNodeConverter.fromJson(json, this);
    }

    public HierarchyNode(HierarchyNode other) {
        this.id = other.id;
        this.parentId = other.parentId;
        this.group = other.group;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        HierarchyNodeConverter.toJson(this, json);
        return json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
