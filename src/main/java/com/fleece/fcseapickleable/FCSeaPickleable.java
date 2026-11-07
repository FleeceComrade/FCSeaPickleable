package com.fleece.fcseapickleable;

import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes;
import com.fleece.fcseapickleable.render.ColorManager;
import com.fleece.fcseapickleable.util.SimpleImmutableReferenceBiMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.*;

import static com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes.BLOCK_ENTITY_TYPES;
import static com.fleece.fcseapickleable.registry.FCSPBlocks.BLOCKS;

@SuppressWarnings("unused")
@Mod(FCSeaPickleable.MODID)
public class FCSeaPickleable {
  public static final String MODID = "fcseapickleable";
  public static final SimpleImmutableReferenceBiMap<ResourceLocation, Block> POTTED_STUFF_BIMAP = new SimpleImmutableReferenceBiMap<>();
  public static final Map<Block, BakedModel> CUSTOM_POT_BY_BLOCK = new HashMap<>();


  public FCSeaPickleable(IEventBus eventBus, ModContainer modContainer) {
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::commonSetup);
    BLOCKS.register(eventBus);
    BLOCK_ENTITY_TYPES.register(eventBus);
  }

  public void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      Map<ResourceLocation, Block> pottedMap = new HashMap<>();
      ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().get())).forEach(entry -> pottedMap.put(entry.getKey(), entry.getValue()));
      POTTED_STUFF_BIMAP.init(pottedMap);
    });
  }

  public void clientSetup(final FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      BlockEntityRenderers.register(FCSPBlockEntityTypes.SEAPICKLED_FLOWERPOT.get(), SeaPickledFlowerPotBlockEntity.Renderer::new);
      ColorManager.register();
    });
  }

  @SuppressWarnings({"removal", "unused"})
  @EventBusSubscriber(
    modid = MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
  )
  public static final class ClientModEvents {

    public enum CustomModelPots {
      CACTUS(ResourceLocation.fromNamespaceAndPath("minecraft", "cactus")),
      BAMBOO(ResourceLocation.fromNamespaceAndPath("minecraft", "bamboo")),
      CRIMSON_ROOTS(ResourceLocation.fromNamespaceAndPath("minecraft", "crimson_roots")),
      WARPED_ROOTS(ResourceLocation.fromNamespaceAndPath("minecraft", "warped_roots")),
      AZALEA(ResourceLocation.fromNamespaceAndPath("minecraft", "azalea")),
      FLOWERING_AZALEA(ResourceLocation.fromNamespaceAndPath("minecraft", "flowering_azalea"));
      private final ResourceLocation blockId;

      private final ResourceLocation location = ResourceLocation.fromNamespaceAndPath(FCSeaPickleable.MODID, "block/pots/custom/" + this.name().toLowerCase());

      CustomModelPots(ResourceLocation blockId) {
        this.blockId = blockId;
      }

      public static void populateMap(Map<ModelResourceLocation, BakedModel> models) {
        CUSTOM_POT_BY_BLOCK.clear();
        for (CustomModelPots value : CustomModelPots.values())
          CUSTOM_POT_BY_BLOCK.put(BuiltInRegistries.BLOCK.get(value.blockId), models.get(ModelResourceLocation.standalone(value.location)));
      }
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
      for (CustomModelPots pot : CustomModelPots.values()) {
        event.register(ModelResourceLocation.standalone(pot.location));
      }
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
      CustomModelPots.populateMap(event.getModels());
    }
  }

  //Todo: move these methods to PlanarCore someday
  /**
   * Combines an array of VoxelShapes down to one shape according to the given BooleanOp.
   * We use a pyramidal method in order to more gradually scale the size of shapes being combined.
   * This is more efficient for VoxelShapes than the normal Stream.reduce() because combining a very large,
   * complex VoxelShape can be incredibly slow.
   */
  public static VoxelShape joinShapes(BooleanOp op, VoxelShape... shapes) {
    Stack<VoxelShape> stack = new Stack<>();
    for (VoxelShape shape : shapes) stack.push(shape);

    while (stack.size() > 1)
      stack.add(0, Shapes.join(stack.pop(), stack.pop(), op));

    return stack.pop();
  }


  public static BlockState copyAllSharedProperties(BlockState from, BlockState to, Property<?>... excluded) {
    for (Property<?> property : from.getProperties()) {
      if (!Arrays.stream(excluded).toList().contains(property))
        to = copyIfPresent(from, to, property);
    }
    return to;
  }

  public static <T extends Comparable<T>> BlockState copyIfPresent(
    BlockState from, BlockState to, Property<T> property
  ) {
    if (to.hasProperty(property)) {
      return to.setValue(property, from.getValue(property));
    }
    return to;
  }
}