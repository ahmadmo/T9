/*
 * Copyright 2016 Ahmad Mozafarnia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t9.util.fx;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author ahmad
 */
public final class MouseHandler implements EventHandler<MouseEvent> {

    private final EventHandler<MouseEvent> primaryHandler;
    private final EventHandler<MouseEvent> secondaryHandler;

    private MouseHandler(EventHandler<MouseEvent> primaryHandler, EventHandler<MouseEvent> secondaryHandler) {
        this.primaryHandler = primaryHandler;
        this.secondaryHandler = secondaryHandler;
    }

    @Override
    public void handle(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY:
                if (primaryHandler != null) {
                    primaryHandler.handle(event);
                }
                break;
            case SECONDARY:
                if (secondaryHandler != null) {
                    secondaryHandler.handle(event);
                }
                break;
        }
    }

    public static void addPrimaryHandler(Node node,
                                         EventType<MouseEvent> eventType,
                                         EventHandler<MouseEvent> handler) {
        addHandler(node, eventType, handler, null);
    }

    public static void addSecondaryHandler(Node node,
                                           EventType<MouseEvent> eventType,
                                           EventHandler<MouseEvent> handler) {
        addHandler(node, eventType, null, handler);
    }

    public static void addHandler(Node node,
                                  EventType<MouseEvent> eventType,
                                  EventHandler<MouseEvent> primaryHandler,
                                  EventHandler<MouseEvent> secondaryHandler) {
        node.addEventHandler(eventType, new MouseHandler(primaryHandler, secondaryHandler));
    }

    public static void addPrimaryPressAndHoldHandler(Node node,
                                                     Duration holdTime,
                                                     EventHandler<MouseEvent> handler) {
        addPressAndHoldHandler(node, holdTime, handler, null);
    }

    public static void addSecondaryPressAndHoldHandler(Node node,
                                                       Duration holdTime,
                                                       EventHandler<MouseEvent> handler) {
        addPressAndHoldHandler(node, holdTime, null, handler);
    }

    public static void addPressAndHoldHandler(Node node,
                                              Duration holdTime,
                                              EventHandler<MouseEvent> primaryHandler,
                                              EventHandler<MouseEvent> secondaryHandler) {
        class Wrapper<T> {
            private T content;
        }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);
        MouseHandler handler = new MouseHandler(primaryHandler, secondaryHandler);
        holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

}
