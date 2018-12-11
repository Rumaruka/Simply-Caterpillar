package com.rumaruka.simplycaterpillar;

import com.rumaruka.simplycaterpillar.client.tabs.TabCaterpillar;
import com.rumaruka.simplycaterpillar.common.blocks.BlockDrillBase;
import com.rumaruka.simplycaterpillar.common.containers.CaterpillarData;
import com.rumaruka.simplycaterpillar.common.handlers.HandlerEvents;
import com.rumaruka.simplycaterpillar.common.handlers.HandlerNBTTag;
import com.rumaruka.simplycaterpillar.common.tileentity.TileEntityDrillComponent;
import com.rumaruka.simplycaterpillar.common.tileentity.TileEntityDrillHead;
import com.rumaruka.simplycaterpillar.init.SCBlocks;
import com.rumaruka.simplycaterpillar.init.SCRecipes;
import com.rumaruka.simplycaterpillar.proxy.CommonProxy;
import com.rumaruka.simplycaterpillar.timers.TimerMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod(name = Caterpillar.MODNAME, modid = Caterpillar.MODID, updateJSON = "http://caterpillar.bitnamiapp.com/jsons/simplycaterpillar.json", acceptedMinecraftVersions = "[1.12.2]")
public class Caterpillar {

    public static final String MODID = "simplycaterpillar";
    public static final String MODNAME = "Simply Caterpillar";
    @Instance(Caterpillar.MODID)
    public static Caterpillar instance;
    public static final CreativeTabs tabCaterpillar = new TabCaterpillar();
    public int saveCount = 0;
    boolean dev = true;
    public TimerMain ModTasks;
    private HashMap<String, CaterpillarData> mainContainers;
    private CaterpillarData selectedCaterpillar;
    @SidedProxy(clientSide = Const.CLIENT_PROXY_CLASS, serverSide = Const.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        Const.MainNBT = new HandlerNBTTag(MODID);
        this.mainContainers = new HashMap<>();
        //NetworkRegistry.INSTANCE.registerGuiHandler();
        this.ModTasks= new TimerMain();

        SCBlocks.init();
        SCBlocks.registerBlocks();


        proxy.registerRenders();

    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerTileEntity(TileEntityDrillComponent.class,"DrillHead");
        MinecraftForge.EVENT_BUS.register(new HandlerEvents());
        SCRecipes.loadRecipes();
        Const.cleanModsFolder();
        Config.load();
        Const.modTick.scheduleAtFixedRate(this.ModTasks,10,10);


    }
    public String getCaterpillarID(int[] movingXZ, BlockPos pos)
    {
        int firstID = movingXZ[1] * pos.getX() + movingXZ[0] * pos.getZ();
        int secondID = pos.getY();
        int third = 0;
        if (movingXZ[0] != 0)
        {
            third = movingXZ[0] + 2;
        }
        if (movingXZ[1] != 0)
        {
            third = movingXZ[1] + 3;
        }
        return firstID + "," + secondID + "," + third;
    }

    public int[] getWayMoving(IBlockState state) {
        try {
            int[] movingXZ = {0, 0};
            if (state.getValue(BlockDrillHeads.FACING) == EnumFacing.EAST)
            {
                movingXZ[0] = -1;
            }
            if (state.getValue(BlockDrillHeads.FACING) == EnumFacing.WEST)
            {
                movingXZ[0] = 1;
            }
            if (state.getValue(BlockDrillHeads.FACING) == EnumFacing.NORTH)
            {
                movingXZ[1] = 1;
            }
            if (state.getValue(BlockDrillHeads.FACING) == EnumFacing.SOUTH)
            {
                movingXZ[1] = -1;
            }

            return movingXZ;
        } catch (Exception e) {
            return new int[]{-2, -2};
        }
    }

    public CaterpillarData getSelectedCaterpillar()
    {
        return this.selectedCaterpillar;
    }
    public void setSelectedCaterpillar(CaterpillarData selectedcat)
    {
        this.selectedCaterpillar = selectedcat;
    }
    public void removeSelectedCaterpillar()
    {
        this.selectedCaterpillar = null;
    }
    public void removeCaterpillar(String CaterpillarID)
    {
        Caterpillar.instance.mainContainers.remove(CaterpillarID);
        this.removeSelectedCaterpillar();
    }

    public boolean doesHaveCaterpillar(String CaterpillarID)
    {
        try {
            return this.mainContainers.containsKey(CaterpillarID);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean doesHaveCaterpillar(BlockPos pos, IBlockState state)
    {
        int[] movingXZ = this.getWayMoving(state);
        if (movingXZ[0] == -2 || movingXZ[1] == -2)
        {
            Const.printDebug("Null: facing");
            return false;
        }
        String CatID = this.getCaterpillarID(movingXZ, pos);
        return this.doesHaveCaterpillar(CatID);
    }

    public void putContainerCaterpillar(CaterpillarData conCat, World objworld) {
        IBlockState thisState =  objworld.getBlockState(conCat.pos);
        int[] movingXZ = this.getWayMoving(thisState);
        if (movingXZ[0] == -2 || movingXZ[1] == -2)
        {
            Const.printDebug("Null: facing");
        }
        String CatID = this.getCaterpillarID(movingXZ, conCat.pos);
        this.putContainerCaterpillar(CatID, conCat);
    }

    public void putContainerCaterpillar(String CaterpillarID, CaterpillarData conCat) {
        this.mainContainers.put(CaterpillarID, conCat);
    }

    public CaterpillarData getContainerCaterpillar(String caterpillarID) {
        return this.mainContainers.get(caterpillarID);
    }

    public CaterpillarData getContainerCaterpillar(BlockPos pos, World objWorld)
    {
        IBlockState thisState =  objWorld.getBlockState(pos);
        int[] movingXZ = this.getWayMoving(thisState);
        if (movingXZ[0] == -2 || movingXZ[1] == -2)
        {
            Const.printDebug("Null: facing");
            return null;
        }
        String catID = this.getCaterpillarID(movingXZ, pos);
        return this.getContainerCaterpillar(catID);
    }

    public CaterpillarData getContainerCaterpillar(BlockPos pos, IBlockState thisState)
    {
        int[] movingXZ = this.getWayMoving(thisState);
        if (movingXZ[0] == -2 || movingXZ[1] == -2)
        {
            Const.printDebug("Null: facing");
            return null;
        }
        String catID =this.getCaterpillarID(movingXZ, pos);
        return this.getContainerCaterpillar(catID);
    }

    public void saveNBTDrills()
    {
        if (Const.loaded)
        {
            NBTTagCompound tmpNBT = new NBTTagCompound();
            int i = 0;
            for (Map.Entry<String, CaterpillarData> key : this.mainContainers.entrySet()) {
                CaterpillarData conCat = key.getValue();
                tmpNBT.setTag("caterpillar" + i, conCat.writeNBTCaterpillar());
                i++;
            }
            tmpNBT.setInteger("count", i);
            Const.MainNBT.saveNBTSettings(tmpNBT, Const.MainNBT.getFolderLocationWorld(), "DrillHeads.dat");
        }
    }
    private World getCaterpillarWorld(BlockPos pos){
        if (FMLCommonHandler.instance().getMinecraftServerInstance().worlds != null)
        {
            if (FMLCommonHandler.instance().getMinecraftServerInstance().worlds.length >0)
            {
                for (WorldServer worldServer : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                    IBlockState state  = worldServer.getBlockState(pos);
                    if (state.getBlock() instanceof BlockDrillBase)
                    {
                        return worldServer;
                    }
                }
            }
        }
        return null;
    }

    public void readNBTDrills()
    {
        NBTTagCompound tmpNBT =  Const.MainNBT.readNBTSettings(Const.MainNBT.getFolderLocationWorld(), "DrillHeads.dat");
        this.mainContainers.clear();

        if (tmpNBT.hasKey("count"))
        {
            int size = tmpNBT.getInteger("count");
            for(int i=0;i<size;i++)
            {
                CaterpillarData conCata = CaterpillarData.readCaterpiller(tmpNBT.getCompoundTag("caterpillar" + i));
                conCata.tabs.selected = GuiTabs.MAIN;
                World objWorld = this.getCaterpillarWorld(conCata.pos);
                if (objWorld != null)
                {
                    IBlockState state = objWorld.getBlockState(conCata.pos);
                    if (state.getBlock() instanceof BlockDrillBase)
                    {
                        int[] movingXZ = this.getWayMoving(state);
                        if (movingXZ[0] != -2 && movingXZ[1] != -2)
                        {
                            this.mainContainers.put(conCata.name, conCata);
                        }
                    }
                }
                else
                {
                    Const.printDebug("Load error NBT Drills");
                }
            }
        }
    }

    public void reset() {
        Const.printDebug("Resetting....");
        Const.loaded = false;
        this.ModTasks.inSetup = false;
        this.mainContainers.clear();
    }
    public ItemStack[] getInventory(CaterpillarData MyCaterpillar, GuiTabs selected)
    {
        if (MyCaterpillar != null)
        {
            switch (selected.value) {
                case 0:
                    return MyCaterpillar.inventory;
                case 1:
                    return MyCaterpillar.decoration.getSelectedInventory();
                case 2:
                    return MyCaterpillar.reinforcement.reinforcementMap;
                case 3:
                    return MyCaterpillar.incinerator.placementMap;
                default:
                    break;
            }
        }
        return new ItemStack[256];
    }

    public enum Replacement {
        AIR(0, proxy.translateToLocal("replacement1")),
        WATER(1, proxy.translateToLocal("replacement2")),
        LAVA(2, proxy.translateToLocal("replacement3")),
        FALLINGBLOCKS(3, proxy.translateToLocal("replacement4")),
        ALL(4, proxy.translateToLocal("replacement5"));
        public final int value;
        public final String name;

        Replacement(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }
    public enum GuiTabs {
        MAIN(0, proxy.translateToLocal("tabs1"), false, new ResourceLocation(MODID  + ":textures/gui/guicatapiller.png")),
        DECORATION(1, proxy.translateToLocal("tabs2"), true, new ResourceLocation(MODID  + ":textures/gui/guidecoration.png")),
        REINFORCEMENT(2, proxy.translateToLocal("tabs3"), true, new ResourceLocation(MODID  + ":textures/gui/guireinfocement.png")),
        INCINERATOR(3, proxy.translateToLocal("tabs4"), true, new ResourceLocation(MODID  + ":textures/gui/guiincinerator.png"));
        public final int value;
        public final String name;
        public final boolean isCrafting;
        public final ResourceLocation guiTextures;

        GuiTabs(int value, String name, boolean isCrafting, ResourceLocation guiTextures) {
            this.value = value;
            this.name = name;
            this.guiTextures = guiTextures;
            this.isCrafting = isCrafting;
        }
    }
    @Nullable
    @Deprecated
    public static TileEntityDrillHead getCaterpillar(World worldIn, BlockPos pos){
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityDrillHead)
            return (TileEntityDrillHead)worldIn.getTileEntity(pos);
        else
            return null;
    }
    @Nullable
    public static TileEntityDrillHead getCaterpillar(World worldIn, BlockPos pos, EnumFacing facing){
        BlockPos pos2 = pos;
        boolean flag = false;
        while(worldIn.getBlockState(pos2).getBlock() instanceof BlockDrillBase || flag){
            //TODO: Verify that the head is facing the same direction as the component
            if(worldIn.getTileEntity(pos2) != null && worldIn.getTileEntity(pos2) instanceof TileEntityDrillHead)
                return (TileEntityDrillHead)worldIn.getTileEntity(pos2);
            pos2.add(facing.getFrontOffsetX(), 0, facing.getFrontOffsetZ());
            Const.printDebug("Searching for the drill head. Initial Pos: "+pos.toString()+", New Pos: "+pos2.toString());
            //Let it skip 1 block while looking, to allow use with a moving caterpillar.
            if(flag){
                if(worldIn.getBlockState(pos2).getBlock() instanceof BlockDrillBase){
                    flag = false;
                }else{
                    break;
                }
            }else{
                if(!(worldIn.getBlockState(pos2).getBlock() instanceof BlockDrillBase))
                    flag = true;
            }
        }
        return null;
    }
}
