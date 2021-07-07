package com.mcz;

import java.util.ArrayList;
import java.util.Random;

/**
 * Klasa obsługująca ruchy i "logikę" komputera
 */
public class Komputer {

    private final int[] wartosci = new int[9];
    public static final char ZNAK_GRACZA = 'X';
    public static final char ZNAK_KOMPUTERA = 'O';

    /**
     * Wartościuj pola i zwróć numer pola o największym potencjale zwycięstwa/obrony
     */
    public final int wykonajRuch(StringBuffer plansza) {
        wartosciujPola(plansza);
        return wybierzNajlepszyRuch(max(wartosci));
    }

    /**
     * Wybiera najlepszy ruch z pól o najwyższej wartości (potencjale)
     */
    private int wybierzNajlepszyRuch(int maksimum) {
        ArrayList<Integer> listaNajlepszychRuchow = new ArrayList<Integer>();

        listaNajlepszychRuchow.clear();

        for(int i=0; i<9; i++) {
            if (wartosci[i] == maksimum)
                listaNajlepszychRuchow.add(i);
        }

        Random losowyWybor = new Random();
        int wybranyIndex = losowyWybor.nextInt(listaNajlepszychRuchow.size());

        return listaNajlepszychRuchow.get(wybranyIndex);
    }

    /**
     * Wyznacza maksymalną wartość z tablicy
      */
    private int max(int[] tablica) {
        int maksimum = -1;

        for (int t : tablica) {
            if (maksimum < t)
                maksimum = t;
        }

        return maksimum;
    }

    /**
     * Wartościowanie pól do potencjalnego ruchu pod kątem korzyści dla komputera
     */
    private void wartosciujPola(StringBuffer plansza) {
        // 1. wartościuj pola na podstawie możliwej liczby potrójnych ustawień znaku X przechodzących przez pole
        // początkowo pole środkowe posiada wartość +2 (4 możliwe zwycięskie ustawienia), pola narożne +1 (3 możliwe zwycięskie ustawienia), pozostałe pola po 0 (2 możliwe zwycięskie ustawienia)
        for (int i=0; i<9; i++) {
            wartosci[i] = 0;
        }
        wartosci[0] = 1;
        wartosci[2] = 1;
        wartosci[4] = 2; // środkowe
        wartosci[6] = 1;
        wartosci[8] = 1;

        // dla każdego pola badaj możliwe układy linii i dla każdej linii:
        // - ustaw 0, jeśli jest niepuste (oraz ignoruj dalsze warunki dla niepustych pól) lub jeśli pozostałę 2 znaki są puste
        // - wartościuj +1, jeśli przeciwnik posiada jeden znak zagrażający zwycięskim ustawieniem z uwzględnieniem tego pola
        // - wartościuj +1, jeśli postawienie na nim znaku otwiera możliwość zwycięskiego ustawienia przez komputer
        // - wartościuj +5, jeśli pole jest krytyczne do obrony przed zwycięskim ruchem przeciwnika, który posiada już 2 znaki w sąsiedztwie tego pola
        // - wartościuj +10, jeśli ruch na to pole zapewnia zwycięstwo
        for (int i=0; i<9; i++) {
            if (plansza.charAt(i) != '-') {
                wartosci[i] = 0;
            } else {
                // wszystkie pola są badane według wierszy i kolumn:
                wartosci[i] += badajLinie(pobierzWiersz(plansza, i));
                wartosci[i] += badajLinie(pobierzKolumne(plansza, i));

                // wybrane pola są badane pod kątem przekątnych:
                switch (i) {
                    case 0:
                    case 8:
                        wartosci[i] += badajLinie(pobierzPrzekatna1(plansza, i));
                        break;

                    case 2:
                    case 6:
                        wartosci[i] += badajLinie(pobierzPrzekatna2(plansza, i));
                        break;

                    case 4:
                        wartosci[i] += badajLinie(pobierzPrzekatna1(plansza, i));
                        wartosci[i] += badajLinie(pobierzPrzekatna2(plansza, i));
                        break;

                    default:
                }
            }

        }
    }

    /**
     * Bada linię (złożoną z 3 pól), gdzie pole 0 jest zawsze polem o potencjalnym ruchu.
     * Zwraca:
     *  -1, jeśli pierwszy znak jest niepusty
     *   0, jeśli w linii są już postawione 2 znaki obydwu graczy lub dwa pozostałe znaki są puste
     *  +1, jeśli przeciwnik posiada jeden znak w danej linii, a drugi znak jest pusty (obrona przed ruchem przeciwnika)
     *  +1, jeśli w linii występuje jeden znak własny komputera (potencjał zwycięskiego ruchu)
     *  +5, jeśli przeciwnik posiada już 2 znaki w sąsiedztwie tego pola
     *  +10, jeśli ruch na to pole zapewnia zwycięstwo (występują 2 znaki własne w linii)
     */
    private int badajLinie(StringBuffer linia) {

        char[] ch = linia.toString().toCharArray();

        // oczekiwany jest znak pusty na pozycji 0
        if (ch[0] != '-') return -1;

        if ((ch[1] == ZNAK_KOMPUTERA) && (ch[2] == ZNAK_KOMPUTERA)) {
            return 10;
        } else if ((ch[1] == ZNAK_GRACZA) && (ch[2] == ZNAK_GRACZA)) {
            return 5;
        } else if ( ((ch[1] == ZNAK_KOMPUTERA) && (ch[2] == ZNAK_GRACZA))
                    || ((ch[1] == ZNAK_GRACZA) && (ch[2] == ZNAK_KOMPUTERA))
                    || ((ch[1] == '-') && (ch[2] == '-')) ) {
            return 0;
        } else {
            // pozostałe możliwości to: jeden pusty, a drugi gracza/komputera - zwróć 1
            return 1;
        }
     }

    /**
     * Pobiera wiersz i przestawia badane pole na pozycję 0 w celu łatwiejszej analizy
     */
    private StringBuffer pobierzWiersz(StringBuffer plansza, int nrPola) {
        int nrWiersza;
        StringBuffer wiersz;

        if (nrPola <= 2) {
            nrWiersza = 0;
        } else if (nrPola <= 5) {
            nrWiersza = 1;
        } else
            nrWiersza = 2;

        wiersz = new StringBuffer();
        wiersz.append(plansza.charAt(nrPola));
        wiersz.append(plansza.substring(3*nrWiersza, 3*nrWiersza + 3));
        wiersz.deleteCharAt(nrPola - 3*nrWiersza + 1);

        return wiersz;
    }

    /**
     * Pobiera kolumnę i przestawia badane pole na pozycję 0 w celu łatwiejszej analizy
     */
    private StringBuffer pobierzKolumne(StringBuffer plansza, int nrPola) {
        int nrKolumny;
        StringBuffer kolumna;

        if ((nrPola == 0) || (nrPola == 3) || (nrPola == 6)) {
            nrKolumny = 0;
        } else if ((nrPola == 1) || (nrPola == 4) || (nrPola == 7)) {
            nrKolumny = 1;
        } else
            nrKolumny = 2;

        kolumna = new StringBuffer();
        kolumna.append(plansza.charAt(nrPola));
        kolumna.append(plansza.charAt(nrKolumny));
        kolumna.append(plansza.charAt(nrKolumny + 3));
        kolumna.append(plansza.charAt(nrKolumny + 6));

        if (nrPola <=2) {
            kolumna.deleteCharAt(1);
        } else if ((nrPola > 2) && (nrPola <= 5)) {
            kolumna.deleteCharAt(2);
        } else
            kolumna.deleteCharAt(3);

        return kolumna;
    }

    /**
     * Pobiera przekątną [0] + [4] + [8] i przestawia badane pole na pozycję 0 w celu łatwiejszej analizy
     */
    private StringBuffer pobierzPrzekatna1(StringBuffer plansza, int nrPola) {
        StringBuffer przekatna;

        przekatna = new StringBuffer();
        przekatna.append(plansza.charAt(nrPola));
        przekatna.append(plansza.charAt(0));
        przekatna.append(plansza.charAt(4));
        przekatna.append(plansza.charAt(8));

        // usuń znak przeniesiony na pozycję 0
        if ((nrPola % 4 == 0) && (nrPola <= 8))
            przekatna.deleteCharAt(1 + (nrPola/4));

        return przekatna;
    }

    /**
     * Pobiera przekątną [2] + [4] + [6] i przestawia badane pole na pozycję 0 w celu łatwiejszej analizy
     */
    private StringBuffer pobierzPrzekatna2(StringBuffer plansza, int nrPola) {
        StringBuffer przekatna;

        przekatna = new StringBuffer();
        przekatna.append(plansza.charAt(nrPola));
        przekatna.append(plansza.charAt(2));
        przekatna.append(plansza.charAt(4));
        przekatna.append(plansza.charAt(6));

        // usuń znak przeniesiony na pozycję 0
        if ((nrPola == 2) || (nrPola == 4) || (nrPola == 6))
            przekatna.deleteCharAt(nrPola/2);

        return przekatna;
    }
}
