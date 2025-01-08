package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.inventory.fake.ItemHandler;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;

import java.util.ArrayList;
import java.util.List;

public class FormMenu implements Listener {

    public static FakeInventory menuGUI(Player player) {

        FakeInventory fakeInventory = new FakeInventory(FakeInventoryType.HOPPER, "Ekonomi Menüsü");

        Item sendMoney = Item.get(Item.ARROW);
        Item createVault = Item.get("wolfland:banka_img");
        Item seeMoney = Item.get(Item.AMETHYST_SHARD);
        Item space = Item.get(Item.STAINED_GLASS_PANE);

        sendMoney.setCustomName(TextFormat.YELLOW + "Bakiye Gönder");
        createVault.setCustomName(TextFormat.YELLOW + "Ortak Kasa Oluştur");
        seeMoney.setCustomName(TextFormat.YELLOW + "Bakiyeni Gör");
        space.setCustomName(" ");

        sendMoney.setLore("WolfLand SkyBlock");
        seeMoney.setLore("WolfLand SkyBlock");
        createVault.setLore("WolfLand SkyBlock");

        fakeInventory.setItem(0, space);
        fakeInventory.setItem(1, sendMoney);
        fakeInventory.setItem(2, seeMoney);
        fakeInventory.setItem(3, createVault);
        fakeInventory.setItem(4, space);

        for (int i = 0; i < fakeInventory.getSize(); i++) {
            fakeInventory.setItemHandler(i, new ItemHandler() {
                @Override
                public void handle(FakeInventory fakeInventory, int slot, Item oldItem, Item newItem, ItemStackRequestActionEvent event) {
                    event.setCancelled(true);

                    // Tıklanan item
                    Item clickedItem = fakeInventory.getItem(slot);

                    // Özel ad kontrolü
                    if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Ortak Kasa Oluştur")) {
                        fakeInventory.close(player);

                        // Online oyuncuları al
                        //List<String> onlinePlayers = getOnlinePlayerNames(player);

                        player.getServer().getScheduler().scheduleDelayedTask(() -> {
                            FormVaultCreate formVaultCreate = new FormVaultCreate();
                            formVaultCreate.open(player);
                        }, 5);

                        // Formu aç
                        FormVaultCreate formVaultCreate = new FormVaultCreate();
                        formVaultCreate.open(player);
                    }

                    if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Bakiyeni Gör")) {
                        fakeInventory.close(player);
                        player.sendMessage(Prefix.getPrefix() + EconomyAPI.getInstance().getBalance(player.getName()));
                    }

                    if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Bakiye Gönder")) {
                        fakeInventory.close(player);

                        player.getServer().getScheduler().scheduleDelayedTask(() -> {
                            FormSendMoney formSendMoney = new FormSendMoney();
                            formSendMoney.sendMoneyForm(player);
                        }, 5);

                    }

                    if (clickedItem.getId() == null) {
                        fakeInventory.close(player);
                        player.sendMessage("Sıkıntı büyük");
                    }
                }
            });
        }

        return fakeInventory;

    }

    private static List<String> getOnlinePlayerNames(Player currentPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : currentPlayer.getServer().getOnlinePlayers().values()) {
            if (!player.equals(currentPlayer)) {
                playerNames.add(player.getName());
            }
        }
        if (playerNames.isEmpty()) {
            playerNames.add("Hiçbir oyuncu mevcut değil");
        }
        return playerNames;
    }

}