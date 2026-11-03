package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import static com.fleece.fcseapickleable.registry.FCSPBlocks.SEAPICKLED_CHAIN;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(ChainBlock.class)
public class ChainBlockMixin extends Block implements ISeaPickleableBlock {

  private ChainBlockMixin() {
    super(Properties.of());
  }

  @Override
  public boolean isPickleable() {
    return this.equals(Blocks.CHAIN);
  }

  @Override
  public Block alternateBlock() {
    return SEAPICKLED_CHAIN.get();
  }

  @Override
  public final boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}