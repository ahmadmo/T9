package com.t9;

import com.t9.engine.CharTrie;
import com.t9.engine.WordSuggester;

import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ahmad
 */
public final class Test {

    public static void main(String[] args) throws Exception {
        CharTrie trie = new CharTrie();

        System.out.print("Importing dictionary... ");
        trie.importBinary(Paths.get(Test.class.getResource("/dictionary.bin").toURI()));
        System.out.println("done.");

        WordSuggester.suggest(trie, "5683", 3).limit(20).forEach(System.out::println);
        WordSuggester.suggest(trie, "5282", 3).limit(20).forEach(System.out::println);
        WordSuggester.suggest(trie, "4663", 3).limit(20).forEach(System.out::println);
        WordSuggester.suggest(trie, "72725535476", 3).limit(20).forEach(System.out::println);
        WordSuggester.suggest(trie, "73776674245489", 3).limit(20).forEach(System.out::println);
        WordSuggester.suggest(trie, "46837628466254928466", 3).limit(20).forEach(System.out::println);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        long totalTime = 0;
        int it = 10000, letters = 20;
        for (int i = 1; i <= letters; i++) {
            long time = 0;
            for (int j = 0; j < it; j++) {
                String randomNumber = randomNumber(i, random);
                long t = System.nanoTime();
                WordSuggester.suggest(trie, randomNumber);
                time += System.nanoTime() - t;
            }
            System.out.printf("letters = %d, response time = %,f ms\n", i, time / it / 1000000.0);
            totalTime += time;
        }
        System.out.printf("avg response time = %,f ms\n", totalTime / (letters * it) / 1000000.0);
    }

    private static String randomNumber(int nDigits, ThreadLocalRandom random) {
        StringBuilder sb = new StringBuilder(nDigits);
        for (int i = 0; i < nDigits; i++) {
            sb.append(Integer.toString(random.nextInt(8) + 2));
        }
        return sb.toString();
    }

}
