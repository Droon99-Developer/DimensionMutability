package mod.linguardium.dimute.mixin;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import mod.linguardium.dimute.Main;
import mod.linguardium.dimute.api.copyableProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.nio.charset.StandardCharsets;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract void resetRecorder();

    @ModifyArgs(at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ServerWorld;<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;Z)V",ordinal = 1),method="createWorlds")
    private void setAndCopyMutableProperties(Args args) {
        RegistryKey<World> worldResourceKey = args.get(4);
        boolean DebugMode = args.get(7);
        if (!worldResourceKey.getValue().getNamespace().equals("minecraft")) {
            @SuppressWarnings("UnstableApiUsage")
            ServerWorldProperties immutable = args.get(3);

            if (immutable instanceof UnmodifiableLevelProperties) {
                ServerWorldProperties baseProperties = ((UnmodifiableLevelPropertiesAccessor)immutable).getWorldProperties();
                if (baseProperties instanceof LevelProperties) {
                    args.set(3,((copyableProperties)baseProperties).copy());
                } else {
                    args.set(3,baseProperties);
                }
            } else {
                args.set(3,immutable);
            }
            HashFunction hashing = Hashing.sha256();
            byte[] resourceStringBytes = worldResourceKey.getValue().toString().getBytes(StandardCharsets.UTF_8);
            byte[] originalSeedBytes = Longs.toByteArray(args.get(8));
            byte[] combinedBytes = new byte[resourceStringBytes.length + Long.BYTES];
            System.arraycopy(originalSeedBytes, 0, combinedBytes, 0, Long.BYTES);
            System.arraycopy(resourceStringBytes, 0, combinedBytes, Long.BYTES, resourceStringBytes.length);
            args.set(8, hashing.hashBytes(combinedBytes).asLong());
            Main.LOGGER.warn("World: " + worldResourceKey.getValue().toString() + " Overwritten Seed: " + args.get(8));
            Main.WORLD_SEEDS.put(worldResourceKey, args.get(8));

            if (!DebugMode) {
                args.set(10, true); // set timeticks on if debug is off and it isnt a vanilla dimension
            }
        }
    }
}
