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

import com.t9.util.fx.Colors;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public final class Symbols extends ScrollPane {

    private static final Map<Character, String> CHAR_MAP = new LinkedHashMap<>();
    private static final int MIN_FONT_SIZE = 26;

    static {
        CHAR_MAP.put('.', ".");
        CHAR_MAP.put(',', ",");
        CHAR_MAP.put('\'', "'");
        CHAR_MAP.put('?', "?");
        CHAR_MAP.put('!', "!");
        CHAR_MAP.put('"', "\"");
        CHAR_MAP.put('-', "-");
        CHAR_MAP.put('(', "(");
        CHAR_MAP.put(')', ")");
        CHAR_MAP.put('@', "@");
        CHAR_MAP.put('/', "/");
        CHAR_MAP.put(':', ":");
        CHAR_MAP.put('_', "_");
        CHAR_MAP.put(';', ";");
        CHAR_MAP.put('+', "+");
        CHAR_MAP.put('&', "&");
        CHAR_MAP.put('%', "%");
        CHAR_MAP.put('*', "*");
        CHAR_MAP.put('=', "=");
        CHAR_MAP.put('<', "<");
        CHAR_MAP.put('>', ">");
        CHAR_MAP.put('£', "£");
        CHAR_MAP.put('€', "€");
        CHAR_MAP.put('$', "$");
        CHAR_MAP.put('¥', "¥");
        CHAR_MAP.put('¤', "¤");
        CHAR_MAP.put('[', "[");
        CHAR_MAP.put(']', "]");
        CHAR_MAP.put('{', "{");
        CHAR_MAP.put('}', "}");
        CHAR_MAP.put('\\', "\\");
        CHAR_MAP.put('~', "~");
        CHAR_MAP.put('^', "^");
        CHAR_MAP.put('¡', "¡");
        CHAR_MAP.put('¿', "¿");
        CHAR_MAP.put('§', "§");
        CHAR_MAP.put('#', "#");
        CHAR_MAP.put('|', "|");
        CHAR_MAP.put('˄', "˄");
        CHAR_MAP.put('˅', "˅");
        CHAR_MAP.put('\n', "↵"); // ⤶ or ↲ or ↩
    }

    private final TilePane tilePane;
    private final ObjectProperty<Color> primaryTextFillColor;
    private final ObjectProperty<Color> secondaryTextFillColor;
    private final ObjectProperty<Color> primaryBgColor;
    private final ObjectProperty<Color> secondaryBgColor;
    private final List<StyledLabel> labels;

    private final ReadOnlyStringWrapper primaryTextFillColorString = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper secondaryTextFillColorString = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper primaryBgColorString = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper secondaryBgColorString = new ReadOnlyStringWrapper();
    private final ReadOnlyDoubleWrapper perimeter = new ReadOnlyDoubleWrapper();

    public Symbols() {
        this(Orientation.HORIZONTAL, 0, 0, Color.BLACK, Color.BLACK, Color.WHITE, Color.GRAY);
    }

    public Symbols(Orientation orientation) {
        this(orientation, 0, 0, Color.BLACK, Color.BLACK, Color.WHITE, Color.GRAY);
    }

    public Symbols(double hgap, double vgap) {
        this(Orientation.HORIZONTAL, hgap, vgap, Color.BLACK, Color.BLACK, Color.WHITE, Color.GRAY);
    }

    public Symbols(Color primaryTextFillColor, Color primaryBgColor,
                   Color secondaryBgColor, Color secondaryTextFillColor) {
        this(Orientation.HORIZONTAL, 0, 0, primaryTextFillColor, secondaryTextFillColor, primaryBgColor, secondaryBgColor);
    }

    public Symbols(Orientation orientation,
                   Color primaryTextFillColor, Color primaryBgColor,
                   Color secondaryBgColor, Color secondaryTextFillColor) {
        this(orientation, 0, 0, primaryTextFillColor, secondaryTextFillColor, primaryBgColor, secondaryBgColor);
    }

    public Symbols(double hgap, double vgap,
                   Color primaryTextFillColor, Color primaryBgColor,
                   Color secondaryBgColor, Color secondaryTextFillColor) {
        this(Orientation.HORIZONTAL, hgap, vgap, primaryTextFillColor, secondaryTextFillColor, primaryBgColor, secondaryBgColor);
    }

    public Symbols(Orientation orientation, double hgap, double vgap,
                   Color primaryTextFillColor, Color secondaryTextFillColor,
                   Color primaryBgColor, Color secondaryBgColor) {
        tilePane = new TilePane(orientation, hgap, vgap);
        this.primaryTextFillColor = new SimpleObjectProperty<>(primaryTextFillColor);
        this.secondaryTextFillColor = new SimpleObjectProperty<>(secondaryTextFillColor);
        this.primaryBgColor = new SimpleObjectProperty<>(primaryBgColor);
        this.secondaryBgColor = new SimpleObjectProperty<>(secondaryBgColor);
        labels = Collections.unmodifiableList(
                CHAR_MAP.entrySet().stream()
                        .map(this::createLabel)
                        .collect(Collectors.toList())
        );
        init();
    }

    private void init() {
        tilePane.getChildren().addAll(labels);
        tilePane.setPadding(new Insets(8, 8, 8, 8));

        primaryTextFillColorString.set(Colors.webString(primaryTextFillColor.get()));
        secondaryTextFillColorString.set(Colors.webString(secondaryTextFillColor.get()));

        primaryTextFillColor.addListener((observable, oldValue, newValue) -> {
            primaryTextFillColorString.set(Colors.webString(newValue));
        });
        secondaryTextFillColor.addListener((observable, oldValue, newValue) -> {
            secondaryTextFillColorString.set(Colors.webString(newValue));
        });

        primaryBgColorString.set(Colors.webString(primaryBgColor.get()));
        secondaryBgColorString.set(Colors.webString(secondaryBgColor.get()));

        primaryBgColor.addListener((observable, oldValue, newValue) -> {
            primaryBgColorString.set(Colors.webString(newValue));
        });
        secondaryBgColor.addListener((observable, oldValue, newValue) -> {
            secondaryBgColorString.set(Colors.webString(newValue));
        });

        tilePane.styleProperty().bind(
                new SimpleStringProperty("-fx-background-color: ").concat(primaryBgColorString).concat(";")
        );

        perimeter.bind(widthProperty().add(heightProperty()));
        perimeter.addListener((observable, oldValue, newValue) -> {
            int size = Math.max(MIN_FONT_SIZE, (int) Math.round(newValue.doubleValue() / CHAR_MAP.size()));
            for (StyledLabel label : labels) {
                label.setFontSize(size);
            }
        });

        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setFitToHeight(true);
        setFitToWidth(true);
        setContent(tilePane);

        for (StyledLabel label : labels) {
            setupHoverEffect(label);
            setupClickEffect(label);
        }
    }

    private StyledLabel createLabel(Map.Entry<Character, String> entry) {
        final StyledLabel label = new StyledLabel(entry.getValue(), Pos.CENTER);
        label.getStyleClass().add("symbol-label");
        label.textFillColorProperty().bind(primaryTextFillColor);
        label.setUserData(entry.getKey());
        label.prefWidthProperty().bind(label.fontSizeProperty().multiply(1.8));
        label.prefHeightProperty().bind(label.widthProperty());
        return label;
    }

    private void setupHoverEffect(StyledLabel label) {
        final ReadOnlyObjectWrapper<Color> currentTextFillColor = new ReadOnlyObjectWrapper<>(primaryTextFillColor.get());
        final ReadOnlyObjectWrapper<Color> currentBgColor = new ReadOnlyObjectWrapper<>(primaryBgColor.get());

        label.textFillColorProperty().bind(currentTextFillColor);
        label.bgColorProperty().bind(currentBgColor);

        final Duration d1 = Duration.millis(150), d2 = Duration.millis(100);
        final Timeline t1 = new Timeline(
                new KeyFrame(d1, new KeyValue(currentTextFillColor, secondaryTextFillColor.get(), Interpolator.EASE_IN)),
                new KeyFrame(d1, new KeyValue(currentBgColor, secondaryBgColor.get(), Interpolator.EASE_IN))
        );
        final Timeline t2 = new Timeline(
                new KeyFrame(d2, new KeyValue(currentTextFillColor, primaryTextFillColor.get(), Interpolator.EASE_OUT)),
                new KeyFrame(d2, new KeyValue(currentBgColor, primaryBgColor.get(), Interpolator.EASE_OUT))
        );

        primaryTextFillColor.addListener((observable, oldValue, newValue) -> {
            currentTextFillColor.set(newValue);
            t2.getKeyFrames().set(0, new KeyFrame(d2, new KeyValue(currentTextFillColor, newValue, Interpolator.EASE_OUT)));
        });
        secondaryTextFillColor.addListener((observable, oldValue, newValue) -> {
            t1.getKeyFrames().set(0, new KeyFrame(d1, new KeyValue(currentTextFillColor, newValue, Interpolator.EASE_IN)));
        });

        primaryBgColor.addListener((observable, oldValue, newValue) -> {
            currentBgColor.set(newValue);
            t2.getKeyFrames().set(1, new KeyFrame(d2, new KeyValue(currentBgColor, newValue, Interpolator.EASE_OUT)));
        });
        secondaryBgColor.addListener((observable, oldValue, newValue) -> {
            t1.getKeyFrames().set(1, new KeyFrame(d1, new KeyValue(currentBgColor, newValue, Interpolator.EASE_IN)));
        });

        label.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            t2.stop();
            t1.playFromStart();
        });
        label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            t1.stop();
            t2.playFromStart();
        });
    }

    private void setupClickEffect(StyledLabel label) {
        final Duration d = Duration.millis(20);
        final TranslateTransition t1 = new TranslateTransition(d, label);
        final TranslateTransition t2 = new TranslateTransition(d, label);
        final AtomicBoolean play = new AtomicBoolean();

        t1.setByY(3);
        t2.setByY(-3);

        label.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            play.set(true);
            t2.stop();
            t1.playFromStart();
        });
        label.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (play.compareAndSet(true, false)) {
                t1.stop();
                t2.playFromStart();
            }
        });
        label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (play.compareAndSet(true, false)) {
                t1.stop();
                t2.playFromStart();
            }
        });
    }

    public Color getPrimaryTextFillColor() {
        return primaryTextFillColor.get();
    }

    public ObjectProperty<Color> primaryTextFillColorProperty() {
        return primaryTextFillColor;
    }

    public void setPrimaryTextFillColor(Color primaryTextFillColor) {
        this.primaryTextFillColor.set(primaryTextFillColor);
    }

    public Color getSecondaryTextFillColor() {
        return secondaryTextFillColor.get();
    }

    public ObjectProperty<Color> secondaryTextFillColorProperty() {
        return secondaryTextFillColor;
    }

    public void setSecondaryTextFillColor(Color secondaryTextFillColor) {
        this.secondaryTextFillColor.set(secondaryTextFillColor);
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

    public List<StyledLabel> getLabels() {
        return labels;
    }

}
