package se.de.hu_berlin.informatik.experiments.test;

public class Hello {
    public static void main(final String[] arg) {
        System.out.println("Hello world!");
        int i = 0;
        final int j = 1;
        if (i < j) {
            i++;
        }
    }

    public String test2() {
        int i = 0;
        final int j = 1;
        if (i < j) {
            i++;
        }

        return "Hello";
    }
}