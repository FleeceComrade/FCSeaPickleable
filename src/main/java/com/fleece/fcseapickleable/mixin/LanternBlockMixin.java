package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.interfaces.ISeaPickleableBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.fleece.fcseapickleable.FCSeaPickleable.copyAllSharedProperties;
import static com.fleece.fcseapickleable.registry.FCSPBlocks.SEAPICKLED_LANTERN;

@SuppressWarnings("deprecation")
@Mixin(LanternBlock.class)
public class LanternBlockMixin extends Block implements ISeaPickleableBlock {

  private LanternBlockMixin() {
    super(Properties.of());
  }

  @Override
  public Block alternateBlock() {
    return SEAPICKLED_LANTERN.get();
  }

  @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
  public void onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
    if (inWorld.is(this)) {
      cir.setReturnValue(this.seaPickled(inWorld));
    }
  }

  @Override
  public final BlockState seaPickled(BlockState toPickle) {
    return copyAllSharedProperties(toPickle, alternateBlock().defaultBlockState());
  }

  @Override
  public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }
}