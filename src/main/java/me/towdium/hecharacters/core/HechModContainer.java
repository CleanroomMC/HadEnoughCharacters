package me.towdium.hecharacters.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import java.io.File;
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
    }

    @Override
    public List<ArtifactVersion> getDependencies() {
        return Arrays.asList(
                VersionParser.parseVersionReference("jei@[4.22.0,)"),
                VersionParser.parseVersionReference("jecharacters@[3.7.2,)")
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

    @Override
    public String getGuiClassName() {
        return "me.towdium.hecharacters.HechGuiFactory";
    }

}
