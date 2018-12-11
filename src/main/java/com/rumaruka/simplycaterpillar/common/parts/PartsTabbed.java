package com.rumaruka.simplycaterpillar.common.parts;

import net.minecraft.nbt.NBTTagCompound;

public class PartsTabbed {

    public int howClose = 0;
    public PartsTabbed(){

    }
    public void readNBT(NBTTagCompound nbtTagCompound){
        if(nbtTagCompound.hasKey("howClose")){
            this.howClose=nbtTagCompound.getInteger("howClose");
        }
        else {
            this.howClose=2;
        }

    }
    public NBTTagCompound saveNBT(){
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger("howClose",this.howClose);
        return tagCompound;
    }

    public NBTTagCompound saveNBT(NBTTagCompound compound){
        compound.setInteger("howClose",this.howClose);
        return compound;
    }
}

