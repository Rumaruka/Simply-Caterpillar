package com.rumaruka.simplycaterpillar.proxy;

import net.minecraft.util.ResourceLocation;

public class PartsTexture {
    public ResourceLocation guiTexture;
    public int Height;
    public String Name;
    public int Width;
    public int X;
    public int Y;
    public PartsTexture (String Name, ResourceLocation guiTexture, int X, int Y, int Width, int Height)
    {
        this.Name = Name;
        this.guiTexture = guiTexture;
        this.Y = Y;
        this.X = X;
        this.Height = Height;
        this.Width = Width;
    }

}
