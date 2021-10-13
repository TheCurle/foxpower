package uk.gemwire.foxpower.reg;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.gemwire.foxpower.Foxpower;
import uk.gemwire.foxpower.generator.SpinBlock;
import uk.gemwire.foxpower.generator.SpinGenerator;

public class Foxregistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Foxpower.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Foxpower.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Foxpower.MODID);


    public static final RegistryObject<SpinBlock> SPINGENERATOR = BLOCKS.register("spingenerator", SpinBlock::new);

    public static final RegistryObject<BlockEntityType<SpinGenerator>> SPIN_TE = TILES.register("spingenerator",
            () -> BlockEntityType.Builder.of(SpinGenerator::new, SPINGENERATOR.get()).build(null)
    );



}
