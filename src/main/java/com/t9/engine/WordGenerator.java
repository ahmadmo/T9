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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.function.BiConsumer;

/**
 * @author ahmad
 */
final class WordGenerator {

    private WordGenerator() {
    }

    static void generate(char[][] table, CharTrie trie, BiConsumer<String, Integer> action) {
        final ForkJoinPool pool = ForkJoinPool.commonPool();
        final VisitorContext ctx = new VisitorContext(table, action);
        for (int x = 0; x < table[0].length; x++) {
            pool.invoke(new Visitor(x, 0, "", trie.iterator(), ctx));
        }
    }

    private static final class VisitorContext {

        private final char[][] table;
        private final BiConsumer<String, Integer> action;

        private VisitorContext(char[][] table, BiConsumer<String, Integer> action) {
            this.table = table;
            this.action = action;
        }

    }

    private static final class Visitor extends RecursiveAction {

        private final int x, y;
        private final CharTrie.Iterator iterator;
        private final VisitorContext ctx;

        private String word;

        private Visitor(int x, int y, String word, CharTrie.Iterator iterator, VisitorContext ctx) {
            this.x = x;
            this.y = y;
            this.word = word;
            this.iterator = iterator;
            this.ctx = ctx;
        }

        @Override
        protected void compute() {
            char nextChar = ctx.table[y][x];
            if (iterator.next(nextChar)) {
                word += nextChar;
                if (y + 1 < ctx.table.length) {
                    int n = ctx.table[y + 1].length;
                    ForkJoinTask[] tasks = new ForkJoinTask[n];
                    for (int x = 0; x < n; x++) {
                        tasks[x] = new Visitor(x, y + 1, word, iterator.clone(), ctx);
                    }
                    invokeAll(tasks);
                } else if (iterator.frequency() > 0) {
                    ctx.action.accept(word, iterator.frequency());
                }
            }
        }

    }

}
