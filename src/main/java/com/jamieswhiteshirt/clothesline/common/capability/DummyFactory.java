package com.jamieswhiteshirt.clothesline.common.capability;

import java.util.concurrent.Callable;

public class DummyFactory<T> implements Callable<T> {
    @Override
    public T call() {
        throw new UnsupportedOperationException("This capability provides no default implementation");
    }
}
