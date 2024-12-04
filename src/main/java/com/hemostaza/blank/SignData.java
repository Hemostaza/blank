package com.hemostaza.blank;
import net.md_5.bungee.api.ChatColor;

public class SignData {
    static final String HEADER_WARP = "[HOME]";

    private String header;
    public String warpName;
    public String warpNameSuf;

    public SignData(String[] lines) {
        header = ChatColor.stripColor(lines[0]);
        warpName = lines[1];
        warpNameSuf = lines[2];
    }

    public Boolean isValidHomeName() {
        return warpName != null && !warpName.isEmpty();
    }

    Boolean isHome() {
        return header.equalsIgnoreCase(HEADER_WARP);
    }

    public Boolean isHomeSign() {
        return isHome();
    }
}