package mods.betterfoliage.mixin;

import mods.betterfoliage.Hooks;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    private static final String worldAnimateTick = "Lnet/minecraft/client/world/ClientWorld;doAnimateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V";
    private static final String blockAnimateTick = "Lnet/minecraft/block/Block;animateTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V";

    private static final String worldNotify = "Lnet/minecraft/client/world/ClientWorld;sendBlockUpdated(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V";
    private static final String rendererNotify = "Lnet/minecraft/client/renderer/WorldRenderer;blockChanged(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V";

    /**
     * Inject a callback to call for every random display tick. Used for adding custom particle effects to blocks.
     */
    @Inject(method = worldAnimateTick, at = @At(value = "INVOKE", target = blockAnimateTick))
    void onAnimateTick(int x, int y, int z, int range, Random random, boolean doBarrier, BlockPos.Mutable pos, CallbackInfo ci) {
        Hooks.onRandomDisplayTick((ClientWorld) (Object) this, pos, random);
    }

    /**
     * Inject callback to get notified of client-side blockstate changes.
     * Used to invalidate caches in the {@link mods.betterfoliage.chunk.ChunkOverlayManager}
     */
    @Redirect(method = worldNotify, at = @At(value = "INVOKE", target = rendererNotify))
    void onClientBlockChanged(WorldRenderer renderer, IBlockReader world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        Hooks.onClientBlockChanged((ClientWorld) world, pos, oldState, newState, flags);
        renderer.blockChanged(world, pos, oldState, newState, flags);
    }
}
