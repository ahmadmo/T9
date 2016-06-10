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

package com.t9;

import com.t9.util.concurrent.Switch;
import com.t9.util.fx.ColorScheme;
import com.t9.view.T9Controller;
import com.t9.view.T9Layout;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author ahmad
 */
public final class MainApp extends Application {

    private static final ReadOnlyObjectWrapper<Stage> STAGE = new ReadOnlyObjectWrapper<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        STAGE.set(primaryStage);

        // color schemes
        ColorScheme darkColorScheme = new ColorScheme("/fx/colors/dark-color-scheme.properties");
        ColorScheme lightColorScheme = new ColorScheme("/fx/colors/light-color-scheme.properties");

        // init layout and scene
        T9Layout layout = new T9Layout(18);

        double width = Screen.getPrimary().getBounds().getWidth() * 3 / 4;
        Scene scene = new Scene(layout, width, width * 9 / 16);
        scene.getStylesheets().add(getClass().getResource("/fx/css/material-fx.css").toExternalForm());

        // select current menu items
        layout.getFontSizeMenuItems().get(18).setSelected(true);
        layout.getDarkThemeMenuItem().setSelected(true);

        // init controller
        new T9Controller(layout, new Switch<>(darkColorScheme, lightColorScheme));

        // prepare and show stage
        primaryStage.setTitle("T9");
        primaryStage.setScene(scene);
        primaryStage.show();

        layout.getTextArea().tryBindContentStyle();
    }

    public static Stage getStage() {
        return STAGE.get();
    }

    public static ReadOnlyObjectProperty<Stage> stageProperty() {
        return STAGE.getReadOnlyProperty();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
