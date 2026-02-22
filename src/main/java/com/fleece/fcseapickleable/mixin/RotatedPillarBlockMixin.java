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

@Mixin(LanternBlock.class)
public class RotatedPillarBlockMixin extends Block {

  private RotatedPillarBlockMixin() {
    super(Properties.of());
  }

  @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
  public void fc$onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    if (this instanceof ISeaPickleableBlock iSeaPickleable) {
      BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
      if (inWorld.is(this))
        cir.setReturnValue(iSeaPickleable.seaPickled(inWorld));
    }
  }
}