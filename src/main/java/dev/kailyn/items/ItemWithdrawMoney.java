package dev.kailyn.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public class ItemWithdrawMoney extends ItemCustom {
    public ItemWithdrawMoney() {
        super("wolfland:para_cek");
    }


    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("paracek")
                .name("Para Çek")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
