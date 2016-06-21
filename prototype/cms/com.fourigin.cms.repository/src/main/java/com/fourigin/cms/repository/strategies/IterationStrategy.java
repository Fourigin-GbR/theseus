package com.fourigin.cms.repository.strategies;

import java.util.Iterator;

public interface IterationStrategy<T, V> {
    Iterator<T> createIteratorOver(V container);
}