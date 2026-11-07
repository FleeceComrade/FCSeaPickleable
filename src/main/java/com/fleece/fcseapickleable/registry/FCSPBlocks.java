package com.fleece.fcseapickleable.registry;

import com.fleece.fcseapickleable.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;

@SuppressWarnings("unused")
public class FCSPBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);

  public static final DeferredHolder<Block, SeaPickledPumpkinBlock> SEAPICKLED_PUMPKIN = BLOCKS.register("seapickled_pumpkin", () -> new SeaPickledPumpkinBlock(Blocks.PUMPKIN));

  public static final DeferredHolder<Block, SeaPickledLanternBlock> SEAPICKLED_LANTERN = BLOCKS.register("seapickled_lantern", () -> new SeaPickledLanternBlock(Blocks.LANTERN));
  public static final DeferredHolder<Block, SeaPickledLanternBlock> SEAPICKLED_SOUL_LANTERN = BLOCKS.register("seapickled_soul_lantern", () -> new SeaPickledLanternBlock(Blocks.SOUL_LANTERN));
  public static final DeferredHolder<Block, SeaPickledChainBlock> SEAPICKLED_CHAIN = BLOCKS.register("seapickled_chain", () -> new SeaPickledChainBlock(Blocks.CHAIN));

  public static final DeferredHolder<Block, SeaPickledTallGrassBlock> SEAPICKLED_GRASS = BLOCKS.register("seapickled_grass", () -> new SeaPickledTallGrassBlock(Blocks.SHORT_GRASS));
  public static final DeferredHolder<Block, SeaPickledTallGrassBlock> SEAPICKLED_FERN = BLOCKS.register("seapickled_fern", () -> new SeaPickledTallGrassBlock(Blocks.FERN));


  public static final DeferredHolder<Block, SeaPickledFlowerPotBlock> SEAPICKLED_FLOWER_POT = BLOCKS.register("seapickled_flower_pot", () -> new SeaPickledFlowerPotBlock(Blocks.FLOWER_POT));
}