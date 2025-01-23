package ru.mip3x.lab4.beans;

import jakarta.ejb.Stateful;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@Stateful
public class NavigationBean implements Serializable {
    public String goToIndex() {
        return "index";
    }

    public String goToMain() {
        return "main";
    }
}
