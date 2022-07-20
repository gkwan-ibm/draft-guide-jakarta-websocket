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
package io.openliberty.guides.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/systemLoad",
                decoders = {SystemLoadDecoder.class},
                encoders = {SystemLoadEncoder.class})
public class SystemService {

    private static Set<Session> sessions = new HashSet<>();

    private static final OperatingSystemMXBean osBean =
        ManagementFactory.getOperatingSystemMXBean();

    private static final MemoryMXBean memBean =
        ManagementFactory.getMemoryMXBean();

    public static void sendToAllSessions(JsonObject systemLoad) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendObject(systemLoad);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened for session: " + session.getId());
        sessions.add(session);
    }
    
    @OnMessage
    public void onMessage(String option, Session session) {
        System.out.println("received message \"" + option + "\" " +
                           "from session: " + session.getId());
        try {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("time", Calendar.getInstance().getTime().toString());
            if (option.equalsIgnoreCase("loadAverage") ||
                option.equalsIgnoreCase("both")) {
                builder.add("loadAverage", Double.valueOf(osBean.getSystemLoadAverage()));
            }
            if (option.equalsIgnoreCase("memoryUsage") ||
                option.equalsIgnoreCase("both")) {
                long heapMax = memBean.getHeapMemoryUsage().getMax();
                long heapUsed = memBean.getHeapMemoryUsage().getUsed();
                builder.add("memoryUsage", Double.valueOf(heapUsed * 100.0 / heapMax ));
            }
            JsonObject systemLoad = builder.build();
            sendToAllSessions(systemLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error for " + session.getId() + " " +
                           throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed for " + session.getId() +
                           " with reason " + closeReason.getCloseCode());
        sessions.remove(session);
    }

}
