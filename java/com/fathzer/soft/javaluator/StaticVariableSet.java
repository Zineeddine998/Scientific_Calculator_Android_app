package com.fathzer.soft.javaluator;

import java.util.HashMap;
import java.util.Map;

public class StaticVariableSet<T> implements AbstractVariableSet<T> {
    private final Map<String, T> varToValue = new HashMap();

    public T get(String variableName) {
        return this.varToValue.get(variableName);
    }

    public void set(String variableName, T value) {
        this.varToValue.put(variableName, value);
    }
}
