package io.github.jacg311.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.jacg311.DebugDelights;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;randomTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V"))
    private void debugdelights$spawnRandomTickParticleForBlock(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, @Local BlockPos pos, @Local BlockState state) {
        if (DebugDelights.CONFIG.randomTickBlocks().contains(state.getBlock()) && chunk.getWorld() instanceof ServerWorld serverWorld) {
            BlockPos posUp = pos.up();
            Vec3d vecUp = posUp.toCenterPos();
            serverWorld.spawnParticles(ParticleTypes.FLAME, vecUp.getX(), posUp.getY(), vecUp.getZ(), 1, 0d, 0d, 0d, 0d);

        }
    }
}
