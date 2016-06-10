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
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * @author ahmad
 */
public final class StyledLabel extends Label {

    private final IntegerProperty fontSize;
    private final ObjectProperty<Color> textFillColor;
    private final ObjectProperty<Color> bgColor;

    private final ReadOnlyStringWrapper textFillColorString = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper bgColorString = new ReadOnlyStringWrapper();

    public StyledLabel() {
        this("", 14, Color.BLACK, Color.TRANSPARENT, Pos.CENTER);
    }

    public StyledLabel(String text) {
        this(text, 14, Color.BLACK, Color.TRANSPARENT, Pos.CENTER);
    }

    public StyledLabel(String text, int fontSize) {
        this(text, fontSize, Color.BLACK, Color.TRANSPARENT, Pos.CENTER);
    }

    public StyledLabel(String text, Pos alignment) {
        this(text, 14, Color.BLACK, Color.TRANSPARENT, alignment);
    }

    public StyledLabel(String text, Color textFillColor, Color bgColor) {
        this(text, 14, textFillColor, bgColor, Pos.CENTER);
    }

    public StyledLabel(String text, int fontSize, Pos alignment) {
        this(text, fontSize, Color.BLACK, Color.TRANSPARENT, alignment);
    }

    public StyledLabel(String text, int fontSize, Color textFillColor, Color bgColor) {
        this(text, fontSize, textFillColor, bgColor, Pos.CENTER);
    }

    public StyledLabel(String text, Color textFillColor, Color bgColor, Pos alignment) {
        this(text, 14, textFillColor, bgColor, alignment);
    }

    public StyledLabel(String text, int fontSize, Color textFillColor, Color bgColor, Pos alignment) {
        super(text);
        this.fontSize = new SimpleIntegerProperty(fontSize);
        this.textFillColor = new SimpleObjectProperty<>(textFillColor);
        this.bgColor = new SimpleObjectProperty<>(bgColor);
        setAlignment(alignment);
        init();
    }

    private void init() {
        textFillColorString.set(Colors.webString(textFillColor.get()));
        bgColorString.set(Colors.webString(bgColor.get()));

        textFillColor.addListener((observable, oldValue, newValue) -> {
            textFillColorString.set(Colors.webString(newValue));
        });
        bgColor.addListener((observable, oldValue, newValue) -> {
            bgColorString.set(Colors.webString(newValue));
        });

        styleProperty().bind(
                new SimpleStringProperty("-fx-font-size: ").concat(fontSize)
                        .concat("px; -fx-text-fill: ").concat(textFillColorString)
                        .concat("; -fx-background-color : ").concat(bgColorString)
                        .concat(";")
        );
    }

    public int getFontSize() {
        return fontSize.get();
    }

    public IntegerProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize.set(fontSize);
    }

    public Color getTextFillColor() {
        return textFillColor.get();
    }

    public ObjectProperty<Color> textFillColorProperty() {
        return textFillColor;
    }

    public void setTextFillColor(Color textFillColor) {
        this.textFillColor.set(textFillColor);
    }

    public Color getBgColor() {
        return bgColor.get();
    }

    public ObjectProperty<Color> bgColorProperty() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor.set(bgColor);
    }

}