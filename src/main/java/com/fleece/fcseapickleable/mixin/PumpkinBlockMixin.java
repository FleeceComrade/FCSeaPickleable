package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(PumpkinBlock.class)
public class PumpkinBlockMixin extends Block implements ISeaPickleableBlock {

  private PumpkinBlockMixin() {
    super(Properties.of());
  }

  @Override
  public boolean isPickleable() {
    return this.equals(Blocks.PUMPKIN);
  }

  @Override
  public Block alternateBlock() {
    return FCSPBlocks.SEAPICKLED_PUMPKIN.get();
  }

  @Override
  public boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}
