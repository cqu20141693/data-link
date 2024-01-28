package com.witeam.device.route.link.service.impl;

import com.witeam.device.route.link.domain.DeviceRouteTableInfo;
import com.witeam.device.route.link.service.DeviceRouteTableService;
import com.witeam.device.route.link.util.RedisScriptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author gow 2024/01/23
 */

@Component
public class DeviceRouteTableServiceImpl implements DeviceRouteTableService {
    private static final Logger log = LoggerFactory.getLogger(DeviceRouteTableServiceImpl.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisScriptUtils redisScriptUtils;

    public DeviceRouteTableServiceImpl() {
    }

    public DeviceRouteTableInfo addRouteKey(String routeKey, DeviceRouteTableInfo deviceRouteTableInfo, int second) {
        log.debug("addRouteKey- routeKey:{} ,deviceRouteTBInfo:{},second:{}", new Object[]{routeKey, deviceRouteTableInfo.toRedisValue(), second});
        String key = "D:R" + routeKey;
        LinkedList<String> keys = new LinkedList();
        keys.add(key);
        String value = deviceRouteTableInfo.toRedisValue();
        String result = (String)this.redisTemplate.execute(this.redisScriptUtils.getRegisterScript(), keys, new Object[]{value, String.valueOf(second)});
        if (!result.isBlank() && !Objects.equals("0", result)) {
            DeviceRouteTableInfo info = new DeviceRouteTableInfo();
            info.fromRedisValue(result);
            return info;
        } else {
            return null;
        }
    }

    public int refreshRouteKey(String routeKey, DeviceRouteTableInfo deviceRouteTableInfo, int second) {
        log.debug("refreshRouteKey- routeKey:{} ,deviceRouteTBInfo:{},second:{}", new Object[]{routeKey, deviceRouteTableInfo.toRedisValue(), second});
        String deviceKey = "D:R" + routeKey;
        String value = deviceRouteTableInfo.toRedisValue();
        List<String> keys = new LinkedList();
        keys.add(deviceKey);

        try {
            Long result = (Long)this.redisTemplate.execute(this.redisScriptUtils.getRefreshScript(), keys, new Object[]{value, String.valueOf(second)});
            return result.intValue();
        } catch (Exception var9) {
            log.error("refreshMqttKey error", var9);
            return -1;
        }
    }

    public Boolean removeRouteKey(String routeKey, DeviceRouteTableInfo deviceRouteTableInfo) {
        log.debug("removeRouteKey- routeKey:{} ,deviceRouteTBInfo:{}", routeKey, deviceRouteTableInfo.toRedisValue());
        String key = "D:R" + routeKey;
        String value = deviceRouteTableInfo.toRedisValue();
        List<String> keys = new LinkedList();
        keys.add(key);

        try {
            return (Boolean)this.redisTemplate.execute(this.redisScriptUtils.getDisconnectScript(), keys, new Object[]{value});
        } catch (Exception var7) {
            log.error("removeRouteKey error", var7);
            return false;
        }
    }

    public DeviceRouteTableInfo getDeviceRouteInfo(String routeKey) {
        String value = (String)this.redisTemplate.opsForValue().get("D:R" + routeKey);
        return (DeviceRouteTableInfo) Optional.ofNullable(value).map((v) -> {
            DeviceRouteTableInfo info = new DeviceRouteTableInfo();
            info.fromRedisValue(v);
            return info;
        }).orElse(null);
    }
}
