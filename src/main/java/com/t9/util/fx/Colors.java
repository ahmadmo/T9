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

import java.math.BigDecimal;

/**
 * @author ahmad
 */
public final class Colors {

    private Colors() {
    }

    public static String webString(Color color) {
        return "#"
                + fmt(Integer.toHexString((int) Math.round(color.getRed() * 255)))
                + fmt(Integer.toHexString((int) Math.round(color.getGreen() * 255)))
                + fmt(Integer.toHexString((int) Math.round(color.getBlue() * 255)))
                + fmt(Integer.toHexString((int) Math.round(color.getOpacity() * 255)));
    }

    public static String rgbaString(Color color) {
        return "rgba("
                + (int) Math.round(color.getRed() * 255.0) + ","
                + (int) Math.round(color.getGreen() * 255.0) + ","
                + (int) Math.round(color.getBlue() * 255.0) + ","
                + BigDecimal.valueOf(color.getOpacity()).toPlainString()
                + ")";
    }

    private static String fmt(String hex) {
        switch (hex.length()) {
            case 0:
                return "00";
            case 1:
                return 0 + hex;
            default:
                return hex;
        }
    }

}
