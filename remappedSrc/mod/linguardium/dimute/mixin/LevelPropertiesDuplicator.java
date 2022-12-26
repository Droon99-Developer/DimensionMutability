package mod.linguardium.dimute.mixin;

import com.mojang.serialization.Lifecycle;
import mod.linguardium.dimute.api.copyableProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelProperties.class)
public class LevelPropertiesDuplicator implements copyableProperties {

    @Shadow private LevelInfo levelInfo;

    @Shadow @Final private GeneratorOptions generatorOptions;

    @Shadow @Final private Lifecycle lifecycle;

    @Override
    public LevelProperties copy() {
        return new LevelProperties(levelInfo,generatorOptions,lifecycle);
    }
}
