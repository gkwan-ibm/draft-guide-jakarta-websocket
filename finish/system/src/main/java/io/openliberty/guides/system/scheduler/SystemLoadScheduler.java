// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.system.scheduler;

import java.net.URI;
import java.util.Random;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;

@Singleton
public class SystemLoadScheduler {

    private SystemClient client;
    final static private String[] messages = new String[] {
        "loadAverage", "memoryUsage", "both" };

    @PostConstruct
    public void init() {
        try {
            client = new SystemClient(new URI("ws://localhost:9081/systemLoad"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void sendSystemLoad() {
        Random r = new Random();
        client.sendMessage(messages[r.nextInt(3)]);
    }

    @PreDestroy
    public void close() {
        client.close();
    }
}
