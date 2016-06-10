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

import javafx.geometry.Insets;

import java.math.BigDecimal;

/**
 * @author ahmad
 */
public class InsetsUtil {

    private InsetsUtil() {
    }

    public static String cssString(Insets insets) {
        return BigDecimal.valueOf(insets.getTop()).toPlainString() + " " +
                BigDecimal.valueOf(insets.getRight()).toPlainString() + " " +
                BigDecimal.valueOf(insets.getBottom()).toPlainString() + " " +
                BigDecimal.valueOf(insets.getLeft()).toPlainString();
    }

    public static Insets fromCssString(String input) {
        String[] segments = input.split("\\s");
        if (segments.length == 0 || segments.length > 4) {
            throw new IllegalStateException("Invalid input string");
        }
        if (segments.length == 1) {
            return new Insets(Double.parseDouble(segments[0]));
        }
        if (segments.length == 2) {
            double topBottom = Double.parseDouble(segments[0]);
            double rightLeft = Double.parseDouble(segments[1]);
            return new Insets(topBottom, rightLeft, topBottom, rightLeft);
        }
        if (segments.length == 3) {
            double top = Double.parseDouble(segments[0]);
            double rightLeft = Double.parseDouble(segments[1]);
            double bottom = Double.parseDouble(segments[2]);
            return new Insets(top, rightLeft, bottom, rightLeft);
        }
        return new Insets(
                Double.parseDouble(segments[0]),
                Double.parseDouble(segments[1]),
                Double.parseDouble(segments[2]),
                Double.parseDouble(segments[3])
        );
    }

}
