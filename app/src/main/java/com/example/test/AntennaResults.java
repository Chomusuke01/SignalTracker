package com.example.test;


import com.google.gson.annotations.SerializedName;

public class AntennaResults {

    @SerializedName("lat")
    private double latitud;

    @SerializedName("lon")
    private double longitud;

    public AntennaResults(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}

