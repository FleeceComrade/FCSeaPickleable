package com.fleece.fcseapickleable.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
}
