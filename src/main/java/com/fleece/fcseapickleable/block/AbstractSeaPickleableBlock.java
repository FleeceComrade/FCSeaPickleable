package com.fleece.fcseapickleable.block;

import com.fleece.fcseapickleable.interfaces.ISeaPickleableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.fleece.fcseapickleable.FCSeaPickleable.copyAllSharedProperties;

@SuppressWarnings({"deprecation", "NullableProblems"})
public abstract class AbstractSeaPickleableBlock extends Block implements ISeaPickleableBlock {
  public static final IntegerProperty QUANTITY = IntegerProperty.create("quantity", 2, 4);

  public AbstractSeaPickleableBlock(Properties properties) {
    super(properties);
  }

  /**
   * Subclasses MUST call super.createBlockStateDefinition
   * **/
  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(QUANTITY);
  }


  /**
   * The following methods are final because subclasses should not be changing these; if they are trying to, they aren't a true to theme "SeaPickleableBlock"
   * **/

  @Override
  public final BlockState seaPickled(BlockState toPickle) {
    return copyAllSharedProperties(toPickle, getNextQuantityState(toPickle), QUANTITY);
  }

  @Override
  public final ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    return alternateBlock().getCloneItemStack(state, target, level, pos, player);
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
  public final void onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {}

  private BlockState getNextQuantityState(BlockState blockState) {
    BlockState toReturn = blockState.getValue(QUANTITY) == 4 ? alternateBlock().defaultBlockState() : blockState.cycle(QUANTITY);
    System.out.println("toReturn? " + toReturn);
    return toReturn;
  }
}