package me.towdium.hecharacters.core;

import me.towdium.hecharacters.transform.transformers.TransformerHei;
import me.towdium.jecharacters.transform.Transformer;
import me.towdium.jecharacters.transform.TransformerRegistry;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.util.List;
import java.util.Map;


public class HechCallHook implements IFMLCallHook {
    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        TransformerRegistry.getTransformer("some.class");
        List<Transformer> transformers = TransformerRegistry.transformers;
        transformers.add(new TransformerHei());
        HechCore.INITIALIZED = true;
        return null;
    }
}
