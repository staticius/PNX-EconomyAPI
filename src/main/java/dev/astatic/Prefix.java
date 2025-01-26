package dev.astatic;

import cn.nukkit.utils.TextFormat;

public class Prefix {

    public static String getPrefix() {

        final String PREFIX = "§cEconomy§fSystem§f » ";
        return PREFIX;
    }

    public static String getMoneyUnit() {
        final String MONEY_UNIT = TextFormat.GREEN + "Coin";
        return MONEY_UNIT;
    }

}
