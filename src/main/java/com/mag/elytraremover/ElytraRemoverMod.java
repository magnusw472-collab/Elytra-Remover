package com.mag.elytraremover;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Strips elytras out of item frames the moment a chunk is first ever
 * generated in the End - i.e. before any player could possibly have
 * reached it yet. CHUNK_GENERATE fires exactly once per chunk (on its
 * first-ever generation), unlike CHUNK_LOAD which fires every time any
 * chunk loads/reloads - so this never touches already-explored territory,
 * including any elytra a player has since placed in a frame themselves.
 */
public class ElytraRemoverMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerChunkEvents.CHUNK_GENERATE.register(ElytraRemoverMod::stripFreshElytras);
    }

    private static void stripFreshElytras(ServerWorld world, WorldChunk chunk) {
        if (world.getRegistryKey() != World.END) {
            return;
        }

        ChunkPos pos = chunk.getPos();
        Box box = new Box(
                pos.getStartX(), world.getBottomY(), pos.getStartZ(),
                pos.getStartX() + 16, world.getTopY(), pos.getStartZ() + 16
        );

        for (ItemFrameEntity frame : world.getEntitiesByClass(ItemFrameEntity.class, box, e -> true)) {
            ItemStack held = frame.getHeldItemStack();
            if (held.isOf(Items.ELYTRA)) {
                frame.setHeldItemStack(ItemStack.EMPTY);
            }
        }
    }
}
