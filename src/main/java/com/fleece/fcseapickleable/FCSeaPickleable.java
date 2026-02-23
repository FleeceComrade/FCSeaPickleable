package com.fleece.fcseapickleable;

import com.fleece.fcseapickleable.render.ColorManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.Stack;

import static com.fleece.fcseapickleable.registry.FCSPBlocks.BLOCKS;

@Mod(FCSeaPickleable.MODID)
public class FCSeaPickleable {
  public static final String MODID = "fcseapickleable";

  public FCSeaPickleable(FMLJavaModLoadingContext context) {
    IEventBus bus = context.getModEventBus();
    bus.addListener(this::clientSetup);
    BLOCKS.register(bus);
  }

  public void clientSetup(final FMLClientSetupEvent event) {
    ColorManager.register();
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

  public static <T extends Comparable<T>> BlockState copyIfPresent(
    BlockState from, BlockState to, Property<T> property
  ) {
    if (to.hasProperty(property)) {
      return to.setValue(property, from.getValue(property));
    }
    return to;
  }

  public static BlockState copyAllSharedProperties(BlockState from, BlockState to, Property<?>... excluded) {
    for (Property<?> property : from.getProperties()) {
      if (!Arrays.stream(excluded).toList().contains(property))
        to = copyIfPresent(from, to, property);
    }
    return to;
  }
}