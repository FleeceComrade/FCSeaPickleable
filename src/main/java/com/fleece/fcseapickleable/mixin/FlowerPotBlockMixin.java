package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin extends Block implements ISeaPickleableBlock {

  public FlowerPotBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    return FCSPBlocks.SEAPICKLED_FLOWER_POT.get();
  }

  @Override
  public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(Items.FLOWER_POT) || super.canBeReplaced(blockState, context);
  }
}