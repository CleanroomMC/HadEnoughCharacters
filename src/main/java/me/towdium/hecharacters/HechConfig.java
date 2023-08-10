package me.towdium.hecharacters;

import me.towdium.jecharacters.core.JechCore;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class HechConfig {

    public static Configuration config;

    public static boolean enableHEI = true;

    public static void init(File location) {
        config = new Configuration(new File(location, "config/HadEnoughCharacters.cfg"), JechCore.VERSION);
        config.load();
        update();
    }

    public static void update() {
        enableHEI = config.getBoolean("enableHEI", "general", true, "Set to false to disable HEI support.");
        config.save();
    }

}
