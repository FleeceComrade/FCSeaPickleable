package com.fleece.fcseapickleable.blockentity;

import com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import static com.fleece.fcseapickleable.FCSeaPickleable.*;
import static com.fleece.fcseapickleable.block.AbstractSeaPickleableBlock.QUANTITY;

@SuppressWarnings("deprecation")
public class SeaPickledFlowerPotBlockEntity extends BlockEntity {
  public static final TagKey<Block> POTTABLE =
    BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "can_be_potted"));

  private static final int MAX_CONTENTS = 4;

  private final Block[] contents = new Block[]{
    Blocks.AIR,
    Blocks.AIR,
    Blocks.AIR,
    Blocks.AIR
  };

  public SeaPickledFlowerPotBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(FCSPBlockEntityTypes.SEAPICKLED_FLOWERPOT.get(), blockPos, blockState);
  }

  public int getQuantity() {
    return getBlockState().getValue(QUANTITY);
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean canSetContents(int index, Block block) {
    if (index < 0 || index >= MAX_CONTENTS || index >= getQuantity() || block.equals(this.getContent(index))) {
      return false;
    }

    return block.builtInRegistryHolder().is(POTTABLE);
  }

  public void setContents (int index, Block block) {
    if (!canSetContents(index, block)) return;
    contents[index] = block;
    setChanged();
    sync();
  }

  public Block getContent(int index) {
    if (index < 0 || index >= MAX_CONTENTS || index >= getQuantity()) {
      return Blocks.AIR;
    }

    return contents[index];
  }

  private void sync() {
    if (level == null || level.isClientSide) {
      return;
    }

    BlockState state = getBlockState();
    level.sendBlockUpdated(
      worldPosition,
      state,
      state,
      Block.UPDATE_CLIENTS
    );
  }

  private void saveContents(CompoundTag tag) {
    for (int i = 0; i < contents.length; i++) {
      ResourceLocation id = BuiltInRegistries.BLOCK.getKey(contents[i]);
      tag.putString("content_" + i, id.toString());
    }
  }

  private void loadContents(CompoundTag tag) {
    for (int i = 0; i < contents.length; i++) {
      String key = "content_" + i;

      if (!tag.contains(key)) {
        contents[i] = Blocks.AIR;
        continue;
      }

      ResourceLocation id = ResourceLocation.tryParse(tag.getString(key));

      if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) {
        contents[i] = Blocks.AIR;
        continue;
      }

      contents[i] = BuiltInRegistries.BLOCK.get(id);
    }
  }

  @Override
  protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    super.saveAdditional(tag, registries);
    saveContents(tag);
  }

  @Override
  protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    super.loadAdditional(tag, registries);
    loadContents(tag);
  }

  @Override
  public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
    CompoundTag tag = new CompoundTag();
    saveContents(tag);
    return tag;
  }

  @Override
  public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
    loadContents(tag);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.@NotNull Provider lookupProvider) {
    CompoundTag tag = packet.getTag();

    handleUpdateTag(tag, lookupProvider);
  }

  public void removeContent(int index) {
    contents[index] = Blocks.AIR;
    setChanged();
    sync();
  }

  public static class Renderer implements BlockEntityRenderer<SeaPickledFlowerPotBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public enum CoordsByQuantity {
      TWO(
        new Coords(6.0, 4.0, 6.0, 0.75f),
        new Coords(10.5, 4.0, 11.5, 0.625f)
      ),
      THREE(
        new Coords(5.0, 4.0, 10.0, 0.75f),
        new Coords(8.0, 3.0, 5.0, 0.5f),
        new Coords(10.5, 4.0, 9.5, 0.625f)
      ),
      FOUR(
        new Coords(11.0, 4.01, 10.0, 0.75f),
        new Coords(5.5, 4.0, 10.5, 0.625f),
        new Coords(5.0, 4.0, 5.0, 0.75f),
        new Coords(10.0, 3.0, 5.0, 0.5f)
      );

      private final Coords[] coords;

      CoordsByQuantity(Coords... coords) {
        this.coords = coords;
      }

      private static Coords get(int quantity, int index) {
        return CoordsByQuantity.values()[quantity - 2].coords[index];
      }

      private record Coords(double x, double y, double z, float scale) {}
    }

    public Renderer(BlockEntityRendererProvider.Context context) {
      this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void render(
      SeaPickledFlowerPotBlockEntity blockEntity,
      float partialTick,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
    ) {
      int quantity = blockEntity.getQuantity();

      for (int i = 0; i < quantity; i++) {
        BlockState state = blockEntity.getContent(i).defaultBlockState();
        CoordsByQuantity.Coords coords = CoordsByQuantity.get(quantity, i);

        poseStack.pushPose();

        poseStack.translate(
          coords.x() / 16.0,
          coords.y() / 16.0,
          coords.z() / 16.0
        );

        if (CUSTOM_POT_BY_BLOCK.containsKey(state.getBlock())) {
          BakedModel model = CUSTOM_POT_BY_BLOCK.get(state.getBlock());

          float scale = Math.min(1f, coords.scale() * 1.33f);
          poseStack.scale(scale, scale, scale);
          poseStack.translate(-0.5, -0.25, -0.5);

          VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());

          Level level = blockEntity.getLevel();
          BlockPos pos = blockEntity.getBlockPos();
          BlockState potState = blockEntity.getBlockState();

          blockRenderer.getModelRenderer().tesselateBlock(
            level,
            model,
            potState,
            pos,
            poseStack,
            consumer,
            false,
            level.getRandom(),
            potState.getSeed(pos),
            packedOverlay,
            ModelData.EMPTY,
            RenderType.cutout()
          );
        } else {
          poseStack.scale(coords.scale(), coords.scale(), coords.scale());
          poseStack.translate(-0.5, 0.0, -0.5);

          blockRenderer.renderSingleBlock(
            state,
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay
          );
        }

        poseStack.popPose();
      }
    }
  }
}