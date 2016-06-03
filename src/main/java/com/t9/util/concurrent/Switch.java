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

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author ahmad
 */
public final class Switch<T> {

    private volatile T left;
    private volatile T right;
    private volatile int control = -1;

    @SuppressWarnings("all")
    private static final AtomicReferenceFieldUpdater<Switch, Object> leftUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Switch.class, Object.class, "left");
    @SuppressWarnings("all")
    private static final AtomicReferenceFieldUpdater<Switch, Object> rightUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Switch.class, Object.class, "right");
    private static final AtomicIntegerFieldUpdater<Switch> controlUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Switch.class, "control");

    public Switch() {
    }

    public Switch(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        leftUpdater.set(this, left);
    }

    public boolean isLeft() {
        return control == -1;
    }

    public boolean toLeft() {
        return compareAndSetControl(1, -1);
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        rightUpdater.set(this, right);
    }

    public boolean isRight() {
        return control == 1;
    }

    public boolean toRight() {
        return compareAndSetControl(-1, 1);
    }

    private boolean compareAndSetControl(int expect, int update) {
        return controlUpdater.compareAndSet(this, expect, update);
    }

    public T get() {
        return get(control);
    }

    public T getOpposite() {
        return get(-control);
    }

    private T get(int control) {
        switch (control) {
            case 1:
                return right;
            default:
                return left;
        }
    }

    public T getAndSwitch() {
        return switchAndGet(1);
    }

    public T switchAndGet() {
        return switchAndGet(-1);
    }

    private T switchAndGet(int c) {
        int curVal;
        do {
            curVal = control;
        } while (!controlUpdater.compareAndSet(this, curVal, -curVal));
        return c != curVal ? left : right;
    }

}
