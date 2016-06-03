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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ahmad
 */
public final class KryoSerializer<T> implements BinaryObjectSerializer<T> {

    private static final KryoFactory DEFAULT_KRYO_FACTORY = Kryo::new;

    private final KryoPool kryoPool;
    private final Class<T> inputType;

    public KryoSerializer(Class<T> inputType) {
        this(DEFAULT_KRYO_FACTORY, inputType);
    }

    public KryoSerializer(KryoFactory kryoFactory, Class<T> inputType) {
        kryoPool = new KryoPool.Builder(kryoFactory).softReferences().build();
        this.inputType = inputType;
    }

    @Override
    public byte[] serialize(T object) throws IOException {
        try (ByteArrayOutputStream outputStream = write(object)) {
            return outputStream.toByteArray();
        }
    }

    @Override
    public ByteArrayOutputStream write(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(object, outputStream);
        return outputStream;
    }

    @Override
    public void write(T object, OutputStream outputStream) throws IOException {
        Output output = new Output(outputStream);
        writeMessage(output, object);
        output.flush();
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        return readMessage(new Input(bytes));
    }

    @Override
    public T read(InputStream inputStream) throws IOException {
        return readMessage(new Input(inputStream));
    }

    @Override
    public Class<T> getInputType() {
        return inputType;
    }

    private void writeMessage(Output output, T object) {
        final Kryo kryo = kryoPool.borrow();
        try {
            kryo.writeObject(output, object);
        } finally {
            kryoPool.release(kryo);
        }
    }

    private T readMessage(Input input) {
        final Kryo kryo = kryoPool.borrow();
        try {
            return kryo.readObject(input, inputType);
        } finally {
            kryoPool.release(kryo);
        }
    }

}