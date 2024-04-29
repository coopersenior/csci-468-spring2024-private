package edu.montana.csci.csci468.demo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Scratch {

    int add(int i) {
        return i + 13;
    }

    static boolean comp(int a, int b) {
        return a != b;
        // a != b
    }

    void list() {
        ArrayList<Object> objects = new ArrayList<>();
            objects.add(1);
            objects.add(2);
            objects.add(3);
    }

    public int intFunc(int i1, int i2) {
        return i1 + i2;
    }

    public static void main(String[] args) {
        System.out.println("foo");
        System.out.println("bar");
        System.out.println(comp(4, 5));
    }
}
