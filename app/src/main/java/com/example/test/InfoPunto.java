package com.example.test;

public class InfoPunto {

    private int dBm;
    private int signalStrength;

    public InfoPunto(int dBm, int signalStrength) {
        this.dBm = dBm;
        this.signalStrength = signalStrength;
    }

    public int getdBm() {
        return dBm;
    }

    public void setdBm(int dBm) {
        this.dBm = dBm;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public int getSignalStrength() {
        return signalStrength;
    }
}
