package dev.kailyn.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public class ItemSendMoney extends ItemCustom {
    public ItemSendMoney() {
        super("wolfland:para_gonder");
    }


    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("paragonder")
                .name("Para Gönder")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
