package ru.ifmo.ctddev.berinchik.implementor;

import org.junit.Test;

/**
 * Created by vadim on 25/02/17.
 */
public interface Base3 extends Base2 {
    int f3();

    @Override
    int f2();
}
