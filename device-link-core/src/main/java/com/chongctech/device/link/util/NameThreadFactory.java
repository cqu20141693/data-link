package com.chongctech.device.link.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(0);
    private final String baseName;

    public NameThreadFactory(String baseName) {
        this.baseName = baseName;
    }


    @Override
    public Thread newThread(Runnable r) {
        int number = count.getAndIncrement();
        return new Thread(r, baseName + number);
    }

}
