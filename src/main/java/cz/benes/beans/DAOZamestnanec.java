package cz.benes.beans;

import java.io.Serializable;

public class DAOZamestnanec implements Serializable{
    private String jmeno;
    private String login_id;
    private String heslo;
    private double uvazek;
    private boolean admin;   

    public DAOZamestnanec() {
    }
    
    public DAOZamestnanec(String jmeno, String login_id) {
        this.jmeno = jmeno;
        this.login_id = login_id;
    }

    public DAOZamestnanec(String login_id, String jmeno, String heslo, String uvazek, String prava) {
        this.jmeno = jmeno;
        this.login_id = login_id;
        this.heslo = heslo;
        setUvazek(uvazek);
        this.admin = prava.equals("1");
    }

    public DAOZamestnanec(DAOZamestnanec zamestnanec) {
        this.login_id = zamestnanec.getLogin_id();
        this.jmeno = zamestnanec.getJmeno();
        this.heslo = zamestnanec.getHeslo();
        this.uvazek = zamestnanec.getUvazek();
        this.admin = zamestnanec.getAdmin();
    }

    
    
    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getHeslo() {
        return heslo;
    }

    public void setHeslo(String heslo) {
        this.heslo = heslo;
    }

    public double getUvazek() {
        return uvazek;
    }

    public void setUvazek(String uvazek) {
        this.uvazek = Double.parseDouble(uvazek);
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    
    
    @Override
    public String toString() {
        return jmeno + " (" + login_id + ")"; //To change body of generated methods, choose Tools | Templates.
    }

  
    
    
    
    
    
}
