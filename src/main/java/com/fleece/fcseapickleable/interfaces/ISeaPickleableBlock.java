package com.fleece.fcseapickleable.interfaces;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.fleece.fcseapickleable.FCSeaPickleable.copyAllSharedProperties;
/**
 * <p>
 *  Only to be implemented by <b>Mixins</b> to vanilla classes, allows BlockItemMixin to detect
 *  seapickleable vanilla items and get the appropriate block to pickle them into.
 * </p>
 *
 * <p>
 *   Custom pickled Blocks should extend <b>AbstractSeaPickleableBlock</b> instead.
 * </p>
 * **/
public interface ISeaPickleableBlock {
  Block alternateBlock();

  default BlockState seaPickled(BlockState toPickle) {
    return copyAllSharedProperties(toPickle, alternateBlock().defaultBlockState());
  }

  /**
   * <p>Make sure to use this injection annotation when overriding this in your vanilla class Mixin:</p>
   * <pre>@Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)</pre>
   * **/
  void onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir);


  boolean canBeReplaced(BlockState blockState, BlockPlaceContext context);
}
