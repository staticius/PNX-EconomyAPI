package dev.kailyn.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public class ItemBaltopThree extends ItemCustom {
    public ItemBaltopThree() {
        super("wolfland:baltop_three");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("baltop_ucuncu")
                .name("Üçüncü")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
