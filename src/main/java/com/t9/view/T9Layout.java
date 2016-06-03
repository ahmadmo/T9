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
import com.t9.view.controls.Symbols;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public final class T9Layout {

    private final BorderPane root;
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
    private final Symbols symbols;
    private final GridPane numPad;
    private final KeyPadButton plusButton;
    private final KeyPadButton spaceButton;
    private final KeyPadButton shiftButton;
    private final List<KeyPadButton> buttons;

    public T9Layout() {
        this(18);
    }

    public T9Layout(int textAreaFontSize) {
        root = new BorderPane();

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
        symbols = new Symbols(8, 8);
        symbols.primaryTextFillColorProperty().bind(textArea.textFillColorProperty());
        symbols.primaryBgColorProperty().bind(textArea.bgColorProperty());
        textAreaPane = new StackPane(textArea);

        // num pad
        numPad = new GridPane();
        numPad.setAlignment(Pos.CENTER);
        numPad.setHgap(12);
        numPad.setVgap(8);
        numPad.add(createButton("1", ".", 22, Pos.BOTTOM_RIGHT), 0, 0);
        numPad.add(createButton("2", "abc", 16, Pos.CENTER), 1, 0);
        numPad.add(createButton("3", "def", 16, Pos.CENTER), 2, 0);
        numPad.add(createButton("4", "ghi", 16, Pos.CENTER), 0, 1);
        numPad.add(createButton("5", "jkl", 16, Pos.CENTER), 1, 1);
        numPad.add(createButton("6", "mno", 16, Pos.CENTER), 2, 1);
        numPad.add(createButton("7", "pqrs", 16, Pos.CENTER), 0, 2);
        numPad.add(createButton("8", "tuv", 16, Pos.CENTER), 1, 2);
        numPad.add(createButton("9", "wxyz", 16, Pos.CENTER), 2, 2);
        numPad.add(plusButton = createButton("*", "+", 18, Pos.BOTTOM_RIGHT), 0, 3);
        numPad.add(spaceButton = createButton("0", "\u2423", 18, Pos.BOTTOM_RIGHT), 1, 3);
        numPad.add(shiftButton = createButton("#", "\u2191", 18, Pos.BOTTOM_RIGHT), 2, 3);

        buttons = Collections.unmodifiableList(
                numPad.getChildren().stream()
                        .map(node -> (KeyPadButton) node)
                        .collect(Collectors.toList())
        );

        // resizable buttons
        for (KeyPadButton button : buttons) {
            button.prefWidthProperty().bind(numPad.widthProperty().divide(4));
            button.prefHeightProperty().bind(numPad.heightProperty().divide(4));
        }
        numPad.prefHeightProperty().bind(root.heightProperty().divide(2));

        // set positions
        root.setTop(new VBox(menuBar));
        root.setCenter(textAreaPane);
        root.setBottom(numPad);
        BorderPane.setMargin(numPad, new Insets(42, 0, 54, 0));
    }

    public BorderPane getRoot() {
        return root;
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

    public Symbols getSymbols() {
        return symbols;
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

    private static KeyPadButton createButton(String primaryText, String secondaryText, int secondaryFontSize, Pos secondaryPosition) {
        return new KeyPadButton(
                new StyledLabel(primaryText, 24, Pos.CENTER),
                new StyledLabel(secondaryText, secondaryFontSize, secondaryPosition)
        );
    }

}
