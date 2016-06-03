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

package com.t9.view;

import com.t9.util.concurrent.CyclicList;
import com.t9.util.concurrent.Switch;
import com.t9.util.fx.ColorScheme;
import com.t9.util.fx.Colors;
import com.t9.util.fx.MouseHandler;
import com.t9.view.controls.KeyPadButton;
import com.t9.view.controls.StyledLabel;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author ahmad
 */
public final class T9Controller {

    private final T9Layout layout;
    private final Switch<ColorScheme> colorSchemeSwitch;

    public T9Controller(T9Layout layout, Switch<ColorScheme> colorSchemeSwitch) {
        this.layout = layout;
        this.colorSchemeSwitch = colorSchemeSwitch;
        init();
    }

    private void init() {
        // set current color scheme
        setColorScheme(colorSchemeSwitch.get(), false);

        // setting up plus button
        final Transition fadeIn = fadeInSymbols();
        final Transition fadeOut = fadeOutSymbols();
        final Switch<Runnable> plusButtonAction = new Switch<>();
        plusButtonAction.setLeft(() -> {
            for (StyledLabel label : layout.getSymbols().getLabels()) {
                label.setOpacity(0);
            }
            fadeOut.stop();
            if (layout.getTextAreaPane().getChildren().size() == 1) {
                layout.getTextAreaPane().getChildren().add(layout.getSymbols());
            }
            fadeIn.playFromStart();
        });
        plusButtonAction.setRight(() -> {
            fadeIn.stop();
            fadeOut.playFromStart();
        });
        MouseHandler.addPrimaryHandler(layout.getPlusButton(), MouseEvent.MOUSE_CLICKED, event -> plusButtonAction.getAndSwitch().run());

        // adding symbols handlers
        for (final StyledLabel label : layout.getSymbols().getLabels()) {
            MouseHandler.addPrimaryHandler(label, MouseEvent.MOUSE_CLICKED, event -> {
                if (plusButtonAction.toLeft()) {
                    plusButtonAction.getRight().run();
                    System.out.println(label.getUserData() + " --> clicked");
                }
            });
        }

        // setting up shift button
        final CyclicList<Runnable> shiftButtonState = new CyclicList<>();
        final Switch<Function<String, String>> upperLowerCaseSwitch = new Switch<>(String::toUpperCase, String::toLowerCase);
        shiftButtonState.getData().add(() -> switchCase(upperLowerCaseSwitch.getAndSwitch()));
        shiftButtonState.getData().add(() -> setButtonColor(colorSchemeSwitch.get(), layout.getShiftButton(), true));
        shiftButtonState.getData().add(() -> {
            setButtonColor(colorSchemeSwitch.get(), layout.getShiftButton(), false);
            switchCase(upperLowerCaseSwitch.getAndSwitch());
        });
        MouseHandler.addPrimaryHandler(layout.getShiftButton(), MouseEvent.MOUSE_CLICKED, event -> shiftButtonState.getAndNext().run());

        // adding press and hold handlers
        for (final KeyPadButton button : layout.getButtons()) {
            MouseHandler.addPrimaryPressAndHoldHandler(
                    button,
                    Duration.seconds(1),
                    event -> System.out.println(button.getPrimaryLabel().getText() + " --> on hold!")
            );
        }

        // setting up menu items
        layout.getFontSizeMenuItems().forEach((size, item) -> item.setOnAction(event -> layout.getTextArea().setFontSize(size)));

        final AtomicBoolean lightColorSchemeSelected = new AtomicBoolean(layout.getLightThemeMenuItem().isSelected());
        final AtomicBoolean darkColorSchemeSelected = new AtomicBoolean(layout.getDarkThemeMenuItem().isSelected());
        layout.getLightThemeMenuItem().setOnAction(event -> {
            if (lightColorSchemeSelected.compareAndSet(false, true) && darkColorSchemeSelected.getAndSet(false)) {
                setColorScheme(colorSchemeSwitch.switchAndGet(), shiftButtonState.getIndex() == 2);
            }
        });
        layout.getDarkThemeMenuItem().setOnAction(event -> {
            if (darkColorSchemeSelected.compareAndSet(false, true) && lightColorSchemeSelected.getAndSet(false)) {
                setColorScheme(colorSchemeSwitch.switchAndGet(), shiftButtonState.getIndex() == 2);
            }
        });
    }

    private void switchCase(Function<String, String> function) {
        for (KeyPadButton button : layout.getButtons()) {
            char ch = button.getPrimaryLabel().getText().charAt(0);
            if (ch >= '2' && ch <= '9') {
                Label label = button.getSecondaryLabel();
                label.setText(function.apply(label.getText()));
            }
        }
    }

    private void setColorScheme(final ColorScheme colorScheme, final boolean shiftButtonEnabled) {
        layout.getRoot().setStyle("-fx-background-color: " + Colors.webString(colorScheme.get("root-bg")) + ";");
        layout.getTextArea().setTextFillColor(colorScheme.get("text-area-text-fill"));
        layout.getTextArea().setBgColor(colorScheme.get("text-area-bg"));
        layout.getSymbols().setSecondaryTextFillColor(colorScheme.get("symbols-secondary-text-fill"));
        layout.getSymbols().setSecondaryBgColor(colorScheme.get("symbols-secondary-bg"));
        layout.getButtons().stream()
                .filter(button -> button != layout.getShiftButton() || !shiftButtonEnabled)
                .forEach(button -> setButtonColor(colorScheme, button, false));
        if (shiftButtonEnabled) {
            setButtonColor(colorScheme, layout.getShiftButton(), true);
        }
    }

    private void setButtonColor(ColorScheme colorScheme, KeyPadButton button, boolean shiftButton) {
        String name = shiftButton ? "shift-button" : "button";
        button.setPrimaryBgColor(colorScheme.get(name + "-primary-bg"));
        button.setSecondaryBgColor(colorScheme.get(name + "-secondary-bg"));
        button.getPrimaryLabel().setTextFillColor(colorScheme.get(name + "-primary-text-fill"));
        button.getSecondaryLabel().setTextFillColor(colorScheme.get(name + "-secondary-text-fill"));
        button.setRippleColor(colorScheme.get(name + "-ripple-color"));
    }

    private Transition fadeInSymbols() {
        ParallelTransition parallelTransition = new ParallelTransition();
        double ms = 250;
        double df = 1.0 / layout.getSymbols().getLabels().size();
        double f = 0;
        for (StyledLabel label : layout.getSymbols().getLabels()) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(ms + ms * f), label);
            fadeTransition.setInterpolator(Interpolator.EASE_IN);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            parallelTransition.getChildren().add(fadeTransition);
            f += df;
        }
        return parallelTransition;
    }

    private Transition fadeOutSymbols() {
        ParallelTransition parallelTransition = new ParallelTransition();
        double ms = 75;
        double df = 1.0 / layout.getSymbols().getLabels().size();
        double f = 0;
        for (StyledLabel label : layout.getSymbols().getLabels()) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(ms + ms * f), label);
            fadeTransition.setInterpolator(Interpolator.EASE_OUT);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            parallelTransition.getChildren().add(fadeTransition);
            f += df;
        }
        parallelTransition.setOnFinished(event -> layout.getTextAreaPane().getChildren().remove(1));
        return parallelTransition;
    }

}
