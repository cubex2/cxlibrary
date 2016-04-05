package cubex2.cxlibrary.gui.control;

import cubex2.cxlibrary.gui.GuiCX;
import cubex2.cxlibrary.gui.data.GuiData;
import cubex2.cxlibrary.util.ClientUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Rectangle;

public class Screen extends ControlContainer<Control>
{
    protected GuiCX gui;


    public Screen(ResourceLocation location)
    {
        super(location, new Anchor(), null);
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

    public boolean doesPauseGame()
    {
        return true;
    }
}
