package com.rumaruka.simplycaterpillar.proxy;

import com.rumaruka.simplycaterpillar.init.SCBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.File;

public class ClientProxy extends CommonProxy {

    @Override
    public boolean checkLoaded()
    {
        return Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu || this.getWorld() == null;
    }
    @Override
    public File getDataDir(){
        return Minecraft.getMinecraft().mcDataDir;
    }
    @Override
    public EntityPlayer getPlayer(){
        return Minecraft.getMinecraft().player;
    }
    @Override
    public World getWorld(){
        return Minecraft.getMinecraft().world;
    }
    @Override
    public boolean isServerSide()
    {
        return false;
    }
    @Override
    public void registerRenders()
    {
        SCBlocks.renderBlocks();
    }
    @Override
    public String translateToLocal(String s){
        return I18n.format(s);
    }
}
