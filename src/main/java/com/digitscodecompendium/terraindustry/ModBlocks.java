package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.refinery.BuiltinRefineries;
import com.digitscodecompendium.terraindustry.refinery.RefineryControllerBlock;
import com.digitscodecompendium.terraindustry.refinery.RefineryPortBlock;
import com.digitscodecompendium.terraindustry.refinery.RefineryPortType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** All refinery pieces are intentionally unbreakable; only their controller owns behaviour. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TerraIndustry.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraIndustry.MODID);

    public static final DeferredBlock<RefineryControllerBlock> REFINERY_CONTROLLER = registerController("refinery_controller");
    public static final DeferredBlock<RefineryControllerBlock> IRON_REFINERY = registerController("iron_refinery", ResourceLocation.parse(BuiltinRefineries.IRON_REFINERY));
    public static final DeferredBlock<RefineryPortBlock> FUEL_PORT = registerPort("fuel_port", RefineryPortType.FUEL);
    public static final DeferredBlock<RefineryPortBlock> MODIFIER_PORT = registerPort("modifier_port", RefineryPortType.MODIFIER);
    public static final DeferredBlock<RefineryPortBlock> COOLANT_PORT = registerPort("coolant_port", RefineryPortType.COOLANT);
    public static final DeferredBlock<Block> CATALYST_BLOCK = registerSimpleBlock("catalyst_block");

    private static DeferredBlock<RefineryControllerBlock> registerController(String name) {
        DeferredBlock<RefineryControllerBlock> block = BLOCKS.registerBlock(name, RefineryControllerBlock::new);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<RefineryControllerBlock> registerController(String name, ResourceLocation definitionId) {
        DeferredBlock<RefineryControllerBlock> block = BLOCKS.registerBlock(name, properties -> new RefineryControllerBlock(properties, definitionId));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<RefineryPortBlock> registerPort(String name, RefineryPortType type) {
        DeferredBlock<RefineryPortBlock> block = BLOCKS.registerBlock(name, properties -> new RefineryPortBlock(properties, type));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<Block> registerSimpleBlock(String name) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(name, properties -> new Block(properties.strength(-1.0F, 3_600_000.0F)));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private ModBlocks() { }
}
