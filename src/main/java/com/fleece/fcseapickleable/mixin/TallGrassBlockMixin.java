package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(TallGrassBlock.class)
public class TallGrassBlockMixin extends Block implements ISeaPickleableBlock {

  private TallGrassBlockMixin() {
    super(Properties.of());
  }

  @Override
  public boolean isPickleable() {
    return this.equals(Blocks.FERN) || this.equals(Blocks.GRASS);
  }

  @Override
  public Block alternateBlock() {
    if (Blocks.FERN.equals(this))
      return FCSPBlocks.SEAPICKLED_FERN.get();
    if (Blocks.GRASS.equals(this))
      return FCSPBlocks.SEAPICKLED_GRASS.get();
    throw new IllegalStateException("Contact FleeceComrade, author of FC's Seapickleable, he did something wrong if you are seeing this! fleececomrade@gmail.com or FleeceComradeMods on Curseforge.");
  }

  @Override
  public boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}
