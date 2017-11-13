package com.sc.plugin;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFApiParameter {
    public final String name;
    public final Object value;

    LFApiParameter(String name, Object value) throws IllegalArgumentException {
        if (name != null) {
            name = name.trim();
        }
        if (name == null || name.length() <= 0)
            throw new IllegalArgumentException("The argument 'name' can NOT be NULL or BLANK.");

        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "LFApiParameter{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
