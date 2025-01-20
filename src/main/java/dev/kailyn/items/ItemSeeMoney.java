package dev.kailyn.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;

public class ItemSeeMoney extends ItemCustom {
    public ItemSeeMoney() {
        super("wolfland:para_bak");
    }


    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("parabak")
                .name("Para Bak")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
