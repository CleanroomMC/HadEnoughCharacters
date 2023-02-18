package me.towdium.hecharacters.transform.transformers;

import me.towdium.hecharacters.HechConfig;
import me.towdium.hecharacters.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

/**
 * Author: Towdium
 * Date:   12/06/17
 */
public class TransformerString extends Transformer.Configurable {
    public TransformerString() {
        reload();
    }

    @Override
    protected String[] getDefault() {
        return HechConfig.listDefaultString;
    }

    @Override
    protected String[] getAdditional() {
        return HechConfig.listAdditionalString;
    }

    @Override
    protected String getName() {
        return "string contains";
    }

    @Override
    protected void transform(MethodNode n) {
        Transformer.transformInvoke(
                n, "java/lang/String",
                "contains",
                "(Ljava/lang/CharSequence;)Z",
                "me/towdium/jecharacters/util/Match",
                "contains",
                "(Ljava/lang/String;Ljava/lang/CharSequence;)Z",
                false, Opcodes.INVOKESTATIC, Opcodes.H_INVOKESTATIC
        );
        Transformer.transformInvoke(
                n, "java/lang/String",
                "equals",
                "me/towdium/jecharacters/util/Match",
                "(Ljava/lang/Object;)Z",
                "equals",
                "(Ljava/lang/String;Ljava/lang/Object;)Z",
                false, Opcodes.INVOKESTATIC, Opcodes.H_INVOKESTATIC
        );
    }
}
