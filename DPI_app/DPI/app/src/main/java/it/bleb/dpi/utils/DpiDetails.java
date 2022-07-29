package it.bleb.dpi.utils;

import java.io.Serializable;

public class DpiDetails implements Serializable {

    private String address;
    private String name;
    private int batteryLvl;
    private boolean status;
    private boolean isAlarmSended;
    private boolean signalIntercepted;
    private int moving;

    public DpiDetails() {
    }

    public DpiDetails(String address, String name) {
        this.address = address;
        this.name = name;
        batteryLvl = -1;
        status = false;
        isAlarmSended = false;
        signalIntercepted = false;
        moving = 0;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(int batteryLvl) {
        this.batteryLvl = batteryLvl;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isAlarmSended() {
        return isAlarmSended;
    }

    public void setAlarmSended(boolean alarmSended) {
        isAlarmSended = alarmSended;
    }

    public boolean isSignalIntercepted() {
        return signalIntercepted;
    }

    public void setSignalIntercepted(boolean signalIntercepted) {
        this.signalIntercepted = signalIntercepted;
    }

    public int getMoving() {
        return moving;
    }

    public void setMoving(int moving) {
        this.moving = moving;
    }
}


