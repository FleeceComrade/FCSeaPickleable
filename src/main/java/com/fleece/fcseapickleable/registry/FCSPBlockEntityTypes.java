package com.fleece.fcseapickleable.registry;

import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;

public class FCSPBlockEntityTypes {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

  public static final RegistryObject<BlockEntityType<SeaPickledFlowerPotBlockEntity>> SEAPICKLED_FLOWERPOT = BLOCK_ENTITY_TYPES.register("seapickled_flowerpot",
    () -> BlockEntityType.Builder.of(SeaPickledFlowerPotBlockEntity::new, FCSPBlocks.SEAPICKLED_FLOWER_POT.get()).build(null));
}
