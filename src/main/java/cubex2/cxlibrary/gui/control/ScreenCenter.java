package cubex2.cxlibrary.gui.control;

import net.minecraft.util.ResourceLocation;

public class ScreenCenter extends Screen
{
    protected final ControlContainer<Control> window;

    public ScreenCenter(ResourceLocation location)
    {
        super(location);

        window = container("bg", location).add();
    }
}
