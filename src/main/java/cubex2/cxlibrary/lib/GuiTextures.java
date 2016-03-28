package cubex2.cxlibrary.lib;

import cubex2.cxlibrary.gui.GuiTexture;

public class GuiTextures
{
    private GuiTextures() {}

    public static final GuiTexture BG = new GuiTexture(Textures.BG, 256, 256);

    static
    {
        BG.addPart("bg", 0, 0, 256, 256);
    }
}
