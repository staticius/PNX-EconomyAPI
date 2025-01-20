package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;

public class FormVaultManage implements Listener {

    public static FakeInventory vaultManageGUI(Player player) {

        FakeInventory vaultManageInventory = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Kasa Yönetim Paneli");

        Item space = Item.get("wolfland:space");
        Item addMember = Item.get("wolfland:add_member");
        Item removeMember = Item.get("wolfland:remove_member");
        Item paraCek = Item.get("wolfland:para_cek");
        Item paraYatir = Item.get("wolfland:para_yatir");
        Item balance = Item.get("wolfland:para_bak");
        Item seeMembers = Item.get("wolfland:soruisareti");
        Item deleteVault = Item.get(Block.REDSTONE_BLOCK);

        space.setCustomName(" ");
        deleteVault.setCustomName(TextFormat.RED + "Kasayı Sil");
        seeMembers.setCustomName(TextFormat.AQUA + "Kasadaki Oyuncular");
        removeMember.setCustomName(TextFormat.RED + "Oyuncu Çıkar");
        addMember.setCustomName(TextFormat.GREEN + "Oyuncu Ekle");
        paraCek.setCustomName(TextFormat.AQUA + "Para Çek");
        paraYatir.setCustomName(TextFormat.AQUA + "Para Yatır");
        balance.setCustomName(TextFormat.AQUA + "Toplam Kasa Bakiyesi");

        seeMembers.setLore("WolfLand SkyBlock");
        deleteVault.setLore("WolfLand SkyBlock");
        addMember.setLore("WolfLand SkyBlock");
        removeMember.setLore("WolfLand SkyBlock");
        paraCek.setLore("WolfLand SkyBlock");
        paraYatir.setLore("WolfLand SkyBlock");

        try {
            String balanceST = DatabaseManage.formatNumber(EconomyAPI.getInstance().getVaultManager().getVaultTotalBalance(player.getName()));
            balance.setLore(TextFormat.GOLD + balanceST);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        vaultManageInventory.setItem(4, seeMembers);
        vaultManageInventory.setItem(10, addMember);
        vaultManageInventory.setItem(12, removeMember);
        vaultManageInventory.setItem(14, paraCek);
        vaultManageInventory.setItem(16, paraYatir);
        vaultManageInventory.setItem(40, balance);

        vaultManageInventory.setItem(11, space);
        vaultManageInventory.setItem(13, space);
        vaultManageInventory.setItem(15, space);
        vaultManageInventory.setItem(53, deleteVault);


        for (int i = 0; i < 4; i++) {
            vaultManageInventory.setItem(i, space);
        }

        for (int i = 5; i < 10; i++) {
            vaultManageInventory.setItem(i, space);
        }

        for (int i = 17; i < 40; i++) {
            vaultManageInventory.setItem(i, space);
        }

        for (int i = 41; i <= 52; i++) {
            vaultManageInventory.setItem(i, space);
        }


        for (int i = 0; i < vaultManageInventory.getSize(); i++) {
            vaultManageInventory.setItemHandler(i, (fakeInventory, slot, oldItem, newItem, event) -> {
                event.setCancelled(true);

                Item clickedItem = fakeInventory.getItem(slot);

                if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Kasadaki Oyuncular")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {
                        FormSeeVaultMembers formSeeVaultMembers = new FormSeeVaultMembers();
                        try {
                            formSeeVaultMembers.sendVaultFormDetails(player);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }, 5);
                }

                else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.GREEN + "Oyuncu Ekle")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormAddVaultMember.openAddMemberMenu(player);

                    }, 5);
                }

                else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.RED + "Oyuncu Çıkar")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormRemoveVaultMember.openRemoveVaultMemberForm(player);

                    }, 5);
                } else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.RED + "Kasayı Sil")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormDeleteVault.deleteVault(player);

                    }, 5);

                } else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Para Yatır")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormDepositVault.formDepositVault(player);

                    }, 5);

                } else if (clickedItem.hasCustomName() && clickedItem.getCustomName().equals(TextFormat.AQUA + "Para Çek")) {
                    fakeInventory.close(player);

                    player.getServer().getScheduler().scheduleDelayedTask(() -> {

                        FormWithdrawVault.withdrawVault(player);

                    }, 5);

                }

            });
        }

        return vaultManageInventory;
    }


}
