package com.witeam.service.common.device.protocol;

public class TopologyChangeProtocol {

    private TopologyChangeNodeProtocol fn;

    private TopologyChangeNodeProtocol cn;

    public TopologyChangeNodeProtocol getFn() {
        return fn;
    }

    public void setFn(TopologyChangeNodeProtocol fn) {
        this.fn = fn;
    }

    public TopologyChangeNodeProtocol getCn() {
        return cn;
    }

    public void setCn(TopologyChangeNodeProtocol cn) {
        this.cn = cn;
    }
}
