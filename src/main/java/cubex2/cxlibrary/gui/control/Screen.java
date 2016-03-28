package cubex2.cxlibrary.gui.control;

import cubex2.cxlibrary.gui.GuiCX;
import org.lwjgl.util.Rectangle;

public class Screen extends ControlContainer<Control>
{
    private GuiCX gui;

    public Screen()
    {
        super(new Anchor(), null);
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
