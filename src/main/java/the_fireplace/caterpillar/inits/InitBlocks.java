package the_fireplace.caterpillar.inits;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.caterpillar.Caterpillar;
import the_fireplace.caterpillar.Reference;
import the_fireplace.caterpillar.blocks.*;

public class InitBlocks {

	public static Block collector;
	public static Block decoration;
	public static Block drillbase;
	public static Block drillheads;
	public static Block reinforcements;
	public static Block storage;
	public static Block incinerator;
	public static Block drill_blades;

	public static void init()
	{
		drill_blades = new BlockDrillBlades().setUnlocalizedName("drillblades");

		drillheads = new BlockDrillHeads().setUnlocalizedName("drillheads").setCreativeTab(Caterpillar.tabCaterpillar);
		reinforcements = new BlockReinforcements().setUnlocalizedName("reinforcements").setCreativeTab(Caterpillar.tabCaterpillar);
		decoration = new BlockDecoration().setUnlocalizedName("decoration").setCreativeTab(Caterpillar.tabCaterpillar);
		collector = new BlockCollector().setUnlocalizedName("collector").setCreativeTab(Caterpillar.tabCaterpillar);
		drillbase = new BlockDrillBase().setUnlocalizedName("drillbase").setCreativeTab(Caterpillar.tabCaterpillar);
		storage = new BlockStorage().setUnlocalizedName("storage").setCreativeTab(Caterpillar.tabCaterpillar);
		incinerator = new BlockIncinerator().setUnlocalizedName("incinerator").setCreativeTab(Caterpillar.tabCaterpillar);
	}

	public static void register() {
		registerBlock(drill_blades);
		registerBlock(drillheads);
		registerBlock(reinforcements);
		registerBlock(decoration);
		registerBlock(collector);
		registerBlock(drillbase);
		registerBlock(storage);
		registerBlock(incinerator);
	}
	private static void registerBlock(Block block){
		GameRegistry.register(block.setRegistryName(block.getUnlocalizedName().substring(5)));
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getUnlocalizedName().substring(5)));
	}
	@SideOnly(Side.CLIENT)
	public static void registerRender(Block block)
	{
		Item item = Item.getItemFromBlock(block);

		if(item != null)
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Caterpillar.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		else
			Reference.printDebug("Error: Null item for "+block);
	}
	@SideOnly(Side.CLIENT)
	public static void registerRenders()
	{
		registerRender(drillheads);
		registerRender(reinforcements);
		registerRender(decoration);
		registerRender(collector);
		registerRender(drillbase);
		registerRender(storage);
		registerRender(incinerator);
	}

}
