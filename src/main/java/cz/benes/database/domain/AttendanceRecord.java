/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.database.domain;

import java.io.Serializable;

/**
 *
 * @author Beny
 */

// beana pro opravu a prohlížení

public class AttendanceRecord implements Serializable {
    private String login_id;
    private String jmeno;

    private String date;
    private String time;
    private String type;

    public AttendanceRecord(String date, String time, String type) {
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
