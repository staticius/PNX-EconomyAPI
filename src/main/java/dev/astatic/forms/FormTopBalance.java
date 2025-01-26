package dev.astatic.forms;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManage;

import java.sql.SQLException;
import java.util.List;

public class FormTopBalance {

        public FakeInventory openFormTopBalance(Player player) throws SQLException {

            FakeInventory topBalanceForm = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Bakiye Sıralaması");

            Item space = Item.get("wolfland:space");
            Item birinci = Item.get("wolfland:baltop_one");
            Item ikinci = Item.get("wolfland:baltop_two");
            Item ucuncu = Item.get("wolfland:baltop_three");
            Item other = Item.get("wolfland:baltop_other");

            space.setCustomName(" ");

            // İlk 10 zengin oyuncuyu al
            List<String> topRichs = DatabaseManage.getTopBalanceList(10);

            // Envanteri doldur
            for (int i = 0; i < topBalanceForm.getSize(); i++) {
                switch (i) {
                    case 4: // Birinci oyuncunun slotu
                        if (topRichs.size() > 0) {
                            birinci.setCustomName(TextFormat.GOLD + "1. " + topRichs.get(0)); // İlk oyuncunun adını ekle
                        } else {
                            birinci.setCustomName(TextFormat.DARK_AQUA + "1. ?");
                        }
                        topBalanceForm.setItem(i, birinci);
                        break;

                    case 12: // İkinci oyuncunun slotu
                        if (topRichs.size() > 1) {
                            ikinci.setCustomName(TextFormat.YELLOW + "2. " + topRichs.get(1)); // İkinci oyuncunun adını ekle
                        } else {
                            ikinci.setCustomName(TextFormat.DARK_AQUA + "2. ?");
                        }
                        topBalanceForm.setItem(i, ikinci);
                        break;

                    case 14: // Üçüncü oyuncunun slotu
                        if (topRichs.size() > 2) {
                            ucuncu.setCustomName(TextFormat.YELLOW + "3. " + topRichs.get(2)); // Üçüncü oyuncunun adını ekle
                        } else {
                            ucuncu.setCustomName(TextFormat.DARK_AQUA + "3. ?");
                        }
                        topBalanceForm.setItem(i, ucuncu);
                        break;

                    case 30:
                    case 31:
                    case 32:
                    case 39:
                    case 40:
                    case 41:
                    case 49: // Diğer oyuncular için slotlar
                        int index = getOtherSlotIndex(i);
                        if (index >= 0 && index + 3 < topRichs.size()) { // İlk 3 oyuncudan sonraki oyuncular
                            other.setCustomName(TextFormat.MATERIAL_QUARTZ + "" + (index + 4) + ". " + topRichs.get(index + 3)); // 4. sıradan başlayarak ekle
                        } else {
                            other.setCustomName(TextFormat.MATERIAL_QUARTZ + "" + (index + 4) + ". ?");
                        }
                        topBalanceForm.setItem(i, other.clone()); // `clone()` kullanılarak farklı öğeler oluşturulur
                        break;

                    default: // Boş slotlar
                        topBalanceForm.setItem(i, space);
                        break;
                }
            }

            for (int i = 0; i < topBalanceForm.getSize(); i++) {
                topBalanceForm.setItemHandler(i, (fakeInventory, slot, oldItem, newItem, event) -> {
                    event.setCancelled(true);
                });
            }


            return topBalanceForm;
        }

        /**
         * Diğer oyuncuların sıralama index'lerini verir.
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
    }

