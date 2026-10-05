package com.fleece.fcseapickleable;

import com.fleece.fcseapickleable.blockentity.SeaPickledFlowerPotBlockEntity;
import com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes;
import com.fleece.fcseapickleable.render.ColorManager;
import com.fleece.fcseapickleable.util.SimpleImmutableReferenceBiMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fleece.fcseapickleable.registry.FCSPBlockEntityTypes.BLOCK_ENTITY_TYPES;
import static com.fleece.fcseapickleable.registry.FCSPBlocks.BLOCKS;
import static net.minecraftforge.fml.config.ModConfig.Type.COMMON;

@Mod(FCSeaPickleable.MODID)
public class FCSeaPickleable {
  //Todo: Flower Pots
  //Todo: translations
  public static final String MODID = "fcseapickleable";
  public static SimpleImmutableReferenceBiMap<ResourceLocation, Block> POTTED_STUFF_BIMAP = new SimpleImmutableReferenceBiMap<>();

  public FCSeaPickleable(FMLJavaModLoadingContext context) {
    IEventBus bus = context.getModEventBus();
    bus.addListener(this::clientSetup);
    bus.addListener(this::commonSetup);
    BLOCKS.register(bus);
    BLOCK_ENTITY_TYPES.register(bus);
    context.registerConfig(COMMON, FCSPConfig.SPEC);
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

  public static String getNonPottedVariant(String potted) {
    return potted.replace("potted_", "");
  }

  @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class FCSPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> POTTABLE_BLOCK_STRINGS = BUILDER
      .comment("List of blocks that can be put into sea-pickled flower pots. There is a tag for this but I added this config for accessibility.")
      .defineListAllowEmpty("PottableBlocks", List.of(), FCSPConfig::validateBlockString);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CUSTOM_MODEL_POTTABLE_BLOCK_STRINGS = BUILDER
      .comment("List of blocks that should use a custom model when put into a sea-pickled flower pot. As opposed" +
        "to just scaling it's usual block model down. There is also a tag for this but again, accessibility.")
      .defineListAllowEmpty("CustomModelPottableBlocks", List.of(), FCSPConfig::validateBlockString);



    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static Set<Block> pottableBlocks;
    private static Set<Block> customModelPottableBlocks;

    private static boolean validateBlockString(Object blockString) {
      return ForgeRegistries.BLOCKS.containsKey(ResourceLocation.parse(blockString.toString()));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
      pottableBlocks = POTTABLE_BLOCK_STRINGS.get().stream()
        .map(blockName -> ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(blockName)))
        .collect(Collectors.toSet());

      customModelPottableBlocks = CUSTOM_MODEL_POTTABLE_BLOCK_STRINGS.get().stream()
        .map(blockName -> ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(blockName)))
        .collect(Collectors.toSet());
    }

    public static boolean isPottable(Block block) {
      return pottableBlocks.contains(block);
    }

    public static boolean hasCustomPottedModel(Block block) {
      return customModelPottableBlocks.contains(block);
    }
  }
}