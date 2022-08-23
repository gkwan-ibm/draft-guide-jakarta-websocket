package it.io.openliberty.guides.system;
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


import java.net.URI;

import io.openliberty.guides.system.SystemLoadDecoder;
import jakarta.json.JsonObject;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

// tag::clientEndpoint[]
@ClientEndpoint()
// end::clientEndpoint[]
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

    // tag::onOpen[]
    @OnOpen
    // end::onOpen[]
    public void onOpen(Session session) {
        this.session = session;
    }

    // tag::onMessage[]
    @OnMessage
    // tag::onOpen[]
    public void onMessage(String message, Session userSession) throws Exception {
        SystemLoadDecoder decoder = new SystemLoadDecoder();
        JsonObject systemLoad = decoder.decode(message);
        SystemServiceIT.verify(systemLoad);
    }

    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public void close() throws Exception {
        session.close();
    }

}
