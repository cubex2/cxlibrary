package cubex2.cxlibrary;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name("CXLibraryCore")
@IFMLLoadingPlugin.MCVersion("1.10.2")
@IFMLLoadingPlugin.TransformerExclusions({"cubex2.cxlibrary.CoreModLoader", "cubex2.cxlibrary.CoreModTransformer", "cubex2.cxlibrary.asm.ASMUtil"})
@IFMLLoadingPlugin.SortingIndex(100)
public class CoreModLoader implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{CoreModTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
