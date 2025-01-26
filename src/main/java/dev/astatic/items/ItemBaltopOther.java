package dev.astatic.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;

public class ItemBaltopOther extends ItemCustom {
    public ItemBaltopOther() {
        super("wolfland:baltop_other");
    }


    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("baltop_other")
                .name("Baltop Other")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
