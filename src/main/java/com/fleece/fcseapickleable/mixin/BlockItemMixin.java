package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.interfaces.ISeaPickleableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

  @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
  private void onGetPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    if (this.getBlock() instanceof ISeaPickleableBlock iSeaPickleable) {
      BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
      if (inWorld.is(this.getBlock()) || inWorld.is(iSeaPickleable.alternateBlock())) {
        System.out.println("we are the right block and item");
        cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(inWorld));
      }
    }
  }

  @Shadow
  public Block getBlock() {
    return null;
  }
}
