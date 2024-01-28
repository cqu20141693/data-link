package com.witeam.service.common.device.protocol;

public class TopologyProtocol {
    private int version;

    private TopologyNodeProtocol topology;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TopologyNodeProtocol getTopology() {
        return topology;
    }

    public void setTopology(TopologyNodeProtocol topology) {
        this.topology = topology;
    }

    @Override
    public String toString() {
        return "TopologyProtocol{" +
                "version=" + version +
                ", topology=" + topology +
                '}';
    }
}
