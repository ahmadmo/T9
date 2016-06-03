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

package com.t9.util.fx;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author ahmad
 */
public final class ColorScheme {

    private final Map<String, Color> colors = new HashMap<>();

    public ColorScheme(String resourceName) throws IOException {
        init(load(resourceName));
    }

    public ColorScheme(InputStream inputStream) throws IOException {
        init(load(inputStream));
    }

    private void init(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            colors.put((String) entry.getKey(), Color.web((String) entry.getValue()));
        }
    }

    public Color get(String name) {
        return colors.get(name);
    }

    private static Properties load(Object obj) throws IOException {
        Properties properties = new Properties();
        if (obj instanceof String) {
            properties.load(ColorScheme.class.getResourceAsStream((String) obj));
        } else if (obj instanceof InputStream) {
            properties.load((InputStream) obj);
        }
        return properties;
    }

}
