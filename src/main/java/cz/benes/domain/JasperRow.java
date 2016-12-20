/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.domain;

/**
 *
 * @author Beny
 */

// beana pro generování přehledu
public class JasperRow {
    private String day;
    private String prichod;
    private String odchod;
    private String pauza;
    private String odpracovano;
    private String poznamka;
    

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPrichod() {
        return prichod;
    }

    public void setPrichod(String prichod) {
        this.prichod = prichod;
    }

    public String getOdchod() {
        return odchod;
    }

    public void setOdchod(String odchod) {
        this.odchod = odchod;
    }

    public String getPauza() {
        return pauza;
    }

    public void setPauza(String pauza) {
        this.pauza = pauza;
    }

    public String getOdpracovano() {
        return odpracovano;
    }

    public void setOdpracovano(String odpracovano) {
        this.odpracovano = odpracovano;
    }
    
    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
    
    public JasperRow(String day, String prichod, String odchod, String pauza, String odpracovano, String poznamka) {
        this.day = day;
        this.prichod = prichod;
        this.odchod = odchod;
        this.pauza = pauza;
        this.odpracovano = odpracovano;
        this.poznamka = poznamka;
    }

    public JasperRow(String day, String poznamka) {
        this.day = day;
        this.poznamka = poznamka;
    }
    
  
    

}
