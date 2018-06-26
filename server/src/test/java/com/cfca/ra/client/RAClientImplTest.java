package com.cfca.ra.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RAClientImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    class A {
        String name1;
        String name2;

        @Override
        public String toString() {
            return "A{" + "name1='" + name1 + '\'' + ", name2='" + name2 + '\'' + '}';
        }
    }

    class B {

        private final A a;

        B(A a) {
            this.a = a;
        }

        @Override
        public String toString() {
            return "B{" + "a=" + a + '}';
        }
    }

    @Test
    public void enroll() {
        // FileUtils.readFileToByteArray()
        final A a = new A();
        a.name1 = "A1";
        System.out.println(a);
        final B b = new B(a);
        a.name1 = "A2";
        System.out.println(a);
        System.out.println(b);
    }
}