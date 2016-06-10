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

import com.t9.util.stream.MoreCollectors;
import com.t9.view.controls.KeyPadButton;
import com.t9.view.controls.StyledLabel;
import com.t9.view.controls.StyledTextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public final class T9Layout extends BorderPane {

    private final MenuBar menuBar;
    private final Menu preferencesMenu;
    private final Menu fontSizeMenu;
    private final Map<Integer, RadioMenuItem> fontSizeMenuItems;
    private final ToggleGroup fontSizesToggleGroup;
    private final Menu themeMenu;
    private final RadioMenuItem lightThemeMenuItem;
    private final RadioMenuItem darkThemeMenuItem;
    private final ToggleGroup themesToggleGroup;
    private final StackPane textAreaPane;
    private final StyledTextArea textArea;
    private final GridPane numPad;
    private final KeyPadButton plusButton;
    private final KeyPadButton spaceButton;
    private final KeyPadButton shiftButton;
    private final List<KeyPadButton> buttons;

    public T9Layout() {
        this(18);
    }

    public T9Layout(int textAreaFontSize) {
        // menu bar
        fontSizeMenuItems = Collections.unmodifiableMap(
                Arrays.asList(9, 10, 11, 12, 13, 14, 18, 24, 26, 36, 48, 64, 72, 96)
                        .stream()
                        .collect(MoreCollectors.toLinkedMap(Function.identity(), size -> new RadioMenuItem(Integer.toString(size))))
        );
        fontSizesToggleGroup = new ToggleGroup();
        fontSizesToggleGroup.getToggles().addAll(fontSizeMenuItems.values());
        fontSizeMenu = new Menu("Font Size");
        fontSizeMenu.getItems().addAll(fontSizeMenuItems.values());

        lightThemeMenuItem = new RadioMenuItem("_Light");
        darkThemeMenuItem = new RadioMenuItem("_Dark");
        themesToggleGroup = new ToggleGroup();
        themesToggleGroup.getToggles().addAll(lightThemeMenuItem, darkThemeMenuItem);
        themeMenu = new Menu("Theme");
        themeMenu.setMnemonicParsing(true);
        themeMenu.getItems().addAll(lightThemeMenuItem, darkThemeMenuItem);

        preferencesMenu = new Menu("_Preferences");
        preferencesMenu.setMnemonicParsing(true);
        preferencesMenu.getItems().addAll(fontSizeMenu, themeMenu);
        menuBar = new MenuBar(preferencesMenu);

        // text area
        textArea = new StyledTextArea(textAreaFontSize);
        textArea.setWrapText(true);

        textAreaPane = new StackPane(textArea);

        // num pad
        numPad = new GridPane();
        numPad.setAlignment(Pos.CENTER);
        numPad.setHgap(12);
        numPad.setVgap(8);
        numPad.add(createButton("1", ".", Pos.BOTTOM_RIGHT), 0, 0);
        numPad.add(createButton("2", "abc", Pos.CENTER), 1, 0);
        numPad.add(createButton("3", "def", Pos.CENTER), 2, 0);
        numPad.add(createButton("4", "ghi", Pos.CENTER), 0, 1);
        numPad.add(createButton("5", "jkl", Pos.CENTER), 1, 1);
        numPad.add(createButton("6", "mno", Pos.CENTER), 2, 1);
        numPad.add(createButton("7", "pqrs", Pos.CENTER), 0, 2);
        numPad.add(createButton("8", "tuv", Pos.CENTER), 1, 2);
        numPad.add(createButton("9", "wxyz", Pos.CENTER), 2, 2);
        numPad.add(plusButton = createButton("*", "+", Pos.BOTTOM_RIGHT), 0, 3);
        numPad.add(spaceButton = createButton("0", "\u2423", Pos.BOTTOM_RIGHT), 1, 3);
        numPad.add(shiftButton = createButton("#", "\u2191", Pos.BOTTOM_RIGHT), 2, 3);

        buttons = Collections.unmodifiableList(
                numPad.getChildren().stream()
                        .map(node -> (KeyPadButton) node)
                        .collect(Collectors.toList())
        );

        numPad.prefHeightProperty().bind(heightProperty().divide(2));

        // resizable buttons
        for (KeyPadButton button : buttons) {
            button.prefWidthProperty().bind(numPad.widthProperty().divide(4));
            button.prefHeightProperty().bind(numPad.heightProperty().divide(4));
//            button.minWidthProperty().bind(button.heightProperty());
        }

        numPad.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 6);

        // responsive layout
        final AtomicBoolean flag = new AtomicBoolean();
        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getWidth() / newValue.getHeight() > 1.15) {
                if (flag.compareAndSet(false, true)) {
                    getChildren().removeAll(textAreaPane, numPad);
                    setRight(textAreaPane);
                    setLeft(numPad);

                    numPad.prefWidthProperty().bind(widthProperty().divide(3));
                    numPad.prefHeightProperty().bind(heightProperty());

                    for (KeyPadButton button : buttons) {
                        button.prefHeightProperty().bind(numPad.heightProperty().divide(6));
                    }

                    textAreaPane.prefWidthProperty().bind(widthProperty().divide(1.5));
                    textArea.setBorderWidth(new Insets(0, 0, 0, 2));
                }
            } else if (flag.compareAndSet(true, false)) {
                getChildren().removeAll(textAreaPane, numPad);
                setCenter(textAreaPane);
                setBottom(numPad);

                numPad.prefWidthProperty().bind(widthProperty());
                numPad.prefHeightProperty().bind(heightProperty().divide(2));

                for (KeyPadButton button : buttons) {
                    button.prefHeightProperty().bind(numPad.heightProperty().divide(4));
                }

                textAreaPane.prefWidthProperty().bind(widthProperty());
                textArea.setBorderWidth(new Insets(0, 0, 2, 0));
            }
        });

        // set positions
        setTop(new VBox(menuBar));
        setCenter(textAreaPane);
        setBottom(numPad);
        BorderPane.setMargin(numPad, new Insets(42, 12, 54, 12));
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public Menu getPreferencesMenu() {
        return preferencesMenu;
    }

    public MenuItem getFontSizeMenu() {
        return fontSizeMenu;
    }

    public Map<Integer, RadioMenuItem> getFontSizeMenuItems() {
        return fontSizeMenuItems;
    }

    public ToggleGroup getFontSizesToggleGroup() {
        return fontSizesToggleGroup;
    }

    public Menu getThemeMenu() {
        return themeMenu;
    }

    public RadioMenuItem getLightThemeMenuItem() {
        return lightThemeMenuItem;
    }

    public RadioMenuItem getDarkThemeMenuItem() {
        return darkThemeMenuItem;
    }

    public ToggleGroup getThemesToggleGroup() {
        return themesToggleGroup;
    }

    public StackPane getTextAreaPane() {
        return textAreaPane;
    }

    public StyledTextArea getTextArea() {
        return textArea;
    }

    public GridPane getNumPad() {
        return numPad;
    }

    public KeyPadButton getPlusButton() {
        return plusButton;
    }

    public KeyPadButton getSpaceButton() {
        return spaceButton;
    }

    public KeyPadButton getShiftButton() {
        return shiftButton;
    }

    public List<KeyPadButton> getButtons() {
        return buttons;
    }

    private static KeyPadButton createButton(String primaryText, String secondaryText, Pos secondaryPosition) {
        return new KeyPadButton(
                new StyledLabel(primaryText, 24, Pos.CENTER),
                new StyledLabel(secondaryText, 16, secondaryPosition)
        );
    }

}
