package com.rumaruka.simplycaterpillar.common.handlers;

import com.rumaruka.simplycaterpillar.Caterpillar;
import com.rumaruka.simplycaterpillar.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandlerEvents {

    @SubscribeEvent
    public void onConfigurationChanger(ConfigChangedEvent.OnConfigChangedEvent e){
        if(e.getModID().equals(Caterpillar.MODID)){
            Config.load();
            Config.save();
        }
    }
}
