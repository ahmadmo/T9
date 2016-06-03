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

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.VBox;

/**
 * @author ahmad
 */
public final class KeyPadButton extends MDButton {

    private final SimpleObjectProperty<StyledLabel> primaryLabel;
    private final SimpleObjectProperty<StyledLabel> secondaryLabel;

    public KeyPadButton() {
        this(null, null, 5);
    }

    public KeyPadButton(StyledLabel primaryLabel, StyledLabel secondary) {
        this(primaryLabel, secondary, 5);
    }

    public KeyPadButton(StyledLabel primaryLabel, StyledLabel secondaryLabel, int spacing) {
        this.primaryLabel = new SimpleObjectProperty<>(primaryLabel);
        this.secondaryLabel = new SimpleObjectProperty<>(secondaryLabel);
        init(spacing);
    }

    private void init(int spacing) {
        VBox vBox = new VBox(spacing);
        vBox.getChildren().addAll(primaryLabel.get(), secondaryLabel.get());
        vBox.setAlignment(Pos.CENTER);
        primaryLabel.get().prefWidthProperty().bind(vBox.widthProperty());
        secondaryLabel.get().prefWidthProperty().bind(vBox.widthProperty());
        setGraphic(vBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    public StyledLabel getPrimaryLabel() {
        return primaryLabel.get();
    }

    public SimpleObjectProperty<StyledLabel> primaryLabelProperty() {
        return primaryLabel;
    }

    public void setPrimaryLabel(StyledLabel primaryLabel) {
        this.primaryLabel.set(primaryLabel);
    }

    public StyledLabel getSecondaryLabel() {
        return secondaryLabel.get();
    }

    public SimpleObjectProperty<StyledLabel> secondaryLabelProperty() {
        return secondaryLabel;
    }

    public void setSecondaryLabel(StyledLabel secondaryLabel) {
        this.secondaryLabel.set(secondaryLabel);
    }

}
