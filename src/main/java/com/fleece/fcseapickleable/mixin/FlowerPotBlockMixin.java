package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"SameReturnValue", "unused"})
@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin extends Block implements ISeaPickleableBlock {

  public FlowerPotBlockMixin() {
    super(Properties.of());
  }

  @Shadow
  public FlowerPotBlock getEmptyPot() {
    return null;
  }

  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
  private void onUseItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit,
                                  CallbackInfoReturnable<ItemInteractionResult> cir) {
    if (this.isPickleable() && !player.isSecondaryUseActive() && stack.is(Items.FLOWER_POT)) {
      cir.setReturnValue(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    }
  }

  @Override
  public boolean isPickleable() {
    return this.getEmptyPot().equals(Blocks.FLOWER_POT);
  }

  @Override
  public Block alternateBlock() {
    return FCSPBlocks.SEAPICKLED_FLOWER_POT.get();
  }

  @Override
  public boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext context) {
    return !context.isSecondaryUseActive() && context.getItemInHand().is(Items.FLOWER_POT) || super.canBeReplaced(blockState, context);
  }
}
