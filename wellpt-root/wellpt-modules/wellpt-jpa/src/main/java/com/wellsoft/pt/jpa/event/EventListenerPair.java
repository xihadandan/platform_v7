/*
 * @(#)2020年7月2日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.event;

import org.springframework.context.ApplicationEvent;

public class EventListenerPair {

    private WellptTransactionalEventListener<?> listener;
    private ApplicationEvent event;
    private boolean needExecute = true;

    /**
     * @param event
     * @param listener
     */
    public EventListenerPair(WellptTransactionalEventListener<?> listener, ApplicationEvent event) {
        this.listener = listener;
        this.event = event;
    }

    /**
     * @param listener
     * @param event
     * @param needExecute
     */
    public EventListenerPair(WellptTransactionalEventListener<?> listener, ApplicationEvent event, boolean needExecute) {
        this.listener = listener;
        this.event = event;
        this.needExecute = needExecute;
    }

    public WellptTransactionalEventListener<?> getListener() {
        return listener;
    }

    public ApplicationEvent getEvent() {
        return event;
    }

    public boolean isNeedExecute() {
        return needExecute;
    }

    public boolean markIgnoreExecute() {
        return this.needExecute = false;
    }

}