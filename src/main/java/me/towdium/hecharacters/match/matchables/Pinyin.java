package me.towdium.hecharacters.match.matchables;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.towdium.hecharacters.HechConfig;
import me.towdium.hecharacters.match.Matchable;
import me.towdium.hecharacters.match.PinyinData;
import me.towdium.hecharacters.match.Utilities.IndexSet;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashSet;

import static me.towdium.hecharacters.match.Utilities.strCmp;

/**
 * Author: Towdium
 * Date: 21/04/19
 */
public class Pinyin implements Matchable {
    private static LoadingCache<String, Pinyin> cache = CacheBuilder.newBuilder().concurrencyLevel(1)
            .build(new CacheLoader<String, Pinyin>() {
                @Override
                @ParametersAreNonnullByDefault
                public Pinyin load(String str) {
                    return new Pinyin(str);
                }
            });

    private Phoneme initial;
    private Phoneme finale;
    private Phoneme tone;

    public Pinyin(String str) {
        set(str);
    }

    public static Pinyin get(String str) {
        return cache.getUnchecked(str);
    }

    public static Pinyin[] get(char ch) {
        String[] ss = PinyinData.get(ch);
        Pinyin[] ret = new Pinyin[ss.length];
        for (int i = 0; i < ss.length; i++)
            ret[i] = get(ss[i]);
        return ret;
    }

    public static void refresh() {
        Phoneme.refresh();
        cache.asMap().forEach((s, p) -> p.set(s));
    }

    private void set(String str) {
        String[] elements = HechConfig.keyboard.separate(str);
        initial = Phoneme.get(elements[0]);
        finale = Phoneme.get(elements[1]);
        tone = Phoneme.get(elements[2]);
    }

    public IndexSet match(String str, int start) {
        IndexSet ret = new IndexSet(0x1);
        ret = initial.match(str, ret, start);
        ret.merge(finale.match(str, ret, start));
        ret.merge(tone.match(str, ret, start));
        return ret;
    }

    public char start() {
        String ret = initial.toString();
        if (ret.isEmpty()) ret = finale.toString();
        return ret.charAt(0);
    }

    @Override
    public String toString() {
        return "" + initial + finale + tone;
    }

    @SuppressWarnings("Duplicates")
    private static class Phoneme {
        private static LoadingCache<String, Phoneme> cache =
                CacheBuilder.newBuilder().concurrencyLevel(1)
                        .build(new CacheLoader<String, Phoneme>() {
                            @Override
                            @ParametersAreNonnullByDefault
                            public Phoneme load(String str) {
                                return new Phoneme(str);
                            }
                        });

        String[] strs;

        @Override
        public String toString() {
            return strs[0];
        }

        public Phoneme(String str) {
            HashSet<String> ret = new HashSet<>();
            ret.add(str);

            if (HechConfig.enableFuzzyCh2c && str.startsWith("c")) Collections.addAll(ret, "c", "ch");
            if (HechConfig.enableFuzzySh2s && str.startsWith("s")) Collections.addAll(ret, "s", "sh");
            if (HechConfig.enableFuzzyZh2z && str.startsWith("z")) Collections.addAll(ret, "z", "zh");
            if (HechConfig.enableFuzzyU2v && str.startsWith("v"))
                ret.add("u" + str.substring(1));
            if ((HechConfig.enableFuzzyAng2an && str.endsWith("ang"))
                    || (HechConfig.enableFuzzyEng2en && str.endsWith("eng"))
                    || (HechConfig.enableFuzzyIng2in && str.endsWith("ing")))
                ret.add(str.substring(0, str.length() - 1));
            if ((HechConfig.enableFuzzyAng2an && str.endsWith("an"))
                    || (str.endsWith("en") && HechConfig.enableFuzzyEng2en)
                    || (str.endsWith("in") && HechConfig.enableFuzzyIng2in))
                ret.add(str + 'g');
            strs = ret.stream().map(HechConfig.keyboard::keys).toArray(String[]::new);
        }

        public static Phoneme get(String str) {
            return cache.getUnchecked(str);
        }

        public static void refresh() {
            cache.invalidateAll();
        }

        IndexSet match(String source, IndexSet idx, int start) {
            if (strs.length == 1 && strs[0].isEmpty()) return new IndexSet(idx);
            else {
                IndexSet ret = new IndexSet();
                idx.foreach(i -> {
                    for (String str : strs) {
                        int size = strCmp(source, str, i + start);
                        if (i + start + size == source.length()) ret.set(i + size);  // ending match
                        else if (size == str.length()) ret.set(i + size); // full match
                    }
                    return true;
                });
                return ret;
            }
        }
    }
}
