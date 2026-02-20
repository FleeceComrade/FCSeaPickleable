package com.fleece.fcseapickleable.registry;

import com.fleece.fcseapickleable.block.SeaPickledLanternBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;

public class FCSPBlocks {
  public static final  DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

  public static final RegistryObject<Block> SEAPICKLED_LANTERN = BLOCKS.register("seapickled_lantern",
    () -> new SeaPickledLanternBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN)));
}
