package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PumpkinBlock.class)
public class PumpkinBlockMixin extends Block implements ISeaPickleableBlock {

  private PumpkinBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    return FCSPBlocks.SEAPICKLED_PUMPKIN.get();
  }

  @Override
  public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}
