package ru.mip3x.lab4.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NavigationBeanTest {
    @Test
    void navigationMethods() {
        NavigationBean nav = new NavigationBean();
        assertEquals("index", nav.goToIndex());
        assertEquals("main",  nav.goToMain());
    }
}
