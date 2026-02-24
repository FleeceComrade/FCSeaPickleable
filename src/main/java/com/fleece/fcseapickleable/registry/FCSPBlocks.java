package com.fleece.fcseapickleable.registry;

import com.fleece.fcseapickleable.block.SeaPickledChainBlock;
import com.fleece.fcseapickleable.block.SeaPickledLanternBlock;
import com.fleece.fcseapickleable.block.SeaPickledPumpkinBlock;
import com.fleece.fcseapickleable.block.SeaPickledTallGrassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;

public class FCSPBlocks {
  public static final  DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

  public static final RegistryObject<Block> SEAPICKLED_LANTERN = BLOCKS.register("seapickled_lantern", () -> new SeaPickledLanternBlock(Blocks.LANTERN));
  public static final RegistryObject<Block> SEAPICKLED_SOUL_LANTERN = BLOCKS.register("seapickled_soul_lantern", () -> new SeaPickledLanternBlock(Blocks.SOUL_LANTERN));
  public static final RegistryObject<Block> SEAPICKLED_CHAIN = BLOCKS.register("seapickled_chain", () -> new SeaPickledChainBlock(Blocks.CHAIN));

  public static final RegistryObject<Block> SEAPICKLED_GRASS = BLOCKS.register("seapickled_grass", () -> new SeaPickledTallGrassBlock(Blocks.GRASS));
  public static final RegistryObject<Block> SEAPICKLED_FERN = BLOCKS.register("seapickled_fern", () -> new SeaPickledTallGrassBlock(Blocks.FERN));
}
