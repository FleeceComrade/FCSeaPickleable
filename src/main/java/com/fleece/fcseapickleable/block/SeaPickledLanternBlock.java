package com.fleece.fcseapickleable.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.fleece.fcseapickleable.FCSeaPickleable.joinShapes;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HANGING;

@SuppressWarnings("NullableProblems")
public class SeaPickledLanternBlock extends AbstractSeaPickleableBlock implements SimpleWaterloggedBlock {

  private static final VoxelShape[] HANGING_AABBS = new VoxelShape[] {
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 1, 7, 14, 8, 13),
      Block.box(9, 8, 8, 13, 10, 12),
      Block.box(4, 4, 5, 8, 10, 9)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(5, 1, 8, 11, 8, 14),
      Block.box(4, 2, 3, 9, 8, 8),
      Block.box(6, 8, 9, 10, 10, 13),
      Block.box(4.5, 8, 3.5, 8.5, 10, 7.5),
      Block.box(9, 4, 4, 13, 10, 8)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 1, 3, 14, 8, 9),
      Block.box(3, 5, 4, 8, 11, 9),
      Block.box(8.5, 8, 9.5, 12.5, 10, 13.5),
      Block.box(8, 2, 9, 13, 8, 14),
      Block.box(9, 8, 4, 13, 10, 8),
      Block.box(3.5, 11, 4.5, 7.5, 13, 8.5),
      Block.box(4, 4, 9, 8, 10, 13)
    )
  };
  private static final VoxelShape[] AABBS = new VoxelShape[] {
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 0, 7, 14, 7, 13),
      Block.box(9, 7, 8, 13, 9, 12),
      Block.box(4, 0, 5, 8, 6, 9)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(5, 0, 8, 11, 7, 14),
      Block.box(4, 0, 3, 9, 6, 8),
      Block.box(6, 7, 9, 10, 9, 13),
      Block.box(4.5, 6, 3.5, 8.5, 8, 7.5),
      Block.box(9, 0, 4, 13, 6, 8)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 0, 3, 14, 7, 9),
      Block.box(3, 0, 4, 8, 6, 9),
      Block.box(8.5, 6, 9.5, 12.5, 8, 13.5),
      Block.box(8, 0, 9, 13, 6, 14),
      Block.box(9, 7, 4, 13, 9, 8),
      Block.box(3.5, 6, 4.5, 7.5, 8, 8.5),
      Block.box(4, 0, 9, 8, 6, 13)
    )
  };

  public SeaPickledLanternBlock(Block alternateBlock) {
    super(alternateBlock);
  }

  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
    int index = blockState.getValue(QUANTITY) - 2;
    return blockState.getValue(HANGING) ? HANGING_AABBS[index] : AABBS[index];
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
    return 15;
  }
}
