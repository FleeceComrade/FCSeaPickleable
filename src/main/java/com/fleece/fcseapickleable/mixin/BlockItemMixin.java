package com.fleece.fcseapickleable.mixin;

import com.fleece.fcseapickleable.block.AbstractSeaPickleableBlock;
import com.fleece.fcseapickleable.block.ISeaPickleableBlock;
import com.fleece.fcseapickleable.block.SeaPickledFlowerPotBlock;
import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import static com.fleece.fcseapickleable.FCSeaPickleable.POTTED_STUFF_BIMAP;
import static com.fleece.fcseapickleable.block.AbstractSeaPickleableBlock.QUANTITY;
import static net.minecraft.world.level.block.Block.popResource;

@Mixin(BlockItem.class)
public class BlockItemMixin {
  private static final ThreadLocal<Map<Integer, String>> potData = ThreadLocal.withInitial(HashMap::new);

  @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
  private void fc$onGetPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    if (this.getBlock() instanceof ISeaPickleableBlock iSeaPickleable) {
      BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
      if (inWorld.is(this.getBlock()) || inWorld.is(iSeaPickleable.alternateBlock())) {
        if (inWorld.getBlock() instanceof SeaPickledFlowerPotBlock flowerPotBlock && inWorld.getValue(QUANTITY) == 4) {
          BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
          if (blockEntity instanceof SeaPickledFlowerPotBlockEntity flowerPotBlockEntity) {
            cir.setReturnValue(POTTED_STUFF_BIMAP.getValueOrDefault(ForgeRegistries.BLOCKS.getKey(flowerPotBlockEntity.getContent(0)), Blocks.FLOWER_POT).defaultBlockState());
          }
        } else {
          cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(inWorld));
        }
      } else if (this.getBlock().equals(Blocks.FLOWER_POT) && inWorld.is(BlockTags.FLOWER_POTS)) {
        cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(Blocks.FLOWER_POT.defaultBlockState()));
      }
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
  private void fc$onPlaceBlock(BlockPlaceContext context, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    BlockState thisPlacement = this.getPlacementState(context);
    BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
    ItemStack itemstack = context.getItemInHand();
    if (thisPlacement.getBlock() instanceof ISeaPickleableBlock) {
      if (inWorld.getBlock() instanceof AbstractSeaPickleableBlock && !(thisPlacement.getBlock() instanceof AbstractSeaPickleableBlock))
        itemstack.setCount(Math.min(itemstack.getMaxStackSize(), itemstack.getCount() + 4));
      if (inWorld.getBlock() instanceof SeaPickledFlowerPotBlock || inWorld.is(BlockTags.FLOWER_POTS) && !inWorld.is(Blocks.FLOWER_POT)) {
        SeaPickledFlowerPotBlockEntity flowerPotBlockEntity = (SeaPickledFlowerPotBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if (flowerPotBlockEntity != null) {
          for (int i = 0; i < inWorld.getValue(QUANTITY); i++) {
            if (flowerPotBlockEntity.getContent(i).equals(Blocks.AIR)) continue;
            potData.get().put(i, ForgeRegistries.BLOCKS.getKey(flowerPotBlockEntity.getContent(i)).toString());
          }
        } else {
          potData.get().put(0, POTTED_STUFF_BIMAP.getKeyOrDefault(inWorld.getBlock(), ResourceLocation.parse("minecraft:air")).toString());
        }
      }
    }
  }

  @Inject(
    method = "place",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/InteractionResult;sidedSuccess(Z)Lnet/minecraft/world/InteractionResult;",
      shift = At.Shift.BEFORE
    )
  )
  @SuppressWarnings("DataFlowIssue")
  private void fc$afterSuccessfulPlacement(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
    if (potData.get().isEmpty()) return;
    BlockState state = context.getLevel().getBlockState(context.getClickedPos());
    if (state.getBlock() instanceof SeaPickledFlowerPotBlock && state.getValue(QUANTITY) == 2) {
      ((SeaPickledFlowerPotBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos())).setContents(0, ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(potData.get().get(0))));
    } else if (state.is(BlockTags.FLOWER_POTS)) {
      potData.get().replace(0, "minecraft:air");
      for (String pottedBlockId : potData.get().values())
        if (!context.getPlayer().isCreative())
          popResource(context.getLevel(), context.getClickedPos(), new ItemStack(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(pottedBlockId))));
    }
    potData.get().clear();
    System.out.println("pot data cleared");
  }

  @Inject(
    method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
    at = @At("TAIL")
  )
  private void fc$updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (potData.get().isEmpty()) return;

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof SeaPickledFlowerPotBlockEntity flowerPotBlockEntity)) return;

    for (Map.Entry<Integer, String> entry : potData.get().entrySet()) {
      Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(entry.getValue()));
      if (block != null)
        flowerPotBlockEntity.setContents(entry.getKey(), block);
    }

    flowerPotBlockEntity.setChanged();

    if (!level.isClientSide) {
      level.sendBlockUpdated(pos, state, state, 3);
    }
  }

  @Shadow
  public Block getBlock() {
    return null;
  }

  @Shadow
  public BlockState getPlacementState(BlockPlaceContext context) {
    return null;
  }
}