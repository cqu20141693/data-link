package com.chongctech.starter.device.link.api;

import com.chongctech.device.link.service.LinkService;
import io.swagger.annotations.ApiOperation;
import org.apache.pulsar.shade.io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gow
 * @date 2021/7/14
 */
@Api("device link api")
@RestController
@RequestMapping("api/device/link")
public class DeviceLinkAPI {

    @Autowired
    private LinkService linkService;

    @ApiOperation(value = "link is online check", notes = "查询链路是否在线")
    @GetMapping("isOnline")
    Boolean isOnline(@RequestParam("linkTag") String linkTag) {
        return linkService.isOnline(linkTag);
    }

    @ApiOperation(value = "send qos1 msg", notes = "发送qos 1 消息")
    @PostMapping("sendQos1Msg")
    Boolean sendQos1Msg(@RequestParam("linkTag") String linkTag, @RequestParam("bizId") String bizId,
                        @RequestParam("topic") String topic,
                        @RequestParam(value = "ackWaitTime", defaultValue = "-1") Long ackWaitTime,
                        @RequestBody byte[] content) {
        return linkService.sendQos1Msg(linkTag, bizId, topic, content, ackWaitTime);
    }

    @ApiOperation(value = "send qos0 msg", notes = "发送qos 0 消息")
    @PostMapping("sendQos0Msg")
    Boolean sendQos0Msg(@RequestParam("linkTag") String linkTag, @RequestParam("topic") String topic,
                        @RequestBody byte[] content) {
        return linkService.sendQos0Msg(linkTag, topic, content);
    }

    @ApiOperation(value = "disconnect Link by session", notes = "通过sessionKey断开链路")
    @GetMapping("disconnectLinkBySession")
    boolean disconnectLinkBySession(@RequestParam("linkTag") String linkTag,
                                    @RequestParam("sessionKey") String sessionKey,
                                    @RequestParam("time") Long time,
                                    @RequestParam("reasonCode") String reasonCode) {
        return linkService.disconnectLink(linkTag, sessionKey, time, reasonCode);
    }
}
