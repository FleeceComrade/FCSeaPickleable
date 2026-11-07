package com.fleece.fcseapickleable.registry;

import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;

public class FCSPBlockEntityTypes {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

  @SuppressWarnings("DataFlowIssue")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeaPickledFlowerPotBlockEntity>> SEAPICKLED_FLOWERPOT = BLOCK_ENTITY_TYPES.register("seapickled_flowerpot",
    () -> BlockEntityType.Builder.of(SeaPickledFlowerPotBlockEntity::new, FCSPBlocks.SEAPICKLED_FLOWER_POT.get()).build(null));
}
