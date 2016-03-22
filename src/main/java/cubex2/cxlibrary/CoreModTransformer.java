package cubex2.cxlibrary;

import com.google.common.collect.Lists;
import cubex2.cxlibrary.asm.ClassTransformer;
import cubex2.cxlibrary.asm.PacketTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class CoreModTransformer implements IClassTransformer
{
    private static List<ClassTransformer> transformers = Lists.newLinkedList();

    static
    {
        transformers.add(new PacketTransformer());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (ClassTransformer transformer : transformers)
        {
            if (transformer.transform(classNode))
            {
                ClassWriter classWriter = new ClassWriter(transformer.getClassWriterFlags());
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            }
        }

        return basicClass;
    }
}
