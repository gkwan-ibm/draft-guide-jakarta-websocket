package io.openliberty.guides.system.scheduler;
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


import java.io.IOException;
import java.net.URI;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

@ClientEndpoint()
public class SystemClient {

    private Session session;

    public SystemClient(URI endpoint) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpoint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.print("Scheduler connected to the server.");
    }

    @OnMessage
    public void onMessage(String message, Session userSession) throws Exception {
        // do nothing
    }

    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("Scheduler closed the session.");
    }

}
