package com.mcz;

import javax.swing.*;

/**
 * Klasa Main.
 */
public class Main {

    private KolkoIKrzyzykGUI kolkoIKrzyzuk;

    /**
     *
     * Metoda main. Utworzenie obiektu klasy KolkoiKrzyzykGUI.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new KolkoIKrzyzykGUI();
            }
        });
    }
}
