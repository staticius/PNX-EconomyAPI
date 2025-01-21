package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static dev.kailyn.database.DatabaseManage.getVaultOwner;

public class FormVaultMemberManage implements Listener {

    public static FakeInventory vaultMemberManageGUI(Player player) {

        FakeInventory vaultMemberManageInventory = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Ortak Kasa");

        Item space = Item.get("wolfland:space");
        Item paraCek = Item.get("wolfland:para_cek");
        Item paraYatir = Item.get("wolfland:para_yatir");
        Item balance = Item.get("wolfland:para_bak");
        Item seeMembers = Item.get("wolfland:soruisareti");
        Item quitVault = Item.get(Block.IRON_DOOR);
        Item vaultOwner = Item.get("wolfland:tac");

        space.setCustomName(" ");
        quitVault.setCustomName(TextFormat.AQUA + "Kasadan Ayrıl");
        seeMembers.setCustomName(TextFormat.AQUA + "Kasadaki Oyuncular");
        paraCek.setCustomName(TextFormat.AQUA + "Para Çek");
        paraYatir.setCustomName(TextFormat.AQUA + "Para Yatır");
        balance.setCustomName(TextFormat.AQUA + "Toplam Kasa Bakiyesi");
        vaultOwner.setCustomName(TextFormat.AQUA + "Kasa Sahibi");

        seeMembers.setLore("WolfLand SkyBlock");
        paraCek.setLore("WolfLand SkyBlock");
        paraYatir.setLore("WolfLand SkyBlock");
        quitVault.setLore("WolfLand SkyBlock");

        try {
            Optional<String> optionalOwner = getVaultOwner(player.getName());

            if (optionalOwner.isPresent()) {
                String ownerName = optionalOwner.get();
                vaultOwner.setLore(TextFormat.GOLD + ownerName);
                String balanceST = DatabaseManage.formatNumber(EconomyAPI.getInstance().getVaultManager().getVaultTotalBalance(optionalOwner.get()));
                balance.setLore(TextFormat.GOLD + balanceST);
            } else {
                balance.setLore(TextFormat.GOLD + "Bakiye Alınamadı.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        vaultMemberManageInventory.setItem(4, seeMembers);
        vaultMemberManageInventory.setItem(11, paraCek);
        vaultMemberManageInventory.setItem(15, paraYatir);
        vaultMemberManageInventory.setItem(22, balance);
        vaultMemberManageInventory.setItem(48, vaultOwner);
        vaultMemberManageInventory.setItem(50, quitVault);
        vaultMemberManageInventory.setItem(49, space);

        for (int i = 0; i < 4; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }
        for (int i = 5; i < 11; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }
        for (int i = 12; i < 15; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }
        for (int i = 16; i < 22; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }
        for (int i = 23; i < 48; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }
        for (int i = 51; i <= 53; i++) {
            vaultMemberManageInventory.setItem(i, space);
        }

        for (int i = 0; i < vaultMemberManageInventory.getSize(); i++) {
            vaultMemberManageInventory.setItemHandler(i, (fakeInventory, slot, oldItem, newItem, event) -> {
                event.setCancelled(true);

                Item clickedItem = fakeInventory.getItem(slot);


                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Kasadaki Oyuncular")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {
                        FormSeeVaultMembers formSeeVaultMembers = new FormSeeVaultMembers();

                        try {
                            formSeeVaultMembers.sendVaultFormDetailsForMember(player);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }, 5);
                }

                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Kasadan Ayrıl")){

                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormQuitVault.openQuitVaultForm(player);

                    }, 5);
                }

                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Para Çek")) {

                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormWithdrawVault.withdrawVaultForMember(player);

                    }, 5);
                }

                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Para Yatır")) {

                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormDepositVault.formDepositVault(player);

                    }, 5);

                }


            });
        }

        return vaultMemberManageInventory;
    }

}
