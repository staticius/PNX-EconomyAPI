package dev.astatic.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.CreativeCategory;

public class ItemAddMember extends ItemCustom {

    public ItemAddMember() {
        super("wolfland:add_member");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .customBuilder(this)
                .texture("uye_ekle")
                .name("Üye Ekle")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
