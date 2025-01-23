package ru.mip3x.lab3;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class NavigationBean implements Serializable {
    public String goToWelcome() {
        return "welcome";
    }

    public String goToMain() {
        return "main";
    }
}
