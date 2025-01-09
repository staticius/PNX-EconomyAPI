package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;

public class FormVaultManage {

    public static FakeInventory vaultManageGUI (Player player) {

        EconomyAPI.getInstance().getEconomyManager().getBalance()

        FakeInventory vaultManageInventory = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Kasa");

        Item space = Item.get("wolfland:space");
        Item addMember = Item.get("wolfland:add_member");
        Item removeMember = Item.get("wolfland:remove_member");
        Item paraCek = Item.get(Item.PAPER);
        Item paraYatir = Item.get(Item.WRITABLE_BOOK);
        Item balance = Item.get("wolfland:banka_img");

        space.setCustomName(" ");
        addMember.setCustomName(TextFormat.YELLOW + "Oyuncu Ekle");
        removeMember.setCustomName(TextFormat.RED + "Oyuncu Çıkar");
        paraCek.setCustomName(TextFormat.AQUA + "Para Çek");
        paraYatir.setCustomName(TextFormat.AQUA + "Para Yatır");
        balance.setCustomName("")

        return vaultManageInventory;
    }



}
