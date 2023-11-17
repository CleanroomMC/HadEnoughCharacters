package me.towdium.hecharacters.core;

import com.google.common.eventbus.EventBus;
import me.towdium.hecharacters.transform.transformers.TransformerHei;
import me.towdium.jecharacters.transform.TransformerRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/9/4.
 */
public class HechModContainer extends DummyModContainer {
    public HechModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "hecharacters";
        meta.name = "Had Enough Characters";
        meta.version = "@VERSION@";
        meta.authorList = Arrays.asList("Towdium", "vfyjxf", "Rongmario");
        meta.description = "Help HEI read Pinyin";
        meta.url = "https://www.curseforge.com/minecraft/mc-mods/had-enough-characters";
        meta.logoFile = "icon.png";
        try {
            registerTransformers();
        } catch (Exception e) {
            HechCore.LOG.error("Couldn't find JECh, HEI will not work");
        }

    }

    private static void registerTransformers() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> registryClazz = Class.forName("me.towdium.jecharacters.transform.TransformerRegistry");
        Field getTransformers = registryClazz.getField("transformers");
        getTransformers.setAccessible(true);
        List transformers = (List) getTransformers.get(null);
        transformers.add(new TransformerHei());
    }

    @Override
    public List<ArtifactVersion> getDependencies() {
        return Arrays.asList(
                VersionParser.parseVersionReference("jei@[4.22.0,)"),
                VersionParser.parseVersionReference("jecharacters@[1.12.0-3.7.2,)")
        );
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public File getSource() {
        return HechCore.source;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        try {
            return getSource().isDirectory() ?
                    Class.forName("net.minecraftforge.fml.client.FMLFolderResourcePack",
                            true, getClass().getClassLoader()) :
                    Class.forName("net.minecraftforge.fml.client.FMLFileResourcePack",
                            true, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
