package com.witeam.service.common.device.protocol;

import java.util.Objects;
import java.util.Set;

public class TopologyNodeProtocol {
    private String groupKey;

    private String sn;

    private Set<TopologyNodeProtocol> nodes;

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Set<TopologyNodeProtocol> getNodes() {
        return nodes;
    }

    public void setNodes(Set<TopologyNodeProtocol> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyNodeProtocol that = (TopologyNodeProtocol) o;
        return Objects.equals(groupKey, that.groupKey) &&
                Objects.equals(sn, that.sn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey, sn);
    }

    @Override
    public String toString() {
        return "TopologyNodeProtocol{" +
                "groupKey='" + groupKey + '\'' +
                ", sn='" + sn + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
