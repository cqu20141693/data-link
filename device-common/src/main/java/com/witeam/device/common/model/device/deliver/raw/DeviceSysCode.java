package com.witeam.device.common.model.device.deliver.raw;

/**
 * @author gow 2024/01/23
 */

public enum DeviceSysCode {
    ONLINE("0001", "上线"),
    CLIENT_SEND_DISCONNECT("0002", "客户端下线事件"),
    SERVER_RESTART("0003", "服务端重启"),
    QOS_2_NOT_SUPPORT("0004", "qos2不支持"),
    NEW_CLIENT_ONLINE("0005", "新客户端登录"),
    SERVER_ERROR("0006", "服务器端错误"),
    CONN_ACK_SEND_FAIL("0007", "连接响应发送失败"),
    INACTIVE_WHILE_CONNECTED("0008", "连接时链路不可用"),
    PUB_NOT_AUTHORITY("0009", "消息推送没有权限"),
    PUB_NULL_ERROR("0010", "推送消息为空"),
    PUB_HEADER_NULL_ERROR("0011", "推送消息头为空"),
    PUB_TOPIC_NULL_ERROR("0012", "推送topic为空"),
    INVALID_LINK("0013", "非法链路"),
    SUB_NOT_SUPPORT("0014", "订阅消息不支持"),
    CONNECTION_LOST("0015", "客户端链路丢失"),
    SYS_EXCEPTION_ERROR("0016", "链路发生异常"),
    SERVER_PROCESS_ERROR("0017", "服务端处理错误"),
    PACKET_ERROR("0018", "报文错误"),
    PACKET_DECODE_ERROR("0019", "报文解码错误"),
    DUPLICATE_CONN_PACKET("0020", "重复连接报文"),
    UNKNOWN_PACKET_TYPE("0021", "未知报文类型"),
    PING("0022", "保活心跳"),
    SAME_CLIENT_ONLINE("1001", "新客户端登录"),
    DEVICE_DISABLED("2002", "设备被禁用"),
    INVALID_TOPIC("1003", "无效topic"),
    PAYLOAD_DECRYPT_ERROR("1004", "荷载解密错误"),
    INVALID_STREAM_ERROR("1005", "无效数据流"),
    PAYLOAD_DECODE_ERROR("1006", "荷载解码错误"),
    SUB_GROUP_ROUTE_EMPTY_ERROR("1007", "订阅路由信息不存在"),
    BIZ_COMMON_ERROR("9999", "业务处理失败"),
    DEVICE_SCHEMA_UPDATE_NOTICE_ERROR("2001", "设备模型更新通知失败"),
    PRE_SCHEMA_UPDATE_ERROR("2003", "预置模型更新通知失败"),
    TOPOLOGY_UPDATE_NOTICE_ERROR("2004", "拓扑结构更新通知失败"),
    DEVICE_DISABLED_NOTICE_ERROR("2005", "设备禁用下线通知失败");

    private String code;
    private String zhDesc;

    private DeviceSysCode(String code, String zhDesc) {
        this.code = code;
        this.zhDesc = zhDesc;
    }

    public String getCode() {
        return this.code;
    }

    public String getZhDesc() {
        return this.zhDesc;
    }
}
