package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static dev.kailyn.forms.FormEcoAdmin.getStrings;

public class FormEcoMenu implements Listener {

    public static FakeInventory menuGUI(Player player) {

        FakeInventory fakeInventory = new FakeInventory(FakeInventoryType.HOPPER, "Ekonomi Menüsü");

        Item sendMoney = Item.get(Item.ARROW);
        Item createVault = Item.get("wolfland:banka_img");
        Item seeMoney = Item.get("wolfland:player_money");
        Item space = Item.get("wolfland:space");

        sendMoney.setCustomName(TextFormat.YELLOW + "Bakiye Gönder");
        createVault.setCustomName(TextFormat.YELLOW + "Kasa İşlemleri");
        seeMoney.setCustomName(TextFormat.YELLOW + "Bakiyeni Gör");

        space.setCustomName("WolfLand SkyBlock");
        sendMoney.setLore("WolfLand SkyBlock");
        seeMoney.setLore("WolfLand SkyBlock");
        createVault.setLore("WolfLand SkyBlock");

        fakeInventory.setItem(0, space);
        fakeInventory.setItem(1, sendMoney);
        fakeInventory.setItem(2, seeMoney);
        fakeInventory.setItem(3, createVault);
        fakeInventory.setItem(4, space);

        for (int i = 0; i < fakeInventory.getSize(); i++) {
            fakeInventory.setItemHandler(i, (fakeInventory1, slot, oldItem, newItem, event) -> {
                event.setCancelled(true);

                // Tıklanan item
                Item clickedItem = fakeInventory1.getItem(slot);

                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Kasa İşlemleri")) {

                    fakeInventory1.close(player);

                    try {

                        boolean kasasiVarMi = DatabaseManage.isPlayerInAnyVault(player.getName());


                        if (DatabaseManage.isVaultOwner(player.getName())) {

                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Sahibi olduğunuz kasaya yönlendiriliyorsunuz.");

                            player.getServer().getScheduler().scheduleDelayedTask(() -> {

                                player.addWindow(FormVaultManage.vaultManageGUI(player));

                            }, 15);
                        }


                        if (DatabaseManage.isVaultMember(player.getName())) {

                            Optional<String> optionalOwnr = DatabaseManage.getVaultOwner(player.getName());

                            if (optionalOwnr.isPresent()) {
                                String owner = optionalOwnr.get();
                                player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Üyesi olduğunuz kasaya yönlendiriliyorsunuz, kasanın sahibi: " + TextFormat.GREEN + owner);

                            } else {
                                player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Kasa sahibi bulunamadı!");
                            }

                            player.getServer().getScheduler().scheduleDelayedTask(() -> {

                                player.addWindow(FormVaultMemberManage.vaultMemberManageGUI(player));

                            }, 15);


                        } else if (!DatabaseManage.isPlayerInAnyVault(player.getName()) && !DatabaseManage.isVaultOwner(player.getName())) {

                            fakeInventory1.close(player);

                            player.getServer().getScheduler().scheduleDelayedTask(() -> {
                                FormVaultCreate.open(player);
                            }, 15);
                        }


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Bakiyeni Gör")) {
                    fakeInventory1.close(player);
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Mevcut bakiyeniz: " + TextFormat.GREEN + DatabaseManage.formatNumber(EconomyAPI.getInstance().getBalance(player.getName())) + TextFormat.DARK_GREEN + " Wolf Coin.");
                } else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.YELLOW + "Bakiye Gönder")) {
                    fakeInventory1.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {
                        FormSendMoney formSendMoney = new FormSendMoney();
                        formSendMoney.sendMoneyForm(player);
                    }, 5);

                }

                if (clickedItem.getId() == null) {
                    fakeInventory1.close(player);
                    player.sendMessage("Sıkıntı büyük");
                }
            });
        }

        return fakeInventory;

    }

    private static List<String> getOnlinePlayerNames(Player currentPlayer) {
        return getStrings(currentPlayer);
    }

}