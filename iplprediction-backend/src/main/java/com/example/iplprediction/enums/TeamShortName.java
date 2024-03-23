package com.example.iplprediction.enums;

public enum TeamShortName {
    CSK,
    MI,
    GT,
    KKR,
    DC,
    PBKS,
    LSG,
    SRH,
    RCB,
    RR;

    public static boolean isValidShortName(String shortName) {
        for (TeamShortName teamShortName : TeamShortName.values()) {
            if (teamShortName.name().equalsIgnoreCase(shortName)) {
                return true;
            }
        }
        return false;
    }
}
