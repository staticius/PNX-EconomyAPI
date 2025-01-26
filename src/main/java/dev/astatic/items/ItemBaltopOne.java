package dev.astatic.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;

public class ItemBaltopOne extends ItemCustom {
    public ItemBaltopOne() {
        super("wolfland:baltop_one");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.customBuilder(this).texture("baltop_birinci").name("Baltop Birinci").allowOffHand(false).creativeCategory(CreativeCategory.ITEMS).build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
