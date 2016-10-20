package com.fourigin.cms.repository.strategies;

import java.util.Collection;

public interface TraversingStrategy<T, V> {
    Collection<T> collect(V container);
}