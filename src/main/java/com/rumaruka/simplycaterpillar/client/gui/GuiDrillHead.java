package com.rumaruka.simplycaterpillar.client.gui;

import com.rumaruka.simplycaterpillar.Caterpillar;
import com.rumaruka.simplycaterpillar.Caterpillar.GuiTabs;
import com.rumaruka.simplycaterpillar.Caterpillar.Replacement;
import com.rumaruka.simplycaterpillar.Config;
import com.rumaruka.simplycaterpillar.Const;
import com.rumaruka.simplycaterpillar.abstracts.AbstractRunnerWidgets;
import com.rumaruka.simplycaterpillar.common.containers.ContainerDrillHead;
import com.rumaruka.simplycaterpillar.common.parts.PartsGuiWidgets;
import com.rumaruka.simplycaterpillar.common.tileentity.TileEntityDrillHead;
import com.rumaruka.simplycaterpillar.proxy.PartsTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class GuiDrillHead  extends GuiContainer
    {
        private TileEntityDrillHead caterpillar;
        private HashMap<GuiTabs, List<PartsGuiWidgets>> widgetsHolder;
        private List<PartsGuiWidgets> selectedWidgets;
        private PartsTutorial howTut;
        private GuiButton left;
        private GuiButton right;
	public GuiDrillHead(EntityPlayer player, TileEntityDrillHead drillTe)
        {
            super(new ContainerDrillHead(player, drillTe));
            this.widgetsHolder = new HashMap<>();
            this.setupWidgets();
            this.caterpillar = drillTe;
            this.caterpillar.tabs.selected = GuiTabs.MAIN;
            this.sendUpdates();
        }

        @Override
        public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(left = new GuiButton(0, guiLeft+68, guiTop+23, 20, 20, "<-"));
        this.buttonList.add(right = new GuiButton(1, guiLeft+88, guiTop+23, 20, 20, "->"));
        setButtonRules();
        super.initGui();
    }

        private void setButtonRules(){
        left.enabled=this.caterpillar.pageIndex > 0;
        left.visible=this.checkShowtabs(GuiTabs.MAIN);
        right.enabled=this.caterpillar.pageIndex < this.caterpillar.inventoryPages.size()-1;
        right.visible=this.checkShowtabs(GuiTabs.MAIN);
    }

        public void setupWidgets()
        {
            Const.printDebug("Setting up widgets...");
            AbstractRunnerWidgets tmpHoover;
            AbstractRunnerWidgets tmpRun;
            AbstractRunnerWidgets tmpCheck;

            for (GuiTabs tab: GuiTabs.values()) {
                List<PartsGuiWidgets> toAddWid = new ArrayList<>();
                PartsGuiWidgets tmpAdding;
                PartsTexture guiTextureA;
                PartsTexture guiTextureB = null;
                switch (tab) {
                    case MAIN://drill head
                        tmpAdding = new PartsGuiWidgets("power",this,81 - 18, 45 + 17, 16, 16);
                        guiTextureA = new PartsTexture("poweron", GuiTabs.MAIN.guiTextures, 176 + 14 + 16, 0 , 16, 16);
                        guiTextureB = new PartsTexture("poweroff", GuiTabs.MAIN.guiTextures, 176 + 14, 0, 16, 16);
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.hooverrun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();
                                if (widget.Name.equals("power"))
                                {
                                    String powertxt = TextFormatting.GREEN + I18n.format("on");
                                    if (!GuiDrillHead.this.caterpillar.running)
                                    {
                                        powertxt = TextFormatting.RED + I18n.format("off");
                                    }
                                    tmoString.add(I18n.format("power") + powertxt);
                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        tmpAdding.clicked = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.equals("power"))
                                {
                                    GuiDrillHead.this.caterpillar.running = !GuiDrillHead.this.caterpillar.running;
                                }
                            }
                        };
                        tmpAdding.beforeDraw = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.equals("power"))
                                {
                                    if (GuiDrillHead.this.caterpillar.running)
                                    {
                                        widget.drawA = true;
                                        widget.drawB = false;
                                    }
                                    else
                                    {
                                        widget.drawA = false;
                                        widget.drawB = true;
                                    }
                                }

                            }
                        };
                        toAddWid.add(tmpAdding);

                        tmpAdding = new PartsGuiWidgets("burner",this,81, 45,12, 14);
                        guiTextureA = new PartsTexture("burner", GuiTabs.MAIN.guiTextures,176, 0, 12, 14);
                        tmpAdding.setTexture(guiTextureA, null);
                        tmpAdding.hooverrun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();

                                if (widget.Name.equals("burner"))
                                {
                                    int i1 = 0;
                                    int cbt1 = GuiDrillHead.this.caterpillar.burntime;
                                    if (cbt1 < 0)
                                    {
                                        cbt1 = 0;
                                    }
                                    if (GuiDrillHead.this.caterpillar.maxburntime > 0)
                                    {
                                        i1 = 100* (cbt1) / GuiDrillHead.this.caterpillar.maxburntime;
                                    }
                                    tmoString.add(I18n.format("furnace") + i1 + "%");
                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        tmpAdding.beforeDraw = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.equals("burner"))
                                {
                                    widget.drawA = true;
                                    int ct = GuiDrillHead.this.caterpillar.burntime;
                                    if (ct < 0)
                                    {
                                        ct = 0;
                                        widget.drawA = false;
                                    }
                                    int maxBurn = GuiDrillHead.this.caterpillar.maxburntime;
                                    widget.YPercentShownA = (double)ct / (double)maxBurn;
                                }
                            }
                        };
                        toAddWid.add(tmpAdding);
                        break;
                    case DECORATION:
                        tmpHoover = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();
                                if (widget.Name.equals("scrollbar") || widget.Name.equals("scrollbarmoving"))
                                {
                                    int where = GuiDrillHead.this.caterpillar.decoration.countindex;
                                    //tmoString.add(StatCollector.translateToLocal("selected")  + (GuiDrillHead.this.caterpillar.decoration.selected));
                                    tmoString.add(I18n.format("where")  + (where));

                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        tmpRun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.equals("scrollbar"))
                                {
                                    widget.drawA = true;
                                }
                                if (widget.Name.equals("scrollbarmoving"))
                                {

                                    widget.drawA = true;
                                    //int where = GuiDrillHead.this.caterpillar.decoration.selected;
                                    int where = GuiDrillHead.this.caterpillar.decoration.countindex;
                                    int Max = 9;
                                    //Max -= 11;
                                    //-18 - 36 == 54
                                    widget.Y = (int)(-18 + 54 *((double)where/(double)Max));
                                }
                            }
                        };

                        int indexForBar = 1;
                        tmpAdding = new PartsGuiWidgets("scrollbar",this, 120, 58 - 52, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10), 12 + 3, 5, 42);
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        tmpAdding.hooverrun = tmpHoover;
                        toAddWid.add(tmpAdding);

                        tmpAdding = new PartsGuiWidgets("scrollbar",this, 120, 58 - 52 + 28, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10), 12 + 3, 5, 42);
                        tmpAdding.YPercentShownA = 0.9f;
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        tmpAdding.hooverrun = tmpHoover;
                        toAddWid.add(tmpAdding);
                        // -18 - 36
                        tmpAdding = new PartsGuiWidgets("scrollbarmoving",this, 120, -18, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10) + 5, 12 + 3 , 5, 38);
                        tmpAdding.YPercentShownA = 0.3f;
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        toAddWid.add(tmpAdding);

                        tmpHoover = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();
                                if (widget.Name.equals("scrollbar2") || widget.Name.equals("scrollbarmoving2"))
                                {
                                    tmoString.add(I18n.format("wheelstorage"));
                                    tmoString.add(I18n.format("selected")  + (GuiDrillHead.this.caterpillar.decoration.selected));
                                    //tmoString.add(StatCollector.translateToLocal("selected")  + (GuiDrillHead.this.caterpillar.decoration.countindex));
                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        tmpRun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.equals("scrollbar2"))
                                {
                                    widget.drawA = true;
                                }
                                if (widget.Name.equals("scrollbarmoving2"))
                                {

                                    widget.drawA = true;
                                    int where = GuiDrillHead.this.caterpillar.decoration.selected;
                                    //int where = GuiDrillHead.this.caterpillar.decoration.countindex + 1;
                                    int Max = 9;
                                    //Max -= 11;
                                    //-18 - 36 == 54
                                    widget.Y = (int)(-18 + 54 *((double)where/(double)Max));
                                }
                            }
                        };

                        indexForBar = 1;
                        tmpAdding = new PartsGuiWidgets("scrollbar2",this, 130, 58 - 52, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10), 12 + 3, 5, 42);
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        tmpAdding.hooverrun = tmpHoover;
                        toAddWid.add(tmpAdding);

                        tmpAdding = new PartsGuiWidgets("scrollbar2",this, 130, 58 - 52 + 28, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10), 12 + 3, 5, 42);
                        tmpAdding.YPercentShownA = 0.9f;
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        tmpAdding.hooverrun = tmpHoover;
                        toAddWid.add(tmpAdding);
                        // -18 - 36
                        tmpAdding = new PartsGuiWidgets("scrollbarmoving2",this, 130, -18, 5, 42);
                        guiTextureA = new PartsTexture("base", GuiTabs.MAIN.guiTextures, 176 + (indexForBar *10) + 5, 12 + 3 , 5, 38);
                        tmpAdding.YPercentShownA = 0.3f;
                        tmpAdding.setTexture(guiTextureA, guiTextureB);
                        tmpAdding.beforeDraw = tmpRun;
                        toAddWid.add(tmpAdding);

                        tmpAdding = new PartsGuiWidgets("selected",this,13+12, 44, 18, 18);
                        tmpAdding.hooverrun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();
                                if (widget.Name.equals("selected"))
                                {
                                    tmoString.add(I18n.format("selected")  + (GuiDrillHead.this.caterpillar.decoration.selected));
                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        toAddWid.add(tmpAdding);

                        this.addDefaultButtons(toAddWid, 5, 5, 40, 5);

                        break;
                    case REINFORCEMENT://decoration
                        tmpAdding = new PartsGuiWidgets("background",this, 0, -23, 176, 29);
                        guiTextureA = new PartsTexture("background", GuiTabs.REINFORCEMENT.guiTextures, 0, 166 , 176, 29);
                        tmpAdding.setTexture(guiTextureA, null);
                        tmpAdding.drawA = true;
                        toAddWid.add(tmpAdding);
                        tmpCheck = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.startsWith("check"))
                                {
                                    String[] getinfo = widget.Name.split(",");
                                    int j = Integer.parseInt(getinfo[1]);
                                    int k = Integer.parseInt(getinfo[2]);
                                    if (GuiDrillHead.this.caterpillar.reinforcement.replacers.get(j)[k] == 0)
                                    {
                                        GuiDrillHead.this.caterpillar.reinforcement.replacers.get(j)[k] = 1;
                                    }
                                    else
                                    {
                                        GuiDrillHead.this.caterpillar.reinforcement.replacers.get(j)[k] = 0;
                                    }
                                }
                            }
                        };
                        tmpHoover  = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                List tmoString = new ArrayList<String>();
                                if (widget.Name.startsWith("check"))
                                {
                                    String[] getinfo = widget.Name.split(",");
                                    int j = Integer.parseInt(getinfo[1]);
                                    int k = Integer.parseInt(getinfo[2]);
                                    if (GuiDrillHead.this.caterpillar.reinforcement.replacers.get(j)[k] == 0)
                                    {
                                        tmoString.add(I18n.format("wontreplace") + TextFormatting.RED + Replacement.values()[k].name);
                                    }
                                    else
                                    {
                                        tmoString.add(I18n.format("willreplace") + TextFormatting.GREEN  + Replacement.values()[k].name);
                                    }
                                }
                                GuiDrillHead.this.drawHoveringText(tmoString,widget.getMouseX() - widget.getGuiX(), widget.getMouseY() - widget.getGuiY());
                            }
                        };
                        tmpRun = new AbstractRunnerWidgets() {

                            @Override
                            public void run(PartsGuiWidgets widget) {
                                if (widget.Name.startsWith("check"))
                                {
                                    String[] getinfo = widget.Name.split(",");
                                    int j = Integer.parseInt(getinfo[1]);
                                    int k = Integer.parseInt(getinfo[2]);
                                    int whatcolor = GuiDrillHead.this.caterpillar.reinforcement.replacers.get(j)[k];
                                    if (whatcolor == 0)
                                    {
                                        widget.drawA = true;
                                        widget.drawB = false;
                                    }
                                    else
                                    {
                                        widget.drawA = false;
                                        widget.drawB = true;
                                    }
                                }
                            }
                        };
                        for (int i = 0; i < 5; i++) {//Left
                            tmpAdding = new PartsGuiWidgets("check,1," + i,this, 63, 35 - 18 + 4*i, 4, 4);
                            guiTextureA = new PartsTexture("check0", GuiTabs.REINFORCEMENT.guiTextures, 176, 0 , 4, 4);
                            guiTextureB = new PartsTexture("check1", GuiTabs.REINFORCEMENT.guiTextures, 176 + 4, 0, 4, 4);
                            tmpAdding.setTexture(guiTextureA, guiTextureB);
                            tmpAdding.beforeDraw = tmpRun;
                            tmpAdding.clicked = tmpCheck;
                            tmpAdding.hooverrun = tmpHoover;
                            toAddWid.add(tmpAdding);
                        }

                        for (int i = 0; i < 5; i++) {//right
                            tmpAdding = new PartsGuiWidgets("check,2," + i,this, 51 + 58, 35 - 18 + 4*i, 4, 4);
                            guiTextureA = new PartsTexture("check0", GuiTabs.REINFORCEMENT.guiTextures, 176, 0 , 4, 4);
                            guiTextureB = new PartsTexture("check1", GuiTabs.REINFORCEMENT.guiTextures, 176 + 4, 0, 4, 4);
                            tmpAdding.setTexture(guiTextureA, guiTextureB);
                            tmpAdding.beforeDraw = tmpRun;
                            tmpAdding.clicked = tmpCheck;
                            tmpAdding.hooverrun = tmpHoover;
                            toAddWid.add(tmpAdding);
                        }

                        for (int i = 0; i < 5; i++) {//top
                            tmpAdding = new PartsGuiWidgets("check,0," + i,this,57 + 23 + 4*i, 0, 4, 4);
                            guiTextureA = new PartsTexture("check0", GuiTabs.REINFORCEMENT.guiTextures, 176, 0 , 4, 4);
                            guiTextureB = new PartsTexture("check1", GuiTabs.REINFORCEMENT.guiTextures, 176 + 4, 0, 4, 4);
                            tmpAdding.setTexture(guiTextureA, guiTextureB);
                            tmpAdding.beforeDraw = tmpRun;
                            tmpAdding.clicked = tmpCheck;
                            tmpAdding.hooverrun = tmpHoover;
                            toAddWid.add(tmpAdding);
                        }

                        for (int i = 0; i < 5; i++) {//lower
                            tmpAdding = new PartsGuiWidgets("check,3," + i,this,57 + 23 + 4*i, 12 + 34, 4, 4);
                            guiTextureA = new PartsTexture("check0", GuiTabs.REINFORCEMENT.guiTextures, 176, 0 , 4, 4);
                            guiTextureB = new PartsTexture("check1", GuiTabs.REINFORCEMENT.guiTextures, 176 + 4, 0, 4, 4);
                            tmpAdding.setTexture(guiTextureA, guiTextureB);
                            tmpAdding.beforeDraw = tmpRun;
                            tmpAdding.clicked = tmpCheck;
                            tmpAdding.hooverrun = tmpHoover;
                            toAddWid.add(tmpAdding);
                        }
                        this.addDefaultButtons(toAddWid, 140, 0, 140, 22);

                        break;
                    case INCINERATOR:
                        this.addDefaultButtons(toAddWid, 5, 5, 5, 30);

                        break;
                    default:
                        break;
                }
                this.widgetsHolder.put(tab,toAddWid);
            }
            this.selectedWidgets = this.widgetsHolder.get(GuiTabs.MAIN);
        }

        public void addDefaultButtons(List<PartsGuiWidgets> toAddWid, int defaultx, int defaulty, int defaultallx, int defaultally) {
        PartsGuiWidgets tmpAdding;
        PartsTexture guiTextureA;
        PartsTexture guiTextureB;
        tmpAdding = new PartsGuiWidgets("default",this , defaultx, defaulty, 31, 20);
        guiTextureA = new PartsTexture("default1", GuiTabs.MAIN.guiTextures, 176, 98 , 31, 20);
        guiTextureB = new PartsTexture("default2", GuiTabs.MAIN.guiTextures, 176, 98 + 20, 31, 20);
        tmpAdding.setTexture(guiTextureA, guiTextureB);
        tmpAdding.drawA = true;
        tmpAdding.hooverrun = new AbstractRunnerWidgets() {
            @Override
            public void run(PartsGuiWidgets widget) {
                GuiDrillHead.this.buttonpress(widget);
            }
        };
        tmpAdding.hoovernotrun = new AbstractRunnerWidgets() {
            @Override
            public void run(PartsGuiWidgets widget) {
                if (widget.Name.equals("default") || widget.Name.equals("defaultall"))
                {
                    widget.drawA = true;
                    widget.drawB = false;
                }
            }
        };
        tmpAdding.text = "Set World\n Default";
        toAddWid.add(tmpAdding);

        tmpAdding = new PartsGuiWidgets("defaultall",this , defaultallx, defaultally, 31, 20);
        guiTextureA = new PartsTexture("default1", GuiTabs.MAIN.guiTextures, 176, 98 , 31, 20);
        guiTextureB = new PartsTexture("default2", GuiTabs.MAIN.guiTextures, 176, 98 + 20, 31, 20);
        tmpAdding.setTexture(guiTextureA, guiTextureB);
        tmpAdding.drawA = true;
        tmpAdding.hooverrun = new AbstractRunnerWidgets() {
            @Override
            public void run(PartsGuiWidgets widget) {
                GuiDrillHead.this.buttonpressall(widget);
            }
        };
        tmpAdding.hoovernotrun = new AbstractRunnerWidgets() {
            @Override
            public void run(PartsGuiWidgets widget) {
                if (widget.Name.equals("default") || widget.Name.equals("defaultall"))
                {
                    widget.drawA = true;
                    widget.drawB = false;
                }
            }
        };
        tmpAdding.text = "Set Global\n Default";
        toAddWid.add(tmpAdding);
    }
        public void buttonpress(PartsGuiWidgets widget)
        {
            if (widget.Name.equals("default"))
            {
                if (Mouse.isButtonDown(0))
                {
                    widget.drawA = false;
                    widget.drawB = true;
                }
                else
                {
                    if (!widget.drawA)
                    {
                        NBTTagCompound tmpNBT = new NBTTagCompound();
                        NBTTagCompound tmpNBTsub;

                        switch (this.caterpillar.tabs.selected) {
                            case MAIN://drill head
                                break;
                            case DECORATION:
                                tmpNBTsub = this.caterpillar.decoration.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("decoration", tmpNBTsub);
                                Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationWorld(), "DecorationDefault.dat");
                                break;
                            case REINFORCEMENT:
                                tmpNBTsub = this.caterpillar.reinforcement.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("reinforcement", tmpNBTsub);
                                Reference.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationWorld(), "ReinforcementDefault.dat");
                                break;
                            case INCINERATOR:
                                tmpNBTsub = this.caterpillar.incinerator.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("incinerator", tmpNBTsub);
                                Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationWorld(), "IncineratorDefault.dat");
                                break;
                            default:
                                break;
                        }
                    }
                    widget.drawA = true;
                    widget.drawB = false;
                }

            }
        }
        public void buttonpressall(PartsGuiWidgets widget)
        {
            if (widget.Name.equals("defaultall"))
            {
                if (Mouse.isButtonDown(0))
                {
                    widget.drawA = false;
                    widget.drawB = true;
                }
                else
                {
                    if (!widget.drawA)
                    {
                        NBTTagCompound tmpNBT = new NBTTagCompound();
                        NBTTagCompound tmpNBTsub;

                        switch (this.caterpillar.tabs.selected) {
                            case MAIN://drill head
                                break;
                            case DECORATION:
                                tmpNBTsub = this.caterpillar.decoration.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("decoration", tmpNBTsub);

                                Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationMod(), "DecorationDefault.txt");
                                break;
                            case REINFORCEMENT:
                                tmpNBTsub = this.caterpillar.reinforcement.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("reinforcement", tmpNBTsub);
                                Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationMod(), "ReinforcementDefault.txt");
                                break;
                            case INCINERATOR:
                                tmpNBTsub = this.caterpillar.incinerator.saveNBT();
                                tmpNBTsub.setInteger("howclose", 0);
                                tmpNBT.setTag("incinerator", tmpNBTsub);
                                Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationMod(), "IncineratorDefault.txt");
                                break;
                            default:
                                break;
                        }
                    }
                    widget.drawA = true;
                    widget.drawB = false;
                }
            }
        }


        /**
         * Args : renderPartialTicks, mouseX, mouseY
         */
        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
        {
            switch (this.caterpillar.tabs.selected) {
                case MAIN://drill head
                    this.drawGuiDrillHeadBackgroundLayer();
                    break;
                case DECORATION:
                    this.drawGuiDecorationBackgroundLayer();
                    break;
                case REINFORCEMENT:
                    this.drawGuiReinforcementBackgroundLayer();
                    break;
                case INCINERATOR:
                    this.drawGuiIncineratorBackgroundLayer();
                    break;
                default:
                    this.drawGuiDrillHeadBackgroundLayer();
                    break;
            }

            this.selectedWidgets.forEach(PartsGuiWidgets::drawGuiWidgets);

            for (GuiTabs p : GuiTabs.values())
            {
                boolean drawtab = this.checkShowtabs(p);
                if (drawtab)
                {
                    this.drawTabsBackground(p);
                }
            }
        }
        private boolean checkShowtabs(GuiTabs p) {
        boolean drawtab = true;
        if (p.equals(GuiTabs.DECORATION))
        {
            if (this.caterpillar.decoration.howclose < 1)
            {
                drawtab = false;
            }
        }
        if (p.equals(GuiTabs.REINFORCEMENT))
        {
            if (this.caterpillar.reinforcement.howclose < 1)
            {
                drawtab = false;
            }
        }
        if (p.equals(GuiTabs.INCINERATOR))
        {
            if (this.caterpillar.incinerator.howclose < 1)
            {
                drawtab = false;
            }
        }
        return drawtab;
    }
        /**
         * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
         */
        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
        {
            for (GuiTabs p : GuiTabs.values())
            {
                boolean drawtab = this.checkShowtabs(p);
                if (drawtab)
                {
                    this.drawTabsForeground(p);
                }
            }

            switch (this.caterpillar.tabs.selected) {
                case MAIN://drill head
                    this.drawGuiDrillHeadForegroundLayer();
                    break;
                case DECORATION:
                    this.drawGuiDecorationForegroundLayer();
                    break;
                case REINFORCEMENT:
                    this.drawGuiReinforcementForegroundLayer();
                    break;
                case INCINERATOR:
                    //this.drawGuiIncineratorForegroundLayer();
                    break;
                default:
                    this.drawGuiDrillHeadForegroundLayer();
                    break;
            }

            for (PartsGuiWidgets widget : this.selectedWidgets) {
                widget.drawforgroundlayer();
                widget.hooverdGuiWidgets();
            }

            for (GuiTabs p : GuiTabs.values())
            {
                boolean drawtab = this.checkShowtabs(p);
                if (drawtab)
                {
                    this.drawTabsHover(p);
                }
            }

            this.drawTutHover();
        }
        private void drawTutHover() {
        if (this.caterpillar.tabs.selected == GuiTabs.MAIN)
        {
            if (Config.tutorial[0])
            {
                this.howTut = new PartsTutorial("placefuel", 0, this, -74, 11, true);
            }else if (Config.tutorial[1]){
                this.howTut = new PartsTutorial("poweron", 1, this, -91, 11, true);
            }else if (this.caterpillar.storage.storageComponentCount > 0 && Config.tutorial[2]){
                this.howTut = new PartsTutorial("buttonstorage", 2, this, -48, -111, false);
            }else{
                this.howTut = null;
            }
        }
        if (this.caterpillar.tabs.selected == GuiTabs.DECORATION)
        {
            if (Config.tutorial[3])
            {
                this.howTut = new PartsTutorial("selection", 3, this, -35, 15, true);
            }
            else if (Config.tutorial[4])
            {
                this.howTut = new PartsTutorial("selectionzero", 4, this, -175, -24, true);
            }
            else if (Config.tutorial[5])
            {
                this.howTut = new PartsTutorial("selectionpatter", 5, this, -75, 14, true);
            }
            else
            {
                this.howTut = null;
            }
        }
        if (this.caterpillar.tabs.selected == GuiTabs.REINFORCEMENT)
        {
            if (Config.tutorial[6])
            {
                this.howTut = new PartsTutorial("options", 6, this, -95, -28, true);
            }
            else if (Config.tutorial[7])
            {
                this.howTut = new PartsTutorial("options2", 7, this, -75, 5, true);
            }
            else
            {
                this.howTut = null;
            }
        }

        if (this.howTut != null)
        {
            this.howTut.draw();
        }

    }
        @Override
        public void drawHoveringText(@Nonnull List textLines, int x, int y)
        {
            super.drawHoveringText(textLines, x, y);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
        {
            if (this.howTut != null)
            {
                if( this.howTut.checkClicked())
                {
                    return;
                }
            }

            super.mouseClicked(mouseX, mouseY, mouseButton);

            int k = (this.width - this.xSize) / 2;
            int l = (this.height - this.ySize) / 2;

            this.selectedWidgets.forEach(PartsGuiWidgets::clickedGuiWidgets);

            for (GuiTabs p : GuiTabs.values())
            {
                boolean drawtab = this.checkShowtabs(p);
                if (drawtab)
                {
                    this.checkMouseClickTabs(mouseX, mouseY, k, l, p.value);
                }
            }
        }


        private void checkMouseClickTabs(int mouseX, int mouseY, int k, int l, int index) {
        //(k + this.xSize, l + 3 + index*20, 176, 58, 31, 20);
        int XSide = k - 31;
        int XWidth = 31;

        int YSide = l + 3 + index*20;
        int YHeight = 20;

        if (mouseX > XSide && mouseX < XSide + XWidth)
        {
            if (mouseY > YSide && mouseY < YSide + YHeight)
            {
                this.caterpillar.tabs.selected = GuiTabs.values()[index];
                this.selectedWidgets = this.widgetsHolder.get(this.caterpillar.tabs.selected);

                Const.printDebug("Placing slots for tab "+this.caterpillar.tabs.selected);
                switch (this.caterpillar.tabs.selected) {
                    case MAIN://drill head
                        this.caterpillar.placeSlotsforMain(this.inventorySlots);
                        this.sendUpdates();
                        break;
                    case DECORATION:
                        this.caterpillar.placeSlotsforDecorations(this.inventorySlots);
                        this.sendUpdates();
                        this.caterpillar.markDirty();
                        break;
                    case REINFORCEMENT:
                        this.caterpillar.placeSlotsforReinforcements(this.inventorySlots);
                        this.sendUpdates();
                        this.caterpillar.markDirty();
                        break;
                    case INCINERATOR:
                        this.caterpillar.placeSlotsforIncinerator(this.inventorySlots);
                        this.sendUpdates();
                        this.caterpillar.markDirty();
                        break;
                    default:
                        this.caterpillar.placeSlotsforMain(this.inventorySlots);
                        this.sendUpdates();
                        break;
                }
                setButtonRules();
                Const.printDebug("Placement complete!");
            }
        }
    }
        private void drawTabsHover(GuiTabs tabHoveredOver) {
        //(k + this.xSize, l + 3 + index*20, 176, 58, 31, 20);
        int i = Mouse.getX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        int XSide = k - 31;
        int XWidth = 31;

        int YSide = l + 3 + tabHoveredOver.value*20;
        int YHeight = 20;

        if (i > XSide && i < XSide + XWidth)
        {
            if (j > YSide && j < YSide + YHeight)
            {
                List tmoString = new ArrayList<String>();
                tmoString.add(tabHoveredOver.name);
                //If any tooltip is needed for a tab, add it to tmoString here.
                this.drawHoveringText(tmoString, i - k, j - l);
            }
        }
    }
        public FontRenderer getfontRendererObj()
        {
            return this.fontRenderer;
        }
        private void drawTabsForeground(GuiTabs tabToDraw) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(tabToDraw.guiTextures);
        String Caption = tabToDraw.name;
        if (Caption.length() > 5)
        {
            Caption = Caption.substring(0, 3) + "...";
        }

        if (this.caterpillar.tabs.selected.equals(tabToDraw))
        {
            this.fontRenderer.drawString(Caption,-31 + 5 ,  3 + tabToDraw.value*20 + 5, Color.BLACK.getRGB());
        }
        else
        {
            this.fontRenderer.drawString(Caption,-31 + 3,  3 + tabToDraw.value*20 + 5, Color.GRAY.getRGB());
        }
    }

        private void drawTabsBackground(GuiTabs p) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiTabs.MAIN.guiTextures);//has to be main, thats where the tab graphics are
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;


        if (this.caterpillar.tabs.selected.equals(p))
        {
            this.drawTexturedModalRect(k - 31 + 3, l + 3 + p.value*20, 176, 58 + 20, 31, 20);
        }
        else
        {
            this.drawTexturedModalRect(k - 31, l + 3 + p.value*20, 176, 58, 31, 20);
        }
    }

        private void drawGuiReinforcementForegroundLayer() {
		/*
		this.fontRendererObj.drawString(StatCollector.translateToLocal("ceiling"),  55 + 18, 3, Color.BLACK.getRGB());
		this.fontRendererObj.drawString(StatCollector.translateToLocal("leftwall"),  13 , 39, Color.BLACK.getRGB());
		this.fontRendererObj.drawString(StatCollector.translateToLocal("rightwall"),  123, 39, Color.BLACK.getRGB());

		this.fontRendererObj.drawString(StatCollector.translateToLocal("floor"),  55 + 20, 74, Color.BLACK.getRGB());
		 */
    }

        private void drawGuiDecorationForegroundLayer() {
		/*
		for (int i = 0; i < 9; i++) {
			int colort = Color.BLACK.getRGB();
			if (!this.caterpillar.decoration.isInventoryEmpty(i + 1))
			{
				colort = Color.GREEN.getRGB();
			}
			if (this.caterpillar.decoration.selected == i + 1)
			{
				colort = Color.BLUE.getRGB();
			}
			this.fontRendererObj.drawString("" + (i + 1), 13 + 18*i, 10, colort);
		}
		 */
        this.fontRenderer.drawString(this.caterpillar.decoration.selected + "", 13 + 18, 10 + 20*2, Color.BLUE.getRGB());
    }

        private void drawGuiIncineratorBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiTabs.INCINERATOR.guiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

        private void drawGuiReinforcementBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiTabs.REINFORCEMENT.guiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

        private void drawGuiDecorationBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiTabs.DECORATION.guiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

        private void drawGuiDrillHeadForegroundLayer() {
        this.fontRenderer.drawString(I18n.format("consumption"), 2, -8, Color.WHITE.getRGB());
        this.fontRenderer.drawString(I18n.format("gathered"), 120, -8, Color.WHITE.getRGB());
    }

        private void drawGuiDrillHeadBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiTabs.MAIN.guiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

        @Override
        public void handleMouseInput() throws IOException
        {
            super.handleMouseInput();
            int speed = Mouse.getDWheel();

            if (speed != 0)
            {
                if (this.caterpillar.tabs.selected.equals(GuiTabs.DECORATION))
                {
                    this.mouseWheelMovedDecoration(speed);
                }
            }
        }

        @Override
        protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            switch(button.id){
                case 0:
                    PacketDispatcher.sendToServer(new PacketIncrementInventory(caterpillar.getPos(), 1));
                    break;
                case 1:
                    PacketDispatcher.sendToServer(new PacketIncrementInventory(caterpillar.getPos(), -1));
            }
        }
    }

        public void mouseWheelMovedDecoration(int speed)
        {
            if (speed < 0)
            {
                this.caterpillar.decoration.selected++;
                if (this.caterpillar.decoration.selected > 9)
                {
                    this.caterpillar.decoration.selected = 0;
                }
            }
            else
            {
                this.caterpillar.decoration.selected--;
                if (this.caterpillar.decoration.selected < 0)
                {
                    this.caterpillar.decoration.selected = 9;
                }
            }
        }
        @Override
        public void onGuiClosed()
        {
            Const.printDebug("GUI: Closing, " + this.caterpillar.getName());
            this.caterpillar.tabs.selected = GuiTabs.MAIN;
            //PacketDispatcher.sendToServer(new PacketSendCatData(this.caterpillar));
            //TODO: See what is needed here
            Const.printDebug("Closing: Saving...");
        }
        private void sendUpdates() {
            Const.printDebug("GUI: Updating Server, " + this.caterpillar.getName());
        boolean whatamI = this.caterpillar.running;
        this.caterpillar.running = false;
        //PacketDispatcher.sendToServer(new PacketSendCatData(this.caterpillar));
        //TODO: See what is needed here
        this.caterpillar.running = whatamI;
    }
}
