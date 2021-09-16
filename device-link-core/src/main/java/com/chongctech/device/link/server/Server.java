/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.chongctech.device.link.server;

import com.chongctech.device.link.server.netty.NettyAcceptor;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Launch a  configured version of the server.
 *
 * @author andrea
 */
@Component
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    @Autowired
    private NettyAcceptor acceptor;

    private volatile boolean started = false;

    public boolean isStarted() {
        return started;
    }

    public void startServer() {
        acceptor.initialize();
        started = true;
    }

    public void stopServer() {
        if (started) {
            logger.info("Server stopping...");
            acceptor.close();
            started = false;
            logger.info("Server stopped");
        }
    }
}
