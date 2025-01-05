package dev.kailyn.api;

import java.util.List;

public class EconomyAPI {

    // Ekonomi İşlemleri

    public boolean transfer(String sender, String receiver, double amount) {
        return false;
    }

    public double getBalance(String playerName) {
        return 0;
    }

    public boolean deposit(String playerName, double amount) {
        return false;
    }

    public boolean withdraw(String playerName, double amount) {
        return false;
    }

    // Ortak Kasa İşlemleri

    public boolean createVault(String owner, List<String> members) {
        return false;
    }

    public double getVaultBalance(String owner) {
        return 0;
    }

    public boolean addVaultBalance(String owner, double amount) {
        return false;
    }

    public List<String> getVaultMembers(String owner) {
        return null;
    }

}
