package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TallGrassBlock.class)
public class TallGrassBlockMixin extends Block implements ISeaPickleableBlock {

  private TallGrassBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    if (Blocks.FERN.equals(this))
      return FCSPBlocks.SEAPICKLED_FERN.get();
    return FCSPBlocks.SEAPICKLED_GRASS.get();
  }

  @Override
  public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}
