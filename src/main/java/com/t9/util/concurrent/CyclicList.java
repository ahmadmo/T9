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

package com.t9.util.concurrent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author ahmad
 */
public final class CyclicList<T> {

    private final List<T> data = new CopyOnWriteArrayList<>();

    private volatile int index = 0;
    private static final AtomicIntegerFieldUpdater<CyclicList> indexUpdater =
            AtomicIntegerFieldUpdater.newUpdater(CyclicList.class, "index");

    public CyclicList() {
    }

    public CyclicList(List<T> initialData) {
        data.addAll(initialData);
    }

    public List<T> getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }

    public T current() {
        return data.get(index);
    }

    public T getAndNext() {
        return nextAndGet(false);
    }

    public T nextAndGet() {
        return nextAndGet(true);
    }

    private T nextAndGet(boolean next) {
        int curVal, newVal;
        do {
            curVal = resolveIndex();
            if (curVal == -1) {
                return null;
            }
            newVal = (curVal + 1) % data.size();
        } while (!indexUpdater.compareAndSet(this, curVal, newVal));
        return data.get(next ? newVal : curVal);
    }

    private int resolveIndex() {
        int curVal, newVal;
        do {
            curVal = index;
            newVal = Math.min(curVal, data.size() - 1);
        } while (!indexUpdater.compareAndSet(this, curVal, Math.max(newVal, 0)));
        return newVal;
    }

    public void reset() {
        int val;
        do {
            val = index;
        } while (!indexUpdater.compareAndSet(this, val, 0));
    }

}
