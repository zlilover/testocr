package com.sc.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFApiParameterList extends ArrayList<LFApiParameter> {
    private static final long serialVersionUID = 3668948424416187047L;

    private LFApiParameterList() {
    }

    public final Boolean add(String name, Object value) throws IllegalArgumentException {
        LFApiParameter parameter = new LFApiParameter(name, value);
        return this.add(parameter);
    }

    public final LFApiParameterList with(String name, Object value) throws IllegalArgumentException {
        this.add(name, value);
        return this;
    }

    public void remove(String name) {
        for (LFApiParameter item : this) {
            if (item.name.equals(name)) {
                this.remove(item);
                break;
            }
        }
    }

    public void removeContains(String name) {
        List<LFApiParameter> delete = new ArrayList<LFApiParameter>();
        for (LFApiParameter item : this) {
            if (item.name.startsWith(name)) {
                delete.add(item);
            }
        }
        for (LFApiParameter di : delete) {
            this.remove(di);
        }
    }

    public Object getValue(String name) {
        Object ret = null;
        for (LFApiParameter item : this) {
            if (item.name.equals(name)) {
                ret = item.value;
                break;
            }
        }

        return ret;
    }

    public final static LFApiParameterList create() {
        return new LFApiParameterList();
    }

    public final static LFApiParameterList createWith(String name, Object value) throws IllegalArgumentException {
        LFApiParameterList list = new LFApiParameterList();
        return list.with(name, value);
    }

    public final boolean contains(String name) {
        for (LFApiParameter item : this) {
            if (item.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
