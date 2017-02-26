package ru.ifmo.ctddev.berinchik.implementor;

import com.sun.istack.internal.NotNull;

/**
 * Created by vadim on 24.02.17.
 */
public interface Interface {
    void f1();
    int f2(String a, int b);

    @interface Annotation {
        String getType();
        int[] x = new int[]{4, 5, 6};

    }

    String[] func(int[] a, String[] method);
}
