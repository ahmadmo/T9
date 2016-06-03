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

package com.t9.view.controls;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.t9.util.fx.Colors;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

public class MDButton extends Button {

    private final ObjectProperty<Color> primaryBgColor = new SimpleObjectProperty<>(Color.web("#f1f1f1"));
    private final ObjectProperty<Color> secondaryBgColor = new SimpleObjectProperty<>(Color.web("#d5d5d5"));
    private final ReadOnlyObjectWrapper<Color> currentBgColor = new ReadOnlyObjectWrapper<>(primaryBgColor.get());
    private final ReadOnlyStringWrapper currentBgColorString = new ReadOnlyStringWrapper();

    private final Circle circleRipple = new Circle(0.1, new Color(0, 0, 0, 0.25));

    private double lastRippleHeight = 0;
    private double lastRippleWidth = 0;

    public MDButton() {
        super();
        init();
    }

    public MDButton(String text) {
        super(text);
        init();
    }

    public MDButton(String text, Node graphic) {
        super(text, graphic);
        init();
    }

    private void init() {
        getStyleClass().add("md-button");
        setupRippleEffect();
        setupHoverEffect();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        final ButtonSkin buttonSkin = new ButtonSkin(this);
        getChildren().add(0, circleRipple);
        return buttonSkin;
    }

    private void setupRippleEffect() {
        circleRipple.setOpacity(0);
        circleRipple.setEffect(new BoxBlur(3, 3, 2));
        final Rectangle rippleClip = new Rectangle();
        final Duration rippleDuration = Duration.millis(500);
        final AtomicBoolean pressed = new AtomicBoolean(),
                play = new AtomicBoolean(),
                played = new AtomicBoolean(),
                ignore = new AtomicBoolean();
        final Timeline scaleRippleTimeline = new Timeline();
        final FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), circleRipple);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        scaleRippleTimeline.setOnFinished(e -> {
            if (ignore.getAndSet(false) && played.compareAndSet(false, true)) {
                fadeTransition.playFromStart();
            } else {
                play.set(true);
            }
        });
        fadeTransition.setOnFinished(e -> {
            circleRipple.setOpacity(0);
            circleRipple.setRadius(0.1);
        });
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            pressed.set(true);
            play.set(false);
            played.set(false);
            ignore.set(false);
            scaleRippleTimeline.stop();
            fadeTransition.stop();
            circleRipple.setOpacity(1);
            circleRipple.setRadius(0.1);
            circleRipple.setCenterX(e.getX());
            circleRipple.setCenterY(e.getY());
            if (getWidth() != lastRippleWidth || getHeight() != lastRippleHeight) {
                lastRippleWidth = getWidth();
                lastRippleHeight = getHeight();
                rippleClip.setWidth(lastRippleWidth);
                rippleClip.setHeight(lastRippleHeight);
                try {
                    rippleClip.setArcHeight(getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius());
                    rippleClip.setArcWidth(getBackground().getFills().get(0).getRadii().getTopRightHorizontalRadius());
                    circleRipple.setClip(rippleClip);
                } catch (Exception ignored) {
                }
                final KeyValue keyValue = new KeyValue(circleRipple.radiusProperty(), Math.max(getHeight(), getWidth()), Interpolator.EASE_OUT);
                final KeyFrame keyFrame = new KeyFrame(rippleDuration, keyValue);
                scaleRippleTimeline.getKeyFrames().clear();
                scaleRippleTimeline.getKeyFrames().add(keyFrame);
            }
            scaleRippleTimeline.playFromStart();
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (play.compareAndSet(true, false) && played.compareAndSet(false, true)) {
                fadeTransition.playFromStart();
            } else {
                ignore.set(true);
            }
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (pressed.compareAndSet(true, false)) {
                if (play.compareAndSet(true, false) && played.compareAndSet(false, true)) {
                    fadeTransition.playFromStart();
                } else {
                    ignore.set(true);
                }
            }
        });
    }

    private void setupHoverEffect() {
        final DoubleProperty shadow = new SimpleDoubleProperty(8.0), shadowDepth = new SimpleDoubleProperty(4.0);
        final Duration d1 = Duration.millis(150), d2 = Duration.millis(100);
        final Timeline t1 = new Timeline(
                new KeyFrame(d1, new KeyValue(currentBgColor, secondaryBgColor.get(), Interpolator.EASE_IN)),
                new KeyFrame(d1, new KeyValue(shadow, 10.0, Interpolator.EASE_IN)),
                new KeyFrame(d1, new KeyValue(shadowDepth, 6.0, Interpolator.EASE_IN))
        );
        final Timeline t2 = new Timeline(
                new KeyFrame(d2, new KeyValue(currentBgColor, primaryBgColor.get(), Interpolator.EASE_OUT)),
                new KeyFrame(d2, new KeyValue(shadow, 8.0, Interpolator.EASE_OUT)),
                new KeyFrame(d2, new KeyValue(shadowDepth, 4.0, Interpolator.EASE_OUT))
        );
        final Timeline t3 = new Timeline(
                new KeyFrame(d1, new KeyValue(shadow, 20.0, Interpolator.EASE_IN)),
                new KeyFrame(d1, new KeyValue(shadowDepth, 10.0, Interpolator.EASE_IN))
        );
        final Timeline t4 = new Timeline(
                new KeyFrame(d2, new KeyValue(shadow, 10.0, Interpolator.EASE_OUT)),
                new KeyFrame(d2, new KeyValue(shadowDepth, 6.0, Interpolator.EASE_OUT))
        );

        currentBgColorString.set(Colors.webString(currentBgColor.get()));
        primaryBgColor.addListener((observable, oldValue, newValue) -> {
            currentBgColor.set(newValue);
            t2.getKeyFrames().set(0, new KeyFrame(d2, new KeyValue(currentBgColor, newValue, Interpolator.EASE_OUT)));
        });
        secondaryBgColor.addListener((observable, oldValue, newValue) -> {
            t1.getKeyFrames().set(0, new KeyFrame(d1, new KeyValue(currentBgColor, newValue, Interpolator.EASE_IN)));
        });
        currentBgColor.addListener((observable, oldValue, newValue) -> {
            currentBgColorString.set(Colors.webString(newValue));
        });
        styleProperty().bind(
                new SimpleStringProperty("-fx-background-color: ").concat(currentBgColorString)
                        .concat("; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), ").concat(shadow)
                        .concat(", 0.2, 0, ").concat(shadowDepth).concat(");")
        );

        final AtomicBoolean play = new AtomicBoolean();
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            t2.stop();
            t1.playFromStart();
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            play.set(false);
            t1.stop();
            t2.playFromStart();
        });
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            play.set(true);
            t4.stop();
            t3.playFromStart();
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (play.compareAndSet(true, false)) {
                t3.stop();
                t4.playFromStart();
            }
        });
    }

    public Color getPrimaryBgColor() {
        return primaryBgColor.get();
    }

    public ObjectProperty<Color> primaryBgColorProperty() {
        return primaryBgColor;
    }

    public void setPrimaryBgColor(Color primaryBgColor) {
        this.primaryBgColor.set(primaryBgColor);
    }

    public Color getSecondaryBgColor() {
        return secondaryBgColor.get();
    }

    public ObjectProperty<Color> secondaryBgColorProperty() {
        return secondaryBgColor;
    }

    public void setSecondaryBgColor(Color secondaryBgColor) {
        this.secondaryBgColor.set(secondaryBgColor);
    }

    public Color getCurrentBgColor() {
        return currentBgColor.get();
    }

    public ReadOnlyObjectProperty<Color> currentBgColorProperty() {
        return currentBgColor.getReadOnlyProperty();
    }

    public void setRippleColor(Color color) {
        circleRipple.setFill(color);
    }

}
