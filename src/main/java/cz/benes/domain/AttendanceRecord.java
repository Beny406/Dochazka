/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.domain;

import java.io.Serializable;

/**
 *
 * @author Beny
 */

// beana pro opravu a prohlížení

public class AttendanceRecord implements Serializable {
    private String date;
    private String time;
    private String in_out;

    public AttendanceRecord(String date, String time, String in_out) {
        this.date = date;
        this.time = time;
        this.in_out = in_out;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getIn_out() {
        return in_out;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIn_out(String in_out) {
        this.in_out = in_out;
    }
    
    
}
