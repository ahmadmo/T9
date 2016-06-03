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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.nio.charset.StandardCharsets;

/**
 * @author ahmad
 */
final class WordSerializer extends Serializer<Word> {

    @Override
    public void write(Kryo kryo, Output output, Word word) {
        kryo.writeObject(output, concat(toBytes(word.getFrequency()), word.getValue().getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Word read(Kryo kryo, Input input, Class<Word> type) {
        byte[] bytes = kryo.readObject(input, byte[].class);
        return new Word(new String(bytes, 4, bytes.length - 4, StandardCharsets.UTF_8), fromBytes(bytes));
    }

    private static byte[] toBytes(int i) {
        return new byte[]{
                (byte) (i >> 24 & 0xff),
                (byte) (i >> 16 & 0xff),
                (byte) (i >> 8 & 0xff),
                (byte) (i & 0xff)
        };
    }

    private static int fromBytes(byte[] bytes) {
        return (bytes[0] & 0xff) << 24
                | (bytes[1] & 0xff) << 16
                | (bytes[2] & 0xff) << 8
                | (bytes[3] & 0xff);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
