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
import com.t9.util.serialization.KryoSerializer;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author ahmad
 */
public final class CharTrie {

    /**
     * A KryoSerializer that is used for Serializing and Deserializing dictionaries.
     */
    @SuppressWarnings("unchecked")
    private static final KryoSerializer<List<Word>> WORD_LIST_SERIALIZER = new KryoSerializer<>(() -> {
        Kryo kryo = new Kryo();
        kryo.register(Word.class);
        return kryo;
    }, (Class<List<Word>>) (Class<?>) ArrayList.class);

    /**
     * Pointer to root Node
     */
    private final Node root = new Node();

    /**
     * Puts the specified word in this CharTrie and increases its frequency.
     *
     * @param word word to put in this CharTrie
     * @return the previous frequency of the specified word
     */
    public int put(String word) {
        if (word.isEmpty()) {
            return 0;
        }
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            current = current.children.computeIfAbsent(word.charAt(i), ch -> new Node());
        }
        return current.getAndIncreaseFrequency();
    }

    /**
     * @param word the word whose frequency is to be returned
     * @return the current frequency of the specified word or -1 if there isn't such a word in this CharTrie
     */
    public int frequency(String word) {
        if (word.isEmpty()) {
            return 0;
        }
        Node current = root;
        for (int i = 0; i < word.length() && current != null; i++) {
            current = current.children.get(word.charAt(i));
        }
        return current == null ? -1 : current.frequency;
    }

    /**
     * @param word the word whose presence in this CharTrie is to be tested
     * @return true if this CharTrie contains the specified word
     */
    public boolean contains(String word) {
        return frequency(word) > 0;
    }

    /**
     * @return a CharTrie Iterator over the Nodes in this CharTrie, starting at the root Node.
     */
    public Iterator iterator() {
        return new Iterator(root);
    }

    /**
     * Node in the CharTrie.
     * frequency-children entry
     */
    private static final class Node {

        /**
         * the number of occurrences of the character that is associated to this Node,
         * at certain position in the CharTrie
         */
        private volatile int frequency = 0;
        private static final AtomicIntegerFieldUpdater<Node> frequencyUpdater
                = AtomicIntegerFieldUpdater.newUpdater(Node.class, "frequency");

        /**
         * Children of this Node
         */
        private final Map<Character, Node> children = new ConcurrentHashMap<>();

        /**
         * Atomically increments by one the current value of the frequency.
         *
         * @return the previous frequency
         */
        private int getAndIncreaseFrequency() {
            return frequencyUpdater.getAndIncrement(this);
        }

    }

    /**
     * Iterator over the Nodes in the CharTrie
     */
    public static final class Iterator implements Cloneable {

        /**
         * Pointer to current Node
         */
        private Node current;

        private Iterator(Node current) {
            this.current = current;
        }

        /**
         * Returns true if the current Node contains the specified character in its children,
         * then moves to the child Node.
         * Otherwise, the current Node will not change.
         *
         * @param ch the character whose presence in the current Node's children is to be tested
         * @return true if the current Node's children contains the specified character
         */
        public boolean next(char ch) {
            Node next = current.children.get(ch);
            if (next == null) {
                return false;
            }
            current = next;
            return true;
        }

        /**
         * @return the current frequency of the current Node
         */
        public int frequency() {
            return current.frequency;
        }

        /**
         * @return the newly created CharTrie Iterator, starting at the current Node of this Iterator
         */
        @Override
        @SuppressWarnings("CloneDoesntCallSuperClone")
        public Iterator clone() {
            return new Iterator(current);
        }

    }

    /**
     * Exports this CharTrie's dictionary as binary object to the specified file.
     *
     * @param dest the output file
     * @throws IOException
     */
    public void exportBinary(Path dest) throws IOException {
        List<Word> words = new ArrayList<>();
        move(root, "", (v, f) -> words.add(new Word(v, f)));

        try (GZIPOutputStream outputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(dest.toFile())))) {
            WORD_LIST_SERIALIZER.write(words, outputStream);
        }
    }

    /**
     * Exports this CharTrie's dictionary as text to the specified file.
     *
     * @param dest the output file
     * @throws IOException
     */
    public void exportDictionary(Path dest) throws IOException {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(dest.toFile()))) {
            move(root, "", (v, f) -> {
                try {
                    for (int i = f; i > 0; i--) {
                        writer.write(v);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    throw new Error(e);
                }
            });
        }
    }

    private static void move(Node node, String word, BiConsumer<String, Integer> action) {
        if (node.frequency > 0) {
            action.accept(word, node.frequency);
        }
        for (Map.Entry<Character, Node> e : node.children.entrySet()) {
            move(e.getValue(), word + e.getKey(), action);
        }
    }

    /**
     * Imports dictionary from the specified binary file to this CharTrie.
     *
     * @param src the source file to be imported
     * @throws IOException
     */
    public void importBinary(Path src) throws IOException {
        try (GZIPInputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(src.toFile())))) {
            for (Word word : WORD_LIST_SERIALIZER.read(inputStream)) {
                for (int i = word.getFrequency(); i > 0; i--) {
                    put(word.getValue());
                }
            }
        }
    }

    /**
     * Imports dictionary from the specified text file to this CharTrie.
     *
     * @param src the source file to be imported
     * @throws IOException
     */
    public void importDictionary(Path src) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(src.toFile()))) {
            String word;
            while ((word = reader.readLine()) != null) {
                put(word);
            }
        }
    }

}
