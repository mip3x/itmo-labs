package ru.mip3x.lab4.beans;

import jakarta.ejb.Stateful;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * A stateful bean used for navigating between views in the web application
 */
@Named
@Stateful
public class NavigationBean implements Serializable {
    /**
     * Navigates to the "index" view
     *
     * @return the view name "index"
     */
    public String goToIndex() {
        return "index";
    }

     /**
     * Navigates to the "main" view
     *
     * @return the view name "main"
     */
    public String goToMain() {
        return "main";
    }
}
