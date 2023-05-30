
package com.example.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Etapa {

    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("dBmMax")
    @Expose
    private int dBmMax;
    @SerializedName("dBmMin")
    @Expose
    private int dBmMin;
    @SerializedName("MaxSignalLevel")
    @Expose
    private int maxSignalLevel;
    @SerializedName("MinSignalLevel")
    @Expose
    private int minSignalLevel;
    @SerializedName("dBmMean")
    @Expose
    private int dBmMean;
    @SerializedName("signalMean")
    @Expose
    private int signalMean;

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getdBmMax() {
        return dBmMax;
    }

    public void setdBmMax(int dBmMax) {
        this.dBmMax = dBmMax;
    }

    public int getdBmMin() {
        return dBmMin;
    }

    public void setdBmMin(int dBmMin) {
        this.dBmMin = dBmMin;
    }

    public int getMaxSignalLevel() {
        return maxSignalLevel;
    }

    public void setMaxSignalLevel(int maxSignalLevel) {
        this.maxSignalLevel = maxSignalLevel;
    }

    public int getMinSignalLevel() {
        return minSignalLevel;
    }

    public void setMinSignalLevel(int minSignalLevel) {
        this.minSignalLevel = minSignalLevel;
    }

    public int getdBmMean() {
        return dBmMean;
    }

    public void setdBmMean(int dBmMean) {
        this.dBmMean = dBmMean;
    }

    public int getSignalMean() {
        return signalMean;
    }

    public void setSignalMean(int signalMean) {
        this.signalMean = signalMean;
    }

    public JsonElement toJson(){
        return gson.toJsonTree(this);
    }
}
