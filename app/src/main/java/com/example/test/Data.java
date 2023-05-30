package com.example.test;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("data")
    private AntennaResults antennaResults;

    public Data(AntennaResults antennaResults) {
        this.antennaResults = antennaResults;
    }

    public AntennaResults getAntennaResults() {
        return antennaResults;
    }
}
