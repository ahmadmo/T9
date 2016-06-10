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

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.stage.Popup;

import java.util.List;

/**
 * @author ahmad
 */
public final class WordSelector extends Popup {

    private static final int N_VISIBLE_ROWS = 4;

    private final ObservableList<String> words = FXCollections.observableArrayList();
    private final ListView<String> wordList = new ListView<>(words);
    private final DoubleProperty fontSize;

    private final ReadOnlyIntegerWrapper lastSelectedIndex = new ReadOnlyIntegerWrapper(-1);
    private final ReadOnlyStringWrapper lastSelectedWord = new ReadOnlyStringWrapper();

    private double cellInset;

    public WordSelector() {
        this(12);
    }

    public WordSelector(int fontSize) {
        wordList.getStyleClass().add("word-selector");
        this.fontSize = new SimpleDoubleProperty(fontSize);

        init();
    }

    private void init() {
        wordList.setCellFactory(v -> new MyListCell());
        wordList.fixedCellSizeProperty().bind(this.fontSize.multiply(2));

        words.addListener((ListChangeListener<? super String>) c ->
                wordList.setPrefHeight(wordList.getFixedCellSize() * Math.min(words.size(), N_VISIBLE_ROWS) + 2)
        );

        wordList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                lastSelectedIndex.set(newValue.intValue());
            }
        });

        wordList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedWord.set(newValue);
            }
        });

        setAutoFix(true);
        setHideOnEscape(true);
        setAutoHide(true);
        getContent().add(wordList);

        setOnShown(event -> Platform.runLater(() -> {
            setWidth();
            updateSelection();
        }));
    }

    private void setWidth() {
        double width = 0;
        ListCell<String> cell = new MyListCell();
        for (String item : words) {
            cell.setText(item);
            width = Math.max(width, cell.prefWidth(-1));
        }

        width *= fontSize.get() / 12;

        wordList.setPrefWidth(width
                + wordList.getInsets().getLeft() + wordList.getInsets().getRight()
                + cellInset
                + getVerticalScrollbar().getWidth()
                + 40
        );
    }

    private void updateSelection() {
        int i = lastSelectedIndex.get();
        if (i == -1) {
            wordList.getSelectionModel().selectFirst();
        } else if (i < words.size()) {
            wordList.getSelectionModel().select(i);
        } else {
            wordList.getSelectionModel().selectLast();
        }
    }

    public void setWords(List<String> words) {
        if (words.isEmpty()) {
            this.words.setAll("No suggestions");
        } else {
            this.words.setAll(words);
        }
    }

    public ListView<String> getWordList() {
        return wordList;
    }

    public double getFontSize() {
        return fontSize.get();
    }

    public DoubleProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize.set(fontSize);
    }

    public int getLastSelectedIndex() {
        return lastSelectedIndex.get();
    }

    public ReadOnlyIntegerProperty lastSelectedIndexProperty() {
        return lastSelectedIndex.getReadOnlyProperty();
    }

    public String getLastSelectedWord() {
        return lastSelectedWord.get();
    }

    public ReadOnlyStringProperty lastSelectedWordProperty() {
        return lastSelectedWord.getReadOnlyProperty();
    }

    private final class MyListCell extends ListCell<String> {

        private MyListCell() {
            super();
            updateListView(wordList);
            setSkin(createDefaultSkin());
            styleProperty().bind(new SimpleStringProperty("-fx-font-size: ").concat(fontSize).concat("px;"));
            insetsProperty().addListener((observable, oldValue, newValue) -> {
                cellInset = newValue.getLeft() + newValue.getRight();
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
        }

    }

    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : wordList.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

}
