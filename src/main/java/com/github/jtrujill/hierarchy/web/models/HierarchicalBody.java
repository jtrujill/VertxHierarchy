package com.github.jtrujill.hierarchy.web.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

@DataObject(generateConverter = true, publicConverter = false)
public class HierarchicalBody {
    private List<String> nodes = new ArrayList<>();

    public HierarchicalBody(JsonObject json) {
        HierarchicalBodyConverter.fromJson(json, this);
    }

    public HierarchicalBody(HierarchicalBody other) {
        this.nodes = List.copyOf(other.nodes);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        HierarchicalBodyConverter.toJson(this, json);
        return json;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
