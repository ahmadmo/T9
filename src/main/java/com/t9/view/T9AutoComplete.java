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

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.t9.MainApp;
import com.t9.util.fx.MouseHandler;
import com.t9.view.controls.WordSelector;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.scene.input.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author ahmad
 */
public final class T9AutoComplete {

    private static final KeyCombination CTRL_SPACE = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);

    private final T9Layout layout;
    private final WordSelector wordSelector;

    private final StringProperty text = new SimpleStringProperty();
    private final IntegerProperty caretPosition = new SimpleIntegerProperty();

    public T9AutoComplete(T9Layout layout, WordSelector wordSelector) {
        this.layout = layout;
        this.wordSelector = wordSelector;

        init();
    }

    private void init() {
        text.bind(layout.getTextArea().textProperty());
        caretPosition.bind(layout.getTextArea().caretPositionProperty());

        layout.getTextArea().textProperty().addListener((observable, oldValue, newValue) -> {
            showSelector();
        });

        layout.getTextArea().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (CTRL_SPACE.match(event)) {
                showSelector();
            }
        });

        wordSelector.getWordList().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (CTRL_SPACE.match(event)) {
                showSelector();
            }
        });

        wordSelector.getWordList().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                wordSelector.hide();
            }
        });

        MouseHandler.addPrimaryHandler(wordSelector.getWordList(), MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                wordSelector.hide();
            }
        });

        wordSelector.lastSelectedWordProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });

    }

    private void showSelector() {
        wordSelector.hide();

        if (layout.getTextArea().textProperty().get().isEmpty()) {
            return;
        }

        wordSelector.setWords(
                IntStream.range(0, ThreadLocalRandom.current().nextInt(10) + 1)
                        .mapToObj(i -> "Word " + (i + 1))
                        .collect(Collectors.toList())
        );

        WordRange range = findWordAt(text.get(), caretPosition.get());
        System.out.println(text.get().substring(range.startInclusive, range.endExclusive));

//        layout.getTextArea().selectRange(range.startInclusive, range.endExclusive);

        TextAreaSkin textAreaSkin = (TextAreaSkin) layout.getTextArea().getSkin();
        Bounds localBounds = textAreaSkin.getCaretBounds();
        Bounds bounds = layout.getTextArea().localToScreen(localBounds);

        wordSelector.setX(bounds.getMaxX());
        wordSelector.setY(bounds.getMaxY());
        wordSelector.show(MainApp.getStage());
    }

    private static WordRange findWordAt(String text, int position) {
        int n = text.length();

        int leftBorder = 0;
        int rightBorder = n;

        for (int left = Math.min(position, n - 1); left > 0; left--) {
            if (!Character.isLetter(text.charAt(left))) {
                leftBorder = left + 1;
                break;
            }
        }

        for (int right = position + 1; right < n; right++) {
            if (!Character.isLetter(text.charAt(right))) {
                rightBorder = right;
                break;
            }
        }

        return new WordRange(leftBorder, rightBorder);
    }

    private static final class WordRange {

        private final int startInclusive;
        private final int endExclusive;

        private WordRange(int startInclusive, int endExclusive) {
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
        }

    }

}
