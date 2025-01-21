package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FormVaultTopBalance {

    public FakeInventory openFormTopVaultBalance(Player player) throws SQLException {

        FakeInventory topVaultBalanceForm = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Kasa Bakiyesi Sıralaması");

        Item space = Item.get("wolfland:space");
        Item birinci = Item.get("wolfland:baltop_one");
        Item ikinci = Item.get("wolfland:baltop_two");
        Item ucuncu = Item.get("wolfland:baltop_three");
        Item other = Item.get("wolfland:vault_top_money");

        space.setCustomName(" ");

        // İlk 10 zengin kasayı al
        List<String> topVaults = DatabaseManage.getTopVaultBalanceList(10);

        // Envanteri doldur
        for (int i = 0; i < topVaultBalanceForm.getSize(); i++) {
            switch (i) {
                case 4: // Birinci kasa
                    if (topVaults.size() > 0) {
                        setupItemWithMembers(birinci, topVaults.get(0));
                    } else {
                        birinci.setCustomName(TextFormat.DARK_AQUA + "1. ?");
                    }
                    topVaultBalanceForm.setItem(i, birinci);
                    break;

                case 12: // İkinci kasa
                    if (topVaults.size() > 1) {
                        setupItemWithMembers(ikinci, topVaults.get(1));
                    } else {
                        ikinci.setCustomName(TextFormat.DARK_AQUA + "2. ?");
                    }
                    topVaultBalanceForm.setItem(i, ikinci);
                    break;

                case 14: // Üçüncü kasa
                    if (topVaults.size() > 2) {
                        setupItemWithMembers(ucuncu, topVaults.get(2));
                    } else {
                        ucuncu.setCustomName(TextFormat.DARK_AQUA + "3. ?");
                    }
                    topVaultBalanceForm.setItem(i, ucuncu);
                    break;

                case 30:
                case 31:
                case 32:
                case 39:
                case 40:
                case 41:
                case 49: // Diğer kasalar
                    int index = getOtherSlotIndex(i);
                    if (index >= 0 && index + 3 < topVaults.size()) { // İlk 3 kasadan sonraki kasalar
                        setupItemWithMembers(other, topVaults.get(index + 3));
                    } else {
                        other.setCustomName(TextFormat.DARK_AQUA + "" + (index + 4) + ". ?");
                    }
                    topVaultBalanceForm.setItem(i, other.clone());
                    break;

                default: // Boş slotlar
                    topVaultBalanceForm.setItem(i, space);
                    break;
            }
        }

        for (int i = 0; i < topVaultBalanceForm.getSize(); i++) {
            topVaultBalanceForm.setItemHandler(i, (fakeInventory, slot, oldItem, newItem, event) -> {
                event.setCancelled(true);
            });
        }

        return topVaultBalanceForm;
    }

    /**
     * Diğer kasaların sıralama index'lerini verir.
     *
     * @param slot Şu anki slot
     * @return Sıralama index'i veya -1
     */
    private int getOtherSlotIndex(int slot) {
        int[] otherSlots = {30, 31, 32, 39, 40, 41, 49};
        for (int i = 0; i < otherSlots.length; i++) {
            if (otherSlots[i] == slot) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Bir kasayı temsil eden öğeyi ayarlar (ad ve üyeler).
     *
     * @param item  Ayarlanacak öğe
     * @param vault Kasa bilgisi
     */
    private void setupItemWithMembers(Item item, String vault) throws SQLException {
        String[] parts = vault.split(" - ");
        String ownerName = parts[0]; // Kasa sahibi
        String balance = parts[1]; // Kasa bakiyesi

        item.setCustomName(TextFormat.GOLD + ownerName + " - " + TextFormat.GREEN + balance);

        // Üyeleri al ve setLore olarak ekle
        Optional<String> membersJson = DatabaseManage.getVaultMembers(ownerName);
        if (membersJson.isPresent()) {
            List<String> members = DatabaseManage.parseMembers(membersJson.get());
            item.setLore(TextFormat.YELLOW + "Üyeler:", String.join(", ", members));
        } else {
            item.setLore(TextFormat.RED + "Üye bilgisi yok.");
        }
    }
}
