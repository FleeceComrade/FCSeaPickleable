package com.fleece.fcseapickleable.block;

import com.fleece.fcseapickleable.interfaces.ISeaPickleableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.fleece.fcseapickleable.FCSeaPickleable.copyAllSharedProperties;

@SuppressWarnings({"deprecation", "NullableProblems"})
public abstract class AbstractSeaPickleableBlock extends Block implements ISeaPickleableBlock {
  public static final IntegerProperty QUANTITY = IntegerProperty.create("quantity", 2, 4);

  private static final ThreadLocal<Block> CTOR_ALTERNATE = new ThreadLocal<>();

  private final Block alternateBlock;

  public AbstractSeaPickleableBlock(Block alternateBlock) {
    super(initProps(alternateBlock));
    this.alternateBlock = alternateBlock;
    CTOR_ALTERNATE.remove();
    this.registerDefaultState(copyAllSharedProperties(alternateBlock.defaultBlockState(), this.stateDefinition.any()).setValue(QUANTITY, 2));
  }

  private static Properties initProps(Block alternateBlock) {
    CTOR_ALTERNATE.set(alternateBlock);
    return Properties.copy(alternateBlock);
  }

  /**
   * This should be the only method that ALL subclasses <i>must</i> implement.
   */
  @Override
  public abstract VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext);

  /**
   * The following are normal methods of Block being deferred to the SeaPickleableBlock's alternate.
   * These <i>Shouldn't</i> be overridden but will not be made final in case they're needed for some unforeseen reason
   */

  public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos blockPos) {
    return alternateBlock().canSurvive(state, levelReader, blockPos);
  }


  public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
    return alternateBlock().updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
  }

  public FluidState getFluidState(BlockState blockState) {
    return alternateBlock().getFluidState(blockState);
  }

  public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType type) {
    return alternateBlock().isPathfindable(blockState, blockGetter, blockPos, type);
  }

  /**
   * The following methods are final because subclasses should not be changing these; if they are trying to, they aren't a true to theme "SeaPickleableBlock"
   * **/


  @Override
  public final BlockState seaPickled(BlockState toPickle) {
    return copyAllSharedProperties(toPickle, getNextQuantityState(toPickle), QUANTITY);
  }

  @Override
  public final Block alternateBlock() {
    return this.alternateBlock;
  }

  @Override
  public final List<ItemStack> getDrops(BlockState blockState, LootParams.Builder lootParamsBuilder) {
    List<ItemStack> drops = new ArrayList<>();
    for (int i = 0; i < blockState.getValue(QUANTITY); i++) {
      drops.addAll(alternateBlock().getDrops(blockState, lootParamsBuilder).stream().filter(itemStack -> {
        for (ItemStack drop : drops) {
          if (drop.getItem().equals(itemStack.getItem())) {
            drop.grow(itemStack.getCount());
            return false;
          }
        }
        return true;
      }).toList());
    }
    return drops;
  }

  @Override
  public final ItemStack getCloneItemStack(BlockState blockState, HitResult hitResult, BlockGetter blockGetter, BlockPos blockPos, Player player) {
    return alternateBlock().getCloneItemStack(blockState, hitResult, blockGetter, blockPos, player);
  }

  @Override
  public final BlockState getStateForPlacement(BlockPlaceContext context) {
    return seaPickled(context.getLevel().getBlockState(context.getClickedPos()));
  }

  @Override
  public final Item asItem() {
    return alternateBlock().asItem();
  }

  @Override
  public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) || super.canBeReplaced(blockState, context);
  }

  @Override
  public final void fc$onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {}

  private BlockState getNextQuantityState(BlockState blockState) {
    BlockState toReturn = blockState.getValue(QUANTITY) == 4 ? alternateBlock().defaultBlockState() : blockState.cycle(QUANTITY);
    return toReturn;
  }

  @Override
  protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);

    Block alt = CTOR_ALTERNATE.get();
    if (alt == null) throw new IllegalStateException("alternateBlock not available");

    for (Property<?> property : alt.defaultBlockState().getProperties()) {
      builder.add(property);
    }

    builder.add(QUANTITY);
  }
}