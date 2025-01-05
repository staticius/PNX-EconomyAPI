package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.inventory.fake.ItemHandler;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public class FormMenu implements Listener {

    public static FakeInventory menuGUI(Player player) {

        FakeInventory fakeInventory = new FakeInventory(FakeInventoryType.HOPPER, "Ekonomi Menüsü");

        Item sendMoney = Item.get(Item.ARROW);
        Item createVault = Item.get(Item.HEART_OF_THE_SEA);
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
                    if (fakeInventory.getTitle().contains("Ekonomi Menüsü")) {
                        event.setCancelled(true);
                    }
                }
            });


        }

        return fakeInventory;

    }
}