package com.fathzer.soft.javaluator;

public class Function {
    private int maxArgumentCount;
    private int minArgumentCount;
    private String name;

    public Function(String name, int argumentCount) {
        this(name, argumentCount, argumentCount);
    }

    public Function(String name, int minArgumentCount, int maxArgumentCount) {
        if (minArgumentCount < 0 || minArgumentCount > maxArgumentCount) {
            throw new IllegalArgumentException("Invalid argument count");
        } else if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Invalid function name");
        } else {
            this.name = name;
            this.minArgumentCount = minArgumentCount;
            this.maxArgumentCount = maxArgumentCount;
        }
    }

    public int getMaximumArgumentCount() {
        return this.maxArgumentCount;
    }

    public int getMinimumArgumentCount() {
        return this.minArgumentCount;
    }

    public String getName() {
        return this.name;
    }
}
