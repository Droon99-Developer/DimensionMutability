package mod.linguardium.dimute;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Main implements ModInitializer {

    public static final String MOD_ID = "dimute";
    public static final String MOD_NAME = "Dimension Mutability";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final Map<RegistryKey<World>, Long> WORLD_SEEDS = new HashMap<>();

    @Override
    public void onInitialize() {
        WORLD_SEEDS.clear();
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}