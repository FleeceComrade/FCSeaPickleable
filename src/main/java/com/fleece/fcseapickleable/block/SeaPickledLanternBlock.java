package com.fleece.fcseapickleable.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.fleece.fcseapickleable.FCSeaPickleable.joinShapes;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class SeaPickledLanternBlock extends AbstractSeaPickleableBlock implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
  private static final VoxelShape[] HANGING_AABBS = new VoxelShape[] {
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 1, 7, 14, 8, 13),
      Block.box(9, 8, 8, 13, 10, 12),
      Block.box(4, 4, 5, 8, 10, 9)
    ),
    Block.box(0, 0, 0, 16, 16, 16),
    Block.box(0, 0, 0, 16, 16, 16)
  };
  private static final VoxelShape[] AABBS = new VoxelShape[] {
    joinShapes(
      BooleanOp.OR,
      Block.box(8, 0, 7, 14, 7, 13),
      Block.box(9, 7, 8, 13, 9, 12),
      Block.box(4, 0, 5, 8, 6, 9)
    ),
    Block.box(0, 0, 0, 16, 16, 16),
    Block.box(0, 0, 0, 16, 16, 16)
  };

  public SeaPickledLanternBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(stateDefinition.any().setValue(QUANTITY, 2).setValue(WATERLOGGED, false).setValue(HANGING, false));
  }

  @Override
  public Block alternateBlock() {
    return Blocks.LANTERN;
  }

  //Todo: CAN THIS BE DEFERRED TO ALTERNATE??
  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(WATERLOGGED, HANGING);
  }

  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
    int index = blockState.getValue(QUANTITY) - 2;
    return blockState.getValue(HANGING) ? HANGING_AABBS[index] : AABBS[index];
  }

  //Todo: vvv find out if I can defer all of these to alternateBlock().suchAndSuch() vvv

  public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos blockPos) {
    Direction direction = getConnectedDirection(state).getOpposite();
    return Block.canSupportCenter(levelReader, blockPos.relative(direction), direction.getOpposite());
  }

  protected static Direction getConnectedDirection(BlockState blockState) {
    return blockState.getValue(HANGING) ? Direction.DOWN : Direction.UP;
  }

  public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
    if (blockState.getValue(WATERLOGGED)) {
      levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
    }

    return getConnectedDirection(blockState).getOpposite() == direction && !blockState.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
  }

  public FluidState getFluidState(BlockState blockState) {
    return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
  }

  public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType type) {
    return false;
  }
}
