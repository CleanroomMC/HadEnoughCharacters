package me.towdium.hecharacters.util;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.hecharacters.core.HechCore;
import me.towdium.jecharacters.JechConfig;
import me.towdium.pinin.searchers.TreeSearcher;
import mezz.jei.search.GeneralizedSuffixTree;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = HechCore.MODID)
public class Match {

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static class FakeTree<T> extends GeneralizedSuffixTree<T> {

        TreeSearcher<T> tree = searcher();

        @Override
        public void getSearchResults(String word, Set<T> results) {
            if (JechConfig.enableVerbose) {
                HechCore.LOG.info("FakeTree:search(" + word + ')');
            }
            results.addAll(tree.search(word));
        }

        @Override
        public void put(String key, T value) {
            if (JechConfig.enableVerbose) {
                HechCore.LOG.info("FakeTree:put(" + key + ',' + value + ')');
            }
            tree.put(key, value);
        }

        @Override
        public void getAllElements(Set<T> results) {
            results.addAll(tree.search(""));
        }
    }

    //TODO:Remove this method, jech has made searcher to be public.
    @Deprecated
    private static <T> TreeSearcher<T> searcher() {
        try {
            return (TreeSearcher<T>) me.towdium.jecharacters.util.Match.class.getDeclaredMethod("searcher").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
