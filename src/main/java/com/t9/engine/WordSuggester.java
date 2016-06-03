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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author ahmad
 */
public final class WordSuggester {

    private static final Pattern INPUT_PATTERN = Pattern.compile("[2-9]+");

    private static final char[][] T9_TABLE = {
            {'a', 'b', 'c'},
            {'d', 'e', 'f'},
            {'g', 'h', 'i'},
            {'j', 'k', 'l'},
            {'m', 'n', 'o'},
            {'p', 'q', 'r', 's'},
            {'t', 'u', 'v'},
            {'w', 'x', 'y', 'z'}
    };

    public static Stream<Word> suggest(CharTrie trie, String input) {
        return suggest(trie, input, 0);
    }

    public static Stream<Word> suggest(CharTrie trie, String input, int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta must be greater than or equal to zero.");
        }
        List<Map<String, Integer>> words = new ArrayList<>();
        suggest(trie, input, delta, words);
        return words.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Word::from)
                .distinct()
                .sorted((a, b) -> b.compareTo(a));
    }

    private static void suggest(CharTrie trie, String input, int delta, List<Map<String, Integer>> wordList) {
        final Map<String, Integer> words = new ConcurrentHashMap<>();
        WordGenerator.generate(mapTable(input), trie, words::put);
        wordList.add(words);
        if (--delta >= 0) for (int i = 2; i <= 9; i++) {
            suggest(trie, input + i, delta, wordList);
        }
    }

    private static char[][] mapTable(String input) {
        int n = input.length();
        if (n < 1 || !INPUT_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Input string must contains only digits 2..9");
        }
        final char[][] table = new char[n][];
        for (int i = 0; i < n; i++) {
            table[i] = T9_TABLE[Character.getNumericValue(input.charAt(i)) - 2];
        }
        return table;
    }

}
