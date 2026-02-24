package com.fleece.fcseapickleable.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.fleece.fcseapickleable.FCSeaPickleable.joinShapes;

public class SeaPickledPumpkinBlock extends AbstractSeaPickleableBlock {

  public static final VoxelShape[] AABBS = new VoxelShape[]{
    joinShapes(
      BooleanOp.OR,
      Block.box(1, 0, 1, 9, 8, 9),
      Block.box(10, 0, 10, 15, 5, 15)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(1, 0, 5, 9, 7.999, 13),
      Block.box(7, 0, 7, 15, 8, 15),
      Block.box(8, 0, 1, 13, 5, 6)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(4, 0, 5, 15, 11, 16),
      Block.box(1, 0, 0, 9, 8, 8),
      Block.box(0, 0, 7, 5, 5, 12),
      Block.box(9, 0, 1, 14, 5, 6)
    )
  };

  public SeaPickledPumpkinBlock(Block alternateBlock) {
    super(alternateBlock);
  }

  @Override
  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
    return AABBS[blockState.getValue(QUANTITY) - 2];
  }
}