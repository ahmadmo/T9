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

package com.t9.util.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ahmad
 */
public interface BinaryObjectSerializer<T> extends ObjectSerializer<T, byte[]> {

    OutputStream write(T object) throws IOException;

    void write(T object, OutputStream outputStream) throws IOException;

    T read(InputStream inputStream) throws IOException;

    @Override
    default Class<byte[]> getOutputType() {
        return byte[].class;
    }

}
