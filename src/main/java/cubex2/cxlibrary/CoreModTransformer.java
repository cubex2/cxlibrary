package cubex2.cxlibrary;

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreModTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        return basicClass;
    }
}
