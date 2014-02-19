package com.plnyyanks.frcvolhelper.datatypes;

/**
 * Created by phil on 2/19/14.
 */
public class Match {
    private String  matchKey,
                    matchType;
    private int     matchNumber,
                    blueAlliance[],
                    redAlliance[];

    public Match(String matchKey, String matchType, int matchNumber, int[] blueAlliance, int[] redAlliance) {
        this.matchKey = matchKey;
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.blueAlliance = blueAlliance;
        this.redAlliance = redAlliance;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int[] getBlueAlliance() {
        return blueAlliance;
    }

    public void setBlueAlliance(int[] blueAlliance) {
        this.blueAlliance = blueAlliance;
    }

    public int[] getRedAlliance() {
        return redAlliance;
    }

    public void setRedAlliance(int[] redAlliance) {
        this.redAlliance = redAlliance;
    }
}
