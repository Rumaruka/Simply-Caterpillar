package com.rumaruka.simplycaterpillar.client.tabs;

import com.rumaruka.simplycaterpillar.Caterpillar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TabCaterpillar extends CreativeTabs {
    public TabCaterpillar( ) {
        super(Caterpillar.MODNAME);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.DIAMOND);
    }
}
