package dev.astatic.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.TextFormat;
import dev.astatic.Prefix;
import dev.astatic.api.EconomyAPI;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FormSendMoney implements Listener {

    public static final int SEND_MONEY_FORM_ID = 7;


    @NotNull
    static List<String> getStrings(Player currentPlayer) {
        return FormEcoAdmin.getStrings(currentPlayer);
    }

    public void sendMoneyForm(Player player) {

        Server server = Server.getInstance();

        List<String> unit = new ArrayList<>();
        List<String> players = new ArrayList<>();
        unit.add("Tam Miktar");
        unit.add("K (Bin)");
        unit.add("M (Milyon)");


        FormWindowCustom formWindowCustom = new FormWindowCustom("Para Gönder");

        formWindowCustom.addElement(new ElementDropdown("Gönderilecek Oyuncu:", getStrings(player)));

        formWindowCustom.addElement(new ElementInput("Miktar", "Gönderilecek Miktar"));

        formWindowCustom.addElement(new ElementDropdown("Birim: ", unit));

        player.showFormWindow(formWindowCustom, SEND_MONEY_FORM_ID);
    }

    @EventHandler
    public void responseHandler(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();

        if (event.getFormID() == SEND_MONEY_FORM_ID) {

            FormWindowCustom window = (FormWindowCustom) event.getWindow();

            if (window.getResponse() == null) {
                return;
            }

            String selectedPlayer = window.getResponse().getDropdownResponse(0).getElementContent();
            String amount = window.getResponse().getInputResponse(1);
            String unit = window.getResponse().getDropdownResponse(2).getElementContent();

            if (selectedPlayer.equals("Hiçbir oyuncu mevcut değil.")) {
                return;
            }


            // Miktar doğrulama
            double parsedAmount;
            try {
                parsedAmount = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Geçersiz miktar girdiniz!");
                return;
            }

            if (parsedAmount < 1) {
                player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Gönderilecek miktar sıfırdan büyük olmalıdır!");
                return;
            }

            // Birim işleme
            if (unit.equals("K (Bin)")) {
                parsedAmount *= 1000;
            } else if (unit.equals("M (Milyon)")) {
                parsedAmount *= 1000000;
            }

            double bakiye = EconomyAPI.getInstance().getEconomyManager().getBalance(player.getName());

            if (bakiye >= parsedAmount) {

                EconomyAPI.getInstance().getEconomyManager().transfer(player.getName(), selectedPlayer, parsedAmount);

                player.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + selectedPlayer + TextFormat.DARK_GREEN + " adlı oyuncuya " + TextFormat.GREEN + amount + " " + unit + " " + Prefix.getMoneyUnit() + TextFormat.DARK_GREEN + " gönderdin.");

                // Alıcı oyuncuya mesaj gönderme
                Player receiver = Server.getInstance().getPlayer(selectedPlayer);

                if (receiver != null && receiver.isOnline()) {
                    receiver.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + player.getName() + TextFormat.DARK_GREEN + " adlı oyuncu size " + TextFormat.GREEN + amount + " " + unit + " " + Prefix.getMoneyUnit() + TextFormat.DARK_GREEN + " gönderdi.");
                }


            } else {
                player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Yeterli bakiyeniz bulunmamaktadır, güncel bakiyeniz: " + TextFormat.RED + bakiye + " " + Prefix.getMoneyUnit());
            }

        }
    }

}
