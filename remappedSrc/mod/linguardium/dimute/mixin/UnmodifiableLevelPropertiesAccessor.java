package mod.linguardium.dimute.mixin;

import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UnmodifiableLevelProperties.class)
public interface UnmodifiableLevelPropertiesAccessor {
    @Accessor
    ServerWorldProperties getWorldProperties();
}
