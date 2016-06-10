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

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ahmad
 */
public final class Clickable {

    private Clickable() {
    }

    public static void install(Node node) {
        final Duration d = Duration.millis(20);
        final TranslateTransition t1 = new TranslateTransition(d, node);
        final TranslateTransition t2 = new TranslateTransition(d, node);
        final AtomicBoolean play = new AtomicBoolean();

        t1.setByY(3);
        t2.setByY(-3);

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            play.set(true);
            t2.stop();
            t1.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (play.compareAndSet(true, false)) {
                t1.stop();
                t2.playFromStart();
            }
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (play.compareAndSet(true, false)) {
                t1.stop();
                t2.playFromStart();
            }
        });
    }

}
