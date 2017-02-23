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

    public @interface TODO {
        int id();
        String synopsis();
        String assignee() default "unassigned";
        String date() default "undefined";
    }

    @TODO (
            id = 123,
            synopsis = "Implement",
            assignee = "Georigy Korneev",
            date = "22.02.2017"
    )

    void test() {
        SortedSet set = new ArraySet<String>();
        System.out.println(set.getClass().getU);
    }
}
