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
  /**
   * Carries flower-pot contents across the vanilla placement pipeline. Placement invokes several
   * methods before the replacement block entity exists, so this temporary per-thread state bridges
   * the old block state and the newly-created {@link SeaPickledFlowerPotBlockEntity}.
   */
  private static final ThreadLocal<Map<Integer, String>> potData = ThreadLocal.withInitial(HashMap::new);

  /**
   * Chooses the block state that will replace the clicked block before vanilla places the item.
   * This turns a matching vanilla block into a two-item cluster, advances an existing cluster, or
   * converts a full flower-pot cluster back into its first contained vanilla flower pot.
   */
  @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
  private void fc$onGetPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
    // Only vanilla blocks augmented by a mixin, or this mod's clustered blocks, participate.
    if (this.getBlock() instanceof ISeaPickleableBlock iSeaPickleable && iSeaPickleable.isPickleable()) {
      BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());

      // Clustering is allowed only when the clicked block is the same item or its vanilla counterpart.
      if (inWorld.is(this.getBlock()) || inWorld.is(iSeaPickleable.alternateBlock())) {
        // A full custom flower-pot cluster converts into the first stored vanilla potted-block state.
        if (inWorld.getBlock() instanceof SeaPickledFlowerPotBlock flowerPotBlock && inWorld.getValue(QUANTITY) == 4) {
          BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());

          // The block entity owns pot contents; use an empty flower pot when its first content is unmapped.
          if (blockEntity instanceof SeaPickledFlowerPotBlockEntity flowerPotBlockEntity) {
            cir.setReturnValue(POTTED_STUFF_BIMAP.getValueOrDefault(ForgeRegistries.BLOCKS.getKey(flowerPotBlockEntity.getContent(0)), Blocks.FLOWER_POT).defaultBlockState());
          }
        } else {
          // Preserve shared properties while creating or incrementing the custom clustered state.
          cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(inWorld));
        }

      // A vanilla flower pot can begin a cluster when the clicked pot already contains a plant.
      } else if (this.getBlock().equals(Blocks.FLOWER_POT) && inWorld.is(BlockTags.FLOWER_POTS)) {
        cir.setReturnValue(((ISeaPickleableBlock) inWorld.getBlock()).seaPickled(Blocks.FLOWER_POT.defaultBlockState()));
      }
    }
  }

  /**
   * Captures the contents of a flower pot before vanilla replaces its block entity. It also restores
   * the consumed-item count when a full custom cluster is converted back to its vanilla block.
   */
  @SuppressWarnings("DataFlowIssue")
  @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
  private void fc$onPlaceBlock(BlockPlaceContext context, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    BlockState thisPlacement = this.getPlacementState(context);
    BlockState inWorld = context.getLevel().getBlockState(context.getClickedPos());
    ItemStack itemstack = context.getItemInHand();

    // Continue only when the placement state is a participating clustered block.
    if (thisPlacement.getBlock() instanceof ISeaPickleableBlock iSeaPickleableBlock && iSeaPickleableBlock.isPickleable()) {
      // Replacing a full custom cluster with a vanilla block returns the four clustered items.
      if (inWorld.getBlock() instanceof AbstractSeaPickleableBlock && !(thisPlacement.getBlock() instanceof AbstractSeaPickleableBlock))
        itemstack.setCount(Math.min(itemstack.getMaxStackSize(), itemstack.getCount() + 4));

      //Todo: get the bugs out of this code!!

      // Both custom and occupied vanilla flower pots need their contents preserved before replacement.
      if (inWorld.getBlock() instanceof SeaPickledFlowerPotBlock || inWorld.is(BlockTags.FLOWER_POTS) && !inWorld.is(Blocks.FLOWER_POT)) {
        SeaPickledFlowerPotBlockEntity flowerPotBlockEntity = (SeaPickledFlowerPotBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());

        // A custom cluster has its own block entity, so save every occupied pot slot by registry ID.
        if (flowerPotBlockEntity != null) {
          for (int i = 0; i < inWorld.getValue(QUANTITY); i++) {
            if (flowerPotBlockEntity.getContent(i).equals(Blocks.AIR)) continue;
            potData.get().put(i, ForgeRegistries.BLOCKS.getKey(flowerPotBlockEntity.getContent(i)).toString());
          }
        } else {
          // A vanilla potted block has no custom entity; derive its single plant from the reverse map.
          potData.get().put(0, POTTED_STUFF_BIMAP.getKeyOrDefault(inWorld.getBlock(), ResourceLocation.parse("minecraft:air")).toString());
        }
      }
    }
  }

  /**
   * Runs after vanilla reports a successful placement. It restores contents for a newly-created
   * two-pot cluster, or drops preserved plant contents when the result is again a vanilla pot.
   */
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
    // Non-pot placements have no captured contents to restore or drop.
    if (potData.get().isEmpty()) return;
    BlockState state = context.getLevel().getBlockState(context.getClickedPos());

    // Seed the first slot after a vanilla pot becomes a new two-pot custom cluster.
    if (state.getBlock() instanceof SeaPickledFlowerPotBlock && state.getValue(QUANTITY) == 2) {
      ((SeaPickledFlowerPotBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos())).setContents(0, ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(potData.get().get(0))));

    // Returning to a vanilla pot leaves only one plant in its state; drop any additional saved plants.
    } else if (state.is(BlockTags.FLOWER_POTS)) {
      potData.get().replace(0, "minecraft:air");
      for (String pottedBlockId : potData.get().values())
        // Creative placement should not create item drops.
        if (!context.getPlayer().isCreative())
          popResource(context.getLevel(), context.getClickedPos(), new ItemStack(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(pottedBlockId))));
    }

    // The captured data applies to exactly one successful placement.
    potData.get().clear();
  }

  /**
   * Restores captured pot contents after vanilla has created and loaded the replacement block
   * entity. This is the reliable point at which all slots of an expanded custom cluster exist.
   */
  @Inject(
    method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
    at = @At("TAIL")
  )
  private void fc$updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
    // Skip ordinary placements, which never captured flower-pot data.
    if (potData.get().isEmpty()) return;

    BlockEntity blockEntity = level.getBlockEntity(pos);
    // Only the custom flower-pot entity can receive the captured slots.
    if (!(blockEntity instanceof SeaPickledFlowerPotBlockEntity flowerPotBlockEntity)) return;

    for (Map.Entry<Integer, String> entry : potData.get().entrySet()) {
      Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(entry.getValue()));
      // Ignore missing registry entries rather than writing an invalid content block.
      if (block != null)
        flowerPotBlockEntity.setContents(entry.getKey(), block);
    }

    // Mark persisted state dirty after all restored slots have been applied.
    flowerPotBlockEntity.setChanged();

    // Notify clients so the renderer receives the restored contents immediately.
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
