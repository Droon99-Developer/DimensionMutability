package mod.linguardium.dimute.mixin;

import mod.linguardium.dimute.Main;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin  extends World implements ServerWorldAccess  {

    @Shadow public abstract long getSeed();

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"),method="tickWeather")
    private void sendToThisDimension(PlayerManager playerManager, Packet<?> packet) {
        playerManager.sendToDimension(packet, getRegistryKey());
    }

    @Inject(method="getSeed()J", at = @At(value="HEAD"), cancellable = true)
    private void newGetSeed(CallbackInfoReturnable<Long> cir) {
        RegistryKey<World> worldResourceKey = this.getRegistryKey();
        if (!worldResourceKey.getValue().getNamespace().equals("minecraft")) {
            cir.setReturnValue(Main.WORLD_SEEDS.get(worldResourceKey));
        }
    }

    @ModifyArgs(method="<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;Z)V", at=@At(value="INVOKE", target="Lnet/minecraft/world/StructureLocator;<init>(Lnet/minecraft/world/storage/NbtScannable;Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/world/biome/source/BiomeSource;JLcom/mojang/datafixers/DataFixer;)V", ordinal = 0))
    private void seedInitSeed(Args args) {
        args.set(8, getSeed());
    }
}
