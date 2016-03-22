package cubex2.cxlibrary.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMUtil implements Opcodes
{
    public static boolean hasAccess(int input, int access)
    {
        return (input & access) != 0;
    }

    public static boolean isAbstract(int input)
    {
        return hasAccess(input, ACC_ABSTRACT);
    }

    public static boolean isStatic(int input)
    {
        return hasAccess(input, ACC_STATIC);
    }

    public static boolean isFinal(int input)
    {
        return hasAccess(input, ACC_FINAL);
    }

    public static boolean isTransient(int input)
    {
        return hasAccess(input, ACC_TRANSIENT);
    }

    public static boolean isPrimitive(String desc)
    {
        if (desc.length() != 1) return false;

        int type = Type.getType(desc).getSort();
        return type >= Type.BOOLEAN && type <= Type.DOUBLE;
    }

    /**
     * Finds a method.
     *
     * @param classNode The classNode
     * @param desc      The method description. May be null to only look for the methods name.
     * @param names     The method names.
     * @return The method or null if such a method has not been found.
     */
    public static MethodNode findMethod(ClassNode classNode, String desc, String... names)
    {
        for (MethodNode m : classNode.methods)
        {
            if (desc != null && !m.desc.equalsIgnoreCase(desc))
                continue;

            for (String name : names)
            {
                if (m.name.equals(name))
                    return m;
            }
        }

        return null;
    }

    /**
     * Checks if the method exists
     *
     * @param classNode The classNode
     * @param desc      The method description. May be null to only look for the methods name.
     * @param names     The method names.
     * @return TRUE if the method does exist, false otherwise
     */
    public static boolean hasMethod(ClassNode classNode, String desc, String... names)
    {
        return findMethod(classNode, desc, names) != null;
    }

    public static void createEmptyDefaultConstructor(ClassNode classNode)
    {
        MethodVisitor m = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        m.visitCode();
        m.visitVarInsn(ALOAD, 0);
        m.visitMethodInsn(INVOKESPECIAL, classNode.superName, "<init>", "()V", false);
        m.visitInsn(RETURN);
        m.visitEnd();
    }
}
