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
 * Strips elytras out of item frames in the End.
 *
 * Originally hooked CHUNK_GENERATE (fires once, at first-ever generation),
 * but that turned out to fire too early: entities embedded in a structure
 * (like an end ship's item frame) aren't actually spawned into the live
 * world until the chunk is later promoted to a fully-ticking state, which
 * only happens once something (a player) gets close enough. CHUNK_GENERATE
 * ran before that happened, so it found nothing to strip.
 *
 * CHUNK_LOAD fires later - specifically once "the chunk is already in the
 * level" (per Fabric's own docs), guaranteeing any structure-spawned
 * entities are present by then. The trade-off: CHUNK_LOAD fires every time
 * a chunk loads, not just its first generation, so this now runs every
 * time any chunk in the End loads. That's harmless for genuine ship loot
 * (once it's gone, it's gone - nothing left to strip on repeat loads), but
 * if a player ever deliberately places their own elytra into a decorative
 * item frame somewhere in the End, it would get stripped back out the next
 * time that chunk reloads. Let me know if that edge case matters to you -
 * it can be fixed by additionally checking the frame's position against
 * generated end_city structure bounds, at the cost of more complex code.
 */
public class ElytraRemoverMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerChunkEvents.CHUNK_LOAD.register(ElytraRemoverMod::stripFreshElytras);
    }

    private static void stripFreshElytras(ServerWorld world, WorldChunk chunk) {
        if (world.getRegistryKey() != World.END) {
            return;
        }

        ChunkPos pos = chunk.getPos();
        Box box = new Box(
                pos.getStartX(), world.getBottomY(), pos.getStartZ(),
                pos.getStartX() + 16, world.getTopYInclusive() + 1, pos.getStartZ() + 16
        );

        for (ItemFrameEntity frame : world.getEntitiesByClass(ItemFrameEntity.class, box, e -> true)) {
            ItemStack held = frame.getHeldItemStack();
            if (held.isOf(Items.ELYTRA)) {
                frame.setHeldItemStack(ItemStack.EMPTY);
            }
        }
    }
}
