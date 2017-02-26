package ru.ifmo.ctddev.berinchik.implementor;

/**
 * Created by vadim on 24.02.17.
 */
public class ClassToImpl {
    private final int x = 10;
    private final String str = "!123!";

    private Void doSomething() {
        System.out.println("xxx!");
        return null;
    }

    public int[] arrayReturnType(int a, int b, int d) {
        return new int[]{1, 2, 3};
    }

    public byte x() {
        return 5;
    }

    protected String[] stringArray(int[] a, String[] bc) {
        return new String[]{"1"};
    }

    public void goSomewhere() {
        System.out.println("do this in future!");
    }

    private int function(String s, int b) {
        return 5;
    }

    //abstract Object met(Object[] arr);
}
