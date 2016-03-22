package cubex2.cxlibrary.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public abstract class ClassTransformer implements Opcodes
{
    public abstract boolean transform(ClassNode classNode);

    public int getClassWriterFlags()
    {
        return ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
    }
}
