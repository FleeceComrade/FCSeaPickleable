package com.fleece.fcseapickleable.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.fleece.fcseapickleable.FCSeaPickleable.joinShapes;
import static net.minecraft.core.Direction.Axis.X;
import static net.minecraft.core.Direction.Axis.Y;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class SeaPickledChainBlock extends AbstractSeaPickleableBlock implements SimpleWaterloggedBlock {

  private static final VoxelShape[] AXIS_X_AABBS = new VoxelShape[]{
    joinShapes(
      BooleanOp.OR,
      Block.box(0, 8.5, 9.5, 16, 11.5, 12.5),
      Block.box(0, 5.5, 4.5, 16, 8.5, 7.5)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(0, 4, 5, 16, 7, 8),
      Block.box(0, 4.5, 9.5, 16, 7.5, 12.5),
      Block.box(0.5, 9.5, 6.5, 16, 12.5, 9.5)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(0, 4.5, 9.5, 16, 7.5, 12.5),
      Block.box(0, 9.5, 4.5, 16, 12.5, 7.5),
      Block.box(0, 5, 4, 16, 8, 7),
      Block.box(0, 10, 9, 16, 13, 12)
    )
  };

  private static final VoxelShape[] AXIS_Y_AABBS = new VoxelShape[]{
    joinShapes(
      BooleanOp.OR,
      Block.box(9.5, 0, 8.5, 12.5, 16, 11.5),
      Block.box(4.5, 0, 5.5, 7.5, 16, 8.5)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(5, 0, 4, 8, 16, 7),
      Block.box(9.5, 0, 4.5, 12.5, 16, 7.5),
      Block.box(6.5, 0, 9.5, 9.5, 16, 12.5)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(9.5, 0, 4.5, 12.5, 16, 7.5),
      Block.box(4.5, 0, 9.5, 7.5, 16, 12.5),
      Block.box(4, 0, 5, 7, 16, 8),
      Block.box(9, 0, 10, 12, 16, 13)
    )
  };

  private static final VoxelShape[] AXIS_Z_AABBS = new VoxelShape[]{
    joinShapes(
      BooleanOp.OR,
      Block.box(9.5, 8.5, 0, 12.5, 11.5, 16),
      Block.box(4.5, 5.5, 0, 7.5, 8.5, 16)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(5, 4, 0, 8, 7, 16),
      Block.box(9.5, 4.5, 0, 12.5, 7.5, 16),
      Block.box(6.5, 9.5, 0.5, 9.5, 12.5, 16)
    ),
    joinShapes(
      BooleanOp.OR,
      Block.box(9.5, 4.5, 0, 12.5, 7.5, 16),
      Block.box(4.5, 9.5, 0, 7.5, 12.5, 16),
      Block.box(4, 5, 0, 7, 8, 16),
      Block.box(9, 10, 0, 12, 13, 16)
    )
  };

  public SeaPickledChainBlock(Block alternateBlock) {
    super(alternateBlock);
  }

  @Override
  public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
    int index = blockState.getValue(QUANTITY) - 2;
    Direction.Axis axis = blockState.getValue(AXIS);

    if (axis == X)
      return AXIS_X_AABBS[index];

    if (axis == Y)
      return AXIS_Y_AABBS[index];

    // (axis == Z)
    return AXIS_Z_AABBS[index];
  }

  @Override @ParametersAreNonnullByDefault
  public @NotNull VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
    return Shapes.block();
  }
}
