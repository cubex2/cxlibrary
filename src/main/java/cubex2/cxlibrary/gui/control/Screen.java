package cubex2.cxlibrary.gui.control;

import cubex2.cxlibrary.gui.GuiCX;
import cubex2.cxlibrary.gui.data.GuiData;
import cubex2.cxlibrary.util.ClientUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Rectangle;

public class Screen extends ControlContainer<Control>
{
    private GuiCX gui;
    protected GuiData data;

    public Screen(ResourceLocation location)
    {
        super(new Anchor(), null);

        data = ClientUtil.loadGuiData(location, true);
    }

    public void setGui(GuiCX gui)
    {
        this.gui = gui;
    }

    @Override
    public Rectangle getBounds()
    {
        return new Rectangle(0, 0, gui.width, gui.height);
    }
}
