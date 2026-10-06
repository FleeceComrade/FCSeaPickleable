package com.fleece.fcseapickleable.block;

import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.fleece.fcseapickleable.FCSeaPickleable.FCSPConfig.isPottable;
import static com.fleece.fcseapickleable.FCSeaPickleable.joinShapes;
import static com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity.POTTABLE;

public class SeaPickledFlowerPotBlock extends AbstractSeaPickleableBlock implements EntityBlock {

  public static final VoxelShape[][] BOXES = new VoxelShape[][] {
    {
      Block.box(3, 0, 3, 9, 6, 9),
      Block.box(8, 0, 9, 13, 5, 14)
    },
    {
      Block.box(2, 0, 7, 8, 6, 13),
      Block.box(6, 0, 3, 10, 4, 7),
      Block.box(8, 0, 7, 13, 5, 12)
    },
    {

      Block.box(8, 0, 7, 14, 6, 13),
      Block.box(3, 0, 8, 8, 5, 13.01),
      Block.box(2, 0, 2, 8, 6, 8),
      Block.box(8, 0, 3, 12, 4, 7)
    }
  };

  public static final VoxelShape[] AABBS = new VoxelShape[]{
    joinShapes(BooleanOp.OR, BOXES[0]),
    joinShapes(BooleanOp.OR, BOXES[1]),
    joinShapes(BooleanOp.OR, BOXES[2])
  };

  public SeaPickledFlowerPotBlock(Block alternateBlock) {
    super(alternateBlock);
  }

  @Override
  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
    return AABBS[blockState.getValue(QUANTITY) - 2];
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new SeaPickledFlowerPotBlockEntity(blockPos, blockState);
  }

  @Override
  public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
    ItemStack inHand = player.getItemInHand(interactionHand);
    if (!(inHand.getItem() instanceof BlockItem blockItem)) return InteractionResult.PASS;
    if (!canBePotted(blockItem.getBlock())) return InteractionResult.PASS;

    BlockEntity be = level.getBlockEntity(blockPos);
    if (!(be instanceof SeaPickledFlowerPotBlockEntity flowerPots)) return InteractionResult.PASS;

    int hitIndex = firstHitIndex(player, blockPos, blockHitResult, BOXES[blockState.getValue(QUANTITY) - 2]);
    if (hitIndex < 0) return InteractionResult.PASS;

    if (!level.isClientSide) {
      if (flowerPots.canSetContents(hitIndex, blockItem.getBlock())) {
        if (!player.isCreative()) {
          popResource(level, blockPos, new ItemStack(flowerPots.getContent(hitIndex)));
          inHand.shrink(1);
        }
        flowerPots.setContents(hitIndex, blockItem.getBlock());
      } else {
        if (!player.isShiftKeyDown()) {
          if (!player.isCreative())
            popResource(level, blockPos, new ItemStack(flowerPots.getContent(hitIndex)));
          flowerPots.removeContent(hitIndex);
        }
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  public static boolean canBePotted(Block block) {
    return block.builtInRegistryHolder().is(POTTABLE) || isPottable(block);
  }

  private static int firstHitIndex(Player player, BlockPos pos, BlockHitResult originalHit, VoxelShape[] shapes) {
    Vec3 start = player.getEyePosition();

    Vec3 hitLoc = originalHit.getLocation();
    Vec3 dir = hitLoc.subtract(start);
    double len = dir.length();
    if (len <= 1.0e-8) return -1;

    Vec3 n = dir.scale(1.0D / len);

    Vec3 end = hitLoc.add(n.scale(1.0e-4));

    int bestIndex = -1;
    double bestDistSqr = Double.POSITIVE_INFINITY;

    for (int i = 0; i < shapes.length; i++) {
      VoxelShape shape = shapes[i];
      if (shape == null || shape.isEmpty()) continue;

      BlockHitResult hit = shape.clip(start, end, pos);
      if (hit == null || hit.getType() == HitResult.Type.MISS) continue;

      double distSqr = start.distanceToSqr(hit.getLocation());
      if (distSqr < bestDistSqr) {
        bestDistSqr = distSqr;
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  @Override
  public boolean canBeReplaced(BlockState blockState, Fluid fluid) {
    return blockState.getValue(QUANTITY) < 4;
  }
}