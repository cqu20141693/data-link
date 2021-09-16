package com.chongctech.device.link.biz.model.link;

/**
 * @author gow
 * @date 2021/8/11
 */
public enum LinkSysCode {

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

    ;

    LinkSysCode(String code, String zhDesc) {
        this.code = code;
        this.zhDesc = zhDesc;
    }

    public String getCode() {
        return code;
    }

    public String getZhDesc() {
        return zhDesc;
    }

    private final String code;
    private final String zhDesc;
}
