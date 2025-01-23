package ru.mip3x.lab4;

import jakarta.ejb.Stateful;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@Stateful
public class NavigationBean implements Serializable {
    public String goToWelcome() {
        return "welcome";
    }

    public String goToMain() {
        return "main";
    }
}
