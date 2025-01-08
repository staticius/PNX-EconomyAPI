package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;

import java.util.ArrayList;
import java.util.List;

public class FormSendMoney implements Listener {

    public static final int SEND_MONEY_FORM_ID = 12;

    public void sendMoneyForm(Player player) {

        Server server = Server.getInstance();

        List<String> unit = new ArrayList<>();
        List<String> players = new ArrayList<>();
        players.add(server.getOnlinePlayers().toString());
        unit.add("Tam Miktar");
        unit.add("K (Bin)");
        unit.add("M (Milyon)");

        FormWindowCustom formWindowCustom = new FormWindowCustom(TextFormat.BOLD + "Para Gönder");

        formWindowCustom.addElement(new ElementDropdown("Gönderilecek Oyuncu:", players));

        formWindowCustom.addElement(new ElementInput("Miktar", "Gönderilecek Miktar"));

        formWindowCustom.addElement(new ElementDropdown("Birim: ", unit));

        player.showFormWindow(formWindowCustom, SEND_MONEY_FORM_ID);
    }

    @EventHandler
    public void responseHandler(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();

        if (event.getFormID() == SEND_MONEY_FORM_ID) {

            FormWindowCustom window = (FormWindowCustom) event.getWindow();

            if (window == null || event.wasClosed()) {
                player.sendMessage(TextFormat.RED + "X");
                return;
            }

            String selectedPlayer = window.getResponse().getDropdownResponse(0).getElementContent();
            String amount = window.getResponse().getInputResponse(1);
            String unit = window.getResponse().getDropdownResponse(2).getElementContent();

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
            if (unit.equals("K")) {
                parsedAmount *= 1000;
            } else if (unit.equals("M")) {
                parsedAmount *= 1000000;
            }

            EconomyAPI.getInstance().transferMoney(player.getName(), selectedPlayer, parsedAmount);

            player.sendMessage(Prefix.getPrefix() + selectedPlayer + " adlı oyuncuya " + amount + " " + unit + " " + Prefix.getMoneyUnit() + "gönderdin.");

        }
    }

}
