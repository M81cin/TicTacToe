package com.mcz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Math.floorDiv;

/**
 * Klasa obsługująca główną planszę i GUI
 */
public class KolkoIKrzyzykGUI extends JFrame implements ActionListener {

    private StringBuffer plansza = new StringBuffer("---------");
    private enum Stan {INIT, PLAYING, FINISHED_WON_COMPUTER, FINISHED_WON_PLAYER, FINISHED_NO_WIN}
    private Stan stan;
    private Komputer komputer;

    private JMenuBar menuBar;
    private JMenu menuGra;
    private JMenuItem menuItemNowa;
    private JMenuItem menuItemZamknij;

    private JButton button[] = new JButton[9];
    private final ImageIcon iconX = createImageIcon("iconX.png", "X"  );
    private final ImageIcon iconO = createImageIcon("iconO.png", "O"  );

    /**
     * konstruktor - inicjuj GUI.
     */
    public KolkoIKrzyzykGUI() {
        super("Kółko i krzyżyk by MCZ :)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        menuBar = new JMenuBar();
        menuGra = new JMenu("Gra");
        menuItemNowa = new JMenuItem("Nowa");
        menuItemZamknij = new JMenuItem("Zakończ");

        menuItemNowa.setActionCommand("Nowa");
        menuItemNowa.addActionListener(this);
        menuItemZamknij.setActionCommand("Zamknij");
        menuItemZamknij.addActionListener(this);

        menuGra.add(menuItemNowa);
        menuGra.addSeparator();
        menuGra.add(menuItemZamknij);
        menuBar.add(menuGra);
        setJMenuBar(menuBar);

        setLayout(null);
        for (int i=0; i<9; i++) {
            button[i] = new JButton();
            button[i].setBounds((i - 3 * floorDiv(i, 3)) * 150, floorDiv(i, 3) * 150, 150, 150);
            button[i].setEnabled(true);
            button[i].setActionCommand(String.valueOf(i));
            button[i].addActionListener(this);
            this.add(button[i]);
        }

        setSize(464, 510);
        setVisible(true);

        repaint();

        this.plansza.replace(0, 9, "---------");
        this.stan = Stan.INIT;
        this.komputer = new Komputer();
    }

    /**
     * Obsługa menu i kliknięć gracza na planszy; wywołanie ruchu komputera po ruchu gracza.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("Zamknij")) {
            dispose();
        } else if (cmd.equals("Nowa")) {
            this.inicjujGre();
        } else {
            Object obj = e.getSource();

            for (int i = 0; i < 9; i++) {
                if (obj == button[i]) {
                    button[i].setIcon(iconX);
                    button[i].setEnabled(false);

                    this.ustawZnak(i, Komputer.ZNAK_GRACZA);
                    this.stan = this.okreslStan();

                    this.repaint();

                    if (!sprawdzWarunkiZakonczenia()) {
                        // wywołaj ruch komputera:
                        this.wywolajRuchKomputera();
                    }
                }
            }
        }
    }

    /**
     * Wykonuje ruch komputera na planszy.
     */
    public void wywolajRuchKomputera () {

        int ruchKomputera = this.komputer.wykonajRuch(this.plansza);

        button[ruchKomputera].setIcon(iconO);
        button[ruchKomputera].setEnabled(false);

        this.ustawZnak(ruchKomputera, Komputer.ZNAK_KOMPUTERA);
        this.stan = this.okreslStan();

        this.sprawdzWarunkiZakonczenia();
    }

    /**
     *
     * Ustawia znak na planszy na podanej pozycji. Zwraca -1, jeśli pole o podanej pozycji jest niepuste.
     */
    public final int ustawZnak(int pozycja, char znak) {
        if ((pozycja <= 8) && (this.plansza.charAt(pozycja) == '-')) {
            this.plansza.setCharAt(pozycja, znak);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Określa aktualny stan/wynik i zwraca jako Stan.
     */
    public Stan okreslStan() {
        // 1. INIT
        if (this.plansza.compareTo(new StringBuffer("---------")) == 0) {
            return Stan.INIT;
        // 2. FINISHED_WON_PLAYER
        } else if (czyWygral('X')) {
            return Stan.FINISHED_WON_PLAYER;
        // 3. FINISHED_WON_COMPUTER
        } else if (czyWygral('O')) {
            return Stan.FINISHED_WON_COMPUTER;
        // 4. FINISHED_NO_WIN
        } else if (this.plansza.indexOf("-") == -1) {
            return Stan.FINISHED_NO_WIN;
        // 5. PLAYING
        } else
            return Stan.PLAYING;
    }

    /**
     * Zwraca true, jeśli na planszy zostanie odnaleziona wygrywająca sekwencja podanego znaku (poziomo / pionowo / ukośnie).
     */
    private boolean czyWygral(Character ch) {
        String chX3 = ch.toString() + ch.toString() + ch.toString();

        // poziomo
        if (this.plansza.substring(0, 3).equals(chX3)) return true;
        if (this.plansza.substring(3, 6).equals(chX3)) return true;
        if (this.plansza.substring(6, 9).equals(chX3)) return true;

        // pionowo
        for(int i = 0; i<3; i++) {
            if ((this.plansza.charAt(i) == ch) && (this.plansza.charAt(i+3) == ch) && (this.plansza.charAt(i+6) == ch)) return true;
        }

        // skośnie
        if ((this.plansza.charAt(0) == ch) && (this.plansza.charAt(4) == ch) && (this.plansza.charAt(8) == ch)) return true;
        if ((this.plansza.charAt(2) == ch) && (this.plansza.charAt(4) == ch) && (this.plansza.charAt(6) == ch)) return true;

        // (...else...)
        return false;
    }

    /**
     * Sprawdza czy gra się zakończyła - jeśli tak, to wyświetla stosowny kompunikat i zwraca true.
     */
    private boolean sprawdzWarunkiZakonczenia() {
        if ((this.stan == Stan.FINISHED_WON_COMPUTER) || (this.stan == Stan.FINISHED_NO_WIN) || (this.stan == Stan.FINISHED_WON_PLAYER)) {

            for (int i=0; i<9; i++) {
                button[i].setEnabled(false);
            }

            this.repaint();

            String wiadomoscDoWyswietlenia;
            if (this.stan == Stan.FINISHED_WON_COMPUTER) {
                    wiadomoscDoWyswietlenia = "Koniec gry. Wygrał komputer!";
                } else if (this.stan == Stan.FINISHED_NO_WIN) {
                    wiadomoscDoWyswietlenia = "Koniec gry. Nikt nie wygrał!";
                } else {
                    wiadomoscDoWyswietlenia = "Koniec gry. Wygrał gracz 1!";
                }
            JOptionPane.showMessageDialog(this, wiadomoscDoWyswietlenia, "Gra zakończona", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } else
            return false;
    }

    /**
     * Inicjuje nową grę
     */
    private void inicjujGre() {
        this.plansza.replace(0, 9, "---------");
        this.stan = Stan.INIT;

        for (int i=0; i<9; i++) {
            button[i].setIcon(null);
            button[i].setEnabled(true);
        }
    }

    /**
     * This method is based on https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(String path,
                                      String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
