package com.witeam.service.common.device.deliver.raw;

import java.util.HashMap;
import java.util.Map;

public class SendActionMsg {
    /**
     * 链路标记
     */
    private String linkTag;

    /**
     * 变更时间戳
     */
    private long timeStamp;

    /**
     * 消息类型
     */
    private ActionType actionType;

    /**
     * 业务id号
     */
    private String bizId;

    public String getLinkTag() {
        return linkTag;
    }

    public void setLinkTag(String linkTag) {
        this.linkTag = linkTag;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public enum ActionType {
        SEND("send"),

        SEND_FAIL("sendFail"),

        ACK("ack"),

        ACK_EXPIRE("ackExpire"),

        UNKNOWN("unknown");

        private final static Map<String, ActionType> INNER;

        static {
            INNER = new HashMap<>();
            for (ActionType cmdStatus : ActionType.values()) {
                INNER.put(cmdStatus.code, cmdStatus);
            }
        }


        private String code;

        ActionType(String code) {
            this.code = code;
        }

        public static ActionType parseFromCode(String code) {
            return INNER.getOrDefault(code, UNKNOWN);
        }

        public String getCode() {
            return code;
        }
    }
}
