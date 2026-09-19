package com.mag.elytraremover;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/**
 * Strips elytras out of item frames in the End.
 *
 * History of approaches tried:
 * 1. CHUNK_GENERATE (fires once, at first-ever generation) - too early:
 *    structure-embedded entities (like an end ship's item frame) aren't
 *    actually spawned into the live world at that point yet.
 * 2. CHUNK_LOAD (fires whenever any chunk loads) - still unreliable in
 *    practice, apparently because the chunk being "loaded" doesn't
 *    guarantee its structure-spawned entities are already present at the
 *    exact moment that event fires.
 * 3. ServerEntityEvents.ENTITY_LOAD (this version) - fires per-entity,
 *    specifically once "the entity is already in the world" (per Fabric's
 *    own docs). This sidesteps chunk-lifecycle timing entirely: we don't
 *    care when or why the item frame came into existence, only that the
 *    moment it does, we can see it and act on it immediately.
 */
public class ElytraRemoverMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerEntityEvents.ENTITY_LOAD.register(ElytraRemoverMod::onEntityLoad);
    }

    private static void onEntityLoad(Entity entity, ServerWorld world) {
        if (world.getRegistryKey() != World.END) {
            return;
        }
        if (!(entity instanceof ItemFrameEntity frame)) {
            return;
        }
        ItemStack held = frame.getHeldItemStack();
        if (held.isOf(Items.ELYTRA)) {
            frame.setHeldItemStack(ItemStack.EMPTY);
        }
    }
}
