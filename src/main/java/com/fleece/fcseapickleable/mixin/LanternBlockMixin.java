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
  public boolean isPickleable() {
    return this.equals(Blocks.LANTERN) || this.equals(Blocks.SOUL_LANTERN);
  }

  @Override
  public Block alternateBlock() {
    if (Blocks.SOUL_LANTERN.equals(this))
      return SEAPICKLED_SOUL_LANTERN.get();
    if (Blocks.LANTERN.equals(this))
      return SEAPICKLED_LANTERN.get();
    throw new IllegalStateException("Contact FleeceComrade, author of FC's Seapickleable, he did something wrong if you are seeing this! fleececomrade@gmail.com or FleeceComradeMods on Curseforge.");
  }

  @Override
  public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}