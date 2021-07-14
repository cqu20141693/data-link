package com.chongctech.device.link.server.bootstrap;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author gow
 * @date 2021/7/13
 */
public class MqttBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("com.chongctech.device.link.server.bootstrap.MqttServerBootstrap")) {
            registry.registerBeanDefinition("com.chongctech.device.link.server.bootstrap.MqttServerBootstrap",
                    new AnnotatedGenericBeanDefinition(MqttServerBootstrap.class));
        }

    }
}
