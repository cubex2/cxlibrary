package cubex2.cxlibrary.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class PacketTransformer extends ClassTransformer
{
    @Override
    public boolean transform(ClassNode classNode)
    {
        if (ASMUtil.isAbstract(classNode.access))
            return false;
        if (!classNode.superName.equalsIgnoreCase("cubex2/cxlibrary/network/Packet"))
            return false;

        // Empty constructor
        if (!ASMUtil.hasMethod(classNode, "()V", "<init>"))
            ASMUtil.createEmptyDefaultConstructor(classNode);

        // toBytes
        if (!ASMUtil.hasMethod(classNode, null, "toBytesGenerated"))
            createToBytes(classNode);

        // fromBytes
        if (!ASMUtil.hasMethod(classNode, null, "fromBytesGenerated"))
            createFromBytes(classNode);

        return true;
    }

    private void createToBytes(ClassNode classNode)
    {
        MethodVisitor m = classNode.visitMethod(ACC_PUBLIC, "toBytesGenerated", "(Lio/netty/buffer/ByteBuf;)V", null, null);
        m.visitCode();
        for (FieldNode field : classNode.fields)
        {
            if (!hasCorrectAccess(field)) continue;

            if (ASMUtil.isPrimitive(field.desc))
                writePrimitive(m, classNode, field);
            else if (isObjectSupported(field.desc))
                writeObject(m, classNode, field);
        }
        m.visitInsn(RETURN);
    }

    private void writePrimitive(MethodVisitor m, ClassNode classNode, FieldNode field)
    {
        String desc = field.desc;
        String className = Type.getType(desc).getClassName();
        String methodName = "write" + className.substring(0, 1).toUpperCase() + className.substring(1);

        m.visitVarInsn(ALOAD, 1);
        m.visitVarInsn(ALOAD, 0);
        m.visitFieldInsn(GETFIELD, classNode.name, field.name, field.desc);
        m.visitMethodInsn(INVOKEVIRTUAL, "io/netty/buffer/ByteBuf", methodName, "(" + desc + ")Lio/netty/buffer/ByteBuf;", false);
    }

    private void writeObject(MethodVisitor m, ClassNode classNode, FieldNode field)
    {
        String desc = field.desc;

        m.visitVarInsn(ALOAD, 1);
        m.visitVarInsn(ALOAD, 0);
        m.visitFieldInsn(GETFIELD, classNode.name, field.name, field.desc);

        if (desc.equals("Ljava/lang/String;"))
            m.visitMethodInsn(INVOKESTATIC, "cpw/mods/fml/common/network/ByteBufUtils", "writeUTF8String", "(Lio/netty/buffer/ByteBuf;Ljava/lang/String;)V", false);
    }

    private void createFromBytes(ClassNode classNode)
    {
        MethodVisitor m = classNode.visitMethod(ACC_PUBLIC, "fromBytesGenerated", "(Lio/netty/buffer/ByteBuf;)V", null, null);
        m.visitCode();
        for (FieldNode field : classNode.fields)
        {
            if (!hasCorrectAccess(field)) continue;

            if (ASMUtil.isPrimitive(field.desc))
                readPrimitive(m, classNode, field);
            else if (isObjectSupported(field.desc))
                readObject(m, classNode, field);
        }
        m.visitInsn(RETURN);
    }

    private void readPrimitive(MethodVisitor m, ClassNode classNode, FieldNode field)
    {
        String desc = field.desc;
        String className = Type.getType(desc).getClassName();
        String methodName = "read" + className.substring(0, 1).toUpperCase() + className.substring(1);

        m.visitVarInsn(ALOAD, 0);
        m.visitVarInsn(ALOAD, 1);
        m.visitMethodInsn(INVOKEVIRTUAL, "io/netty/buffer/ByteBuf", methodName, "()" + desc, false);
        m.visitFieldInsn(PUTFIELD, classNode.name, field.name, field.desc);
    }

    private void readObject(MethodVisitor m, ClassNode classNode, FieldNode field)
    {
        String desc = field.desc;

        m.visitVarInsn(ALOAD, 0);
        m.visitVarInsn(ALOAD, 1);
        if (desc.equals("Ljava/lang/String;"))
            m.visitMethodInsn(INVOKESTATIC, "cpw/mods/fml/common/network/ByteBufUtils", "readUTF8String", "(Lio/netty/buffer/ByteBuf;)Ljava/lang/String;", false);
        m.visitFieldInsn(PUTFIELD, classNode.name, field.name, field.desc);
    }

    private boolean hasCorrectAccess(FieldNode field)
    {
        return !ASMUtil.isFinal(field.access) &&
                !ASMUtil.isStatic(field.access) &&
                !ASMUtil.isTransient(field.access);
    }

    private boolean isObjectSupported(String desc)
    {
        return desc.equals("Ljava/lang/String;");
    }
}
