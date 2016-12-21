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
    private String date;
    private String time;
    private String type;

    public AttendanceRecord(String date, String time, String type) {
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
