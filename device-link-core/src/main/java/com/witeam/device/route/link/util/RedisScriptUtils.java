package com.witeam.device.route.link.util;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

/**
 * @author gow 2024/01/24
 */

@Component
public class RedisScriptUtils implements SmartInitializingSingleton {
    private final DefaultRedisScript<String> registerScript = new DefaultRedisScript();
    private final DefaultRedisScript<Boolean> disconnectScript = new DefaultRedisScript();
    private final DefaultRedisScript<Long> refreshScript = new DefaultRedisScript();

    public RedisScriptUtils() {
    }

    public DefaultRedisScript<String> getRegisterScript() {
        return this.registerScript;
    }

    public DefaultRedisScript<Boolean> getDisconnectScript() {
        return this.disconnectScript;
    }

    public DefaultRedisScript<Long> getRefreshScript() {
        return this.refreshScript;
    }

    public void afterSingletonsInstantiated() {
        this.registerScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/register.lua")));
        this.registerScript.setResultType(String.class);
        this.disconnectScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/disconnect.lua")));
        this.disconnectScript.setResultType(Boolean.class);
        this.refreshScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/refresh.lua")));
        this.refreshScript.setResultType(Long.class);
    }
}
