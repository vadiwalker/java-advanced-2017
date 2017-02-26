package ru.ifmo.ctddev.berinchik.arrayset;

import com.sun.xml.internal.bind.v2.TODO;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by vadim on 17.02.17.
 */
public class Main {

    public static void main(String[] args) {
        new Main().test();
    }

    void test() {
        SortedSet set = new ArraySet<String>();
        System.out.println(set.getClass());
    }
}
