package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.AbstractSeaPickleableBlock;
import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
  private void fc$onGetPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    if (this.getBlock() instanceof ISeaPickleableBlock iSeaPickleable) {
      BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
      if (inWorld.is(this.getBlock()) || inWorld.is(iSeaPickleable.alternateBlock())) {
        cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(inWorld));
      }
    }
  }

  @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
  private void fc$onPlaceBlock(BlockPlaceContext context, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    BlockState thisPlacement = this.getPlacementState(context);
    BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
    ItemStack itemstack = context.getItemInHand();
    if ((thisPlacement.getBlock() instanceof ISeaPickleableBlock && !(thisPlacement.getBlock() instanceof AbstractSeaPickleableBlock))) {
      if (inWorld.getBlock() instanceof AbstractSeaPickleableBlock) {
        itemstack.setCount(Math.min(itemstack.getMaxStackSize(), itemstack.getCount() + 4));
      }
    }
  }

  @Shadow
  public Block getBlock() {
    return null;
  }

  @Shadow
  public BlockState getPlacementState(BlockPlaceContext context) {
    return null;
  }
}
