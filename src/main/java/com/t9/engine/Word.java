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

package com.t9.engine;

import java.util.Map;

/**
 * @author ahmad
 */
public final class Word implements Comparable<Word> {

    private String value;
    private int frequency;

    public Word() {
    }

    public Word(String value, int frequency) {
        this.value = value;
        this.frequency = frequency;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int hashCode() {
        return value.hashCode() * 31 + frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Word that = (Word) o;
        return frequency == that.frequency && value.equals(that.value);
    }

    @Override
    public String toString() {
        return "{" +
                "value='" + value + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    @Override
    public int compareTo(Word o) {
        return Integer.compare(frequency, o.frequency);
    }

    static Word from(Map.Entry<String, Integer> entry) {
        return new Word(entry.getKey(), entry.getValue());
    }

}
