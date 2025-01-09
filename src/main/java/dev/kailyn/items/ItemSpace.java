package dev.kailyn.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public class ItemSpace extends ItemCustom {
    public ItemSpace(@NotNull String id) {
        super("wolfland:space");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("space")
                .name("Boşluk")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
