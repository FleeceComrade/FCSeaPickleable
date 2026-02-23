package com.fleece.fcseapickleable.render;

import com.fleece.fcseapickleable.registry.FCSPBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class ColorManager {
    public static void register() {
         Minecraft.getInstance().getBlockColors().register(
           ColorManager::biomeShortGrassColor,
           FCSPBlocks.SEAPICKLED_FERN.get(), FCSPBlocks.SEAPICKLED_GRASS.get());
    }

    public static int biomeFoliageColor(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, int tintIndex) {
      return level != null && blockPos != null ? BiomeColors.getAverageFoliageColor(level, blockPos) : FoliageColor.getDefaultColor();
    }

    public static int biomeShortGrassColor(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, int tintIndex) {
      return level != null && blockPos != null ? BiomeColors.getAverageGrassColor(level, blockPos) : GrassColor.getDefaultColor();
    }
}
