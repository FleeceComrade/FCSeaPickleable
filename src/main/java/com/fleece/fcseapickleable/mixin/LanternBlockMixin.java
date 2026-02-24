package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import static com.fleece.fcseapickleable.registry.FCSPBlocks.SEAPICKLED_LANTERN;
import static com.fleece.fcseapickleable.registry.FCSPBlocks.SEAPICKLED_SOUL_LANTERN;

@SuppressWarnings("deprecation")
@Mixin(LanternBlock.class)
public class LanternBlockMixin extends Block implements ISeaPickleableBlock {

  private LanternBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    if (Blocks.SOUL_LANTERN.equals(this))
      return SEAPICKLED_SOUL_LANTERN.get();
    return SEAPICKLED_LANTERN.get();
  }

  @Override
  public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}