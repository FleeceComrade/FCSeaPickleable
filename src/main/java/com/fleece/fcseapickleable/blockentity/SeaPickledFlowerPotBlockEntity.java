package com.fleece.fcseapickleable.blockentity;

import com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.fleece.fcseapickleable.FCSeaPickleable.MODID;
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

  public boolean canSetContents(int index, Block block) {
    if (index < 0 || index >= MAX_CONTENTS || index >= getQuantity() || block.equals(this.getContent(index))) {
      return false;
    }

    if (!block.builtInRegistryHolder().is(POTTABLE)) {
      return false;
    }

    return true;
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
  protected void saveAdditional(@NotNull CompoundTag tag) {
    super.saveAdditional(tag);
    saveContents(tag);
  }

  @Override
  public void load(@NotNull CompoundTag tag) {
    super.load(tag);
    loadContents(tag);
  }

  @Override
  public @NotNull CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    saveContents(tag);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    loadContents(tag);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void onDataPacket(
    Connection connection,
    ClientboundBlockEntityDataPacket packet
  ) {
    CompoundTag tag = packet.getTag();

    if (tag != null) {
      handleUpdateTag(tag);
    }
  }

  public void removeContent(int index) {
    contents[index] = Blocks.AIR;
    setChanged();
    sync();
  }

  public static class Renderer implements BlockEntityRenderer<SeaPickledFlowerPotBlockEntity> {

    public Renderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
      SeaPickledFlowerPotBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      @NotNull MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
    ) {
      Minecraft mc = Minecraft.getInstance();
      Font font = mc.font;

      poseStack.pushPose();
      poseStack.translate(0.5D, 0.8D, 0.5D);
      poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
      poseStack.scale(-0.01F, -0.01F, 0.01F);

      int lineHeight = font.lineHeight;
      int quantity = blockEntity.getQuantity();

      for (int i = 0; i < quantity; i++) {
        Block content = blockEntity.getContent(i);
        String id = BuiltInRegistries.BLOCK.getKey(content).toString();
        String text = i + ": " + id;

        float x = -font.width(text) / 2.0F;
        float y = i * lineHeight;

        font.drawInBatch(
          text,
          x,
          y,
          0xFFFFFFFF,
          false,
          poseStack.last().pose(),
          bufferSource,
          Font.DisplayMode.NORMAL,
          0,
          packedLight
        );
      }

      poseStack.popPose();
    }
  }
}