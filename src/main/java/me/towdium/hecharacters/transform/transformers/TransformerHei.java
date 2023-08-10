package me.towdium.hecharacters.transform.transformers;

import me.towdium.hecharacters.HechConfig;
import me.towdium.hecharacters.core.HechCore;
import me.towdium.jecharacters.JechConfig;
import me.towdium.jecharacters.transform.Transformer;
import me.towdium.jecharacters.transform.TransformerRegistry;
import me.towdium.jecharacters.transform.transformers.TransformerJei;
import mezz.jei.config.Constants;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: Towdium
 * Date:   13/06/17
 */

public class TransformerHei extends Transformer.Default {

    @Override
    public boolean accepts(String name) {
        boolean loading = "mezz.jei.search.PrefixInfo".equals(name);
        if (loading) {
            boolean isHei = Loader.instance().getModList()
                    .stream()
                    .anyMatch(mod -> mod.getModId().equals(Constants.MOD_ID) && mod.getName().equals(Constants.NAME));
            if (!isHei) return false;
        }
        return HechConfig.enableHEI && loading;
    }

    @Override
    public void transform(ClassNode n) {
        HechCore.LOG.info("Transforming class " + n.name + " for HEI integration.");
        Transformer.findMethod(n, "<clinit>").ifPresent(methodNode ->
                transformInvokeLambda(methodNode,
                        "mezz/jei/search/GeneralizedSuffixTree",
                        "<init>",
                        "()V",
                        "me/towdium/hecharacters/util/Match$FakeTree",
                        "<init>",
                        "()V"
                ));
        if (JechConfig.enableForceQuote) Transformer.findMethod(n, "parseSearchToken").ifPresent(methodNode -> {
            InsnList list = methodNode.instructions;

            for (int i = 0; i < list.size(); i++) {
                AbstractInsnNode node = list.get(i);
                if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode methodInsn = (MethodInsnNode) node;
                    if ("java/util/regex/Pattern".equals(methodInsn.owner) && "matcher".equals(methodInsn.name) && "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;".equals(methodInsn.desc)) {
                        list.insert(node.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "me/towdium/hecharacters/util/Match", "wrap",
                                "(Ljava/lang/String;)Ljava/lang/String;", false));
                    }
                }
            }
        });
    }

    private static boolean transformInvokeLambda(
            MethodNode method, String owner, String name, String desc,
            String newOwner, String newName, String newDesc
    ) {
        boolean ret = false;
        Iterator<AbstractInsnNode> i = method.instructions.iterator();
        while (i.hasNext()) {
            AbstractInsnNode node = i.next();
            int op = node.getOpcode();
            if (node instanceof InvokeDynamicInsnNode && op == Opcodes.INVOKEDYNAMIC) {
                InvokeDynamicInsnNode insnNode = ((InvokeDynamicInsnNode) node);
                if (insnNode.bsmArgs[1] instanceof Handle) {
                    Handle h = ((Handle) insnNode.bsmArgs[1]);
                    if (!(!h.getOwner().equals(owner) || !h.getName().equals(name) || !h.getDesc().equals(desc))) {
                        insnNode.bsmArgs[1] = new Handle(h.getTag(), newOwner, newName, newDesc, h.isInterface());
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }
}
