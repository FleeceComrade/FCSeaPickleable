package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.interfaces.ISeaPickleableBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.fleece.fcseapickleable.FCSeaPickleable.copyAllSharedProperties;
import static com.fleece.fcseapickleable.registry.FCSPBlocks.*;

@SuppressWarnings("deprecation")
@Mixin(ChainBlock.class)
public class ChainBlockMixin extends Block implements ISeaPickleableBlock {

  private ChainBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    return SEAPICKLED_CHAIN.get();
  }

  @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
  public void fc$onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {}

  @Override
  public final BlockState seaPickled(BlockState toPickle) {
    return copyAllSharedProperties(toPickle, alternateBlock().defaultBlockState());
  }

  @Override
  public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}