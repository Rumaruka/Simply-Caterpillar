package com.rumaruka.simplycaterpillar.timers;

import com.rumaruka.simplycaterpillar.Caterpillar;
import com.rumaruka.simplycaterpillar.Const;
import net.minecraft.client.Minecraft;

import java.util.TimerTask;

public class TimerMain extends TimerTask {

    public boolean inSetup = false;
    private boolean onlyrunOnce = false;
    private boolean savedyet = false;
    private void checkReady() {
        if (Caterpillar.proxy.getWorld() != null )
        {
            if (Caterpillar.proxy.getWorld().loadedEntityList != null)
            {
                if (Caterpillar.proxy.getWorld().loadedEntityList.size() > 0)
                {
                    if (!Const.loaded)
                    {
                        if (!this.inSetup)
                        {
                            this.inSetup = true;
                            this.worldLoadedfromMod();
                            Const.loaded = true;
                            Const.printDebug("Mod is running!");
                        }
                    }

                }
            }
        }
    }

    @Override
    public void run() {
        if (!this.onlyrunOnce)
        {
            this.onlyrunOnce = true;
        }

        this.checkReady();


        if (Const.loaded)
        {
            if (Const.checkLoaded())
            {
                Caterpillar.instance.reset();
                return;

            }
            this.runningTickfromMod();
        }

    }

    public void runningTickfromMod()
    {
        if (Const.loaded)
        {
            if (!Caterpillar.proxy.isServerSide())
            {
                if (Minecraft.getMinecraft().currentScreen != null)
                {
                    if (!this.savedyet)
                    {
                        this.savedyet = true;
                        Const.printDebug("Menu: Saving...");
                        Caterpillar.instance.saveNBTDrills();
                    }
                }
                else
                {
                    this.savedyet = false;
                }
            }

            Caterpillar.instance.saveCount++;
            if (Caterpillar.instance.saveCount > 6000)
            {
                Caterpillar.instance.saveCount = 0;
            }
        }
    }
    public void worldLoadedfromMod() {
        Const.printDebug("World loaded, starting mod!");

        Caterpillar.instance.readNBTDrills();

        Const.printDebug("World loaded, finished!");
    }
}
