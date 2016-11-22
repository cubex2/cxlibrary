package cubex2.cxlibrary.gui;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IGuiCX
{
    void renderTheToolTip(ItemStack stack, int x, int y);

    void drawTheHoveringText(List<String> textLines, int x, int y);

    void drawTheDefaultBackground();

    int getTheWidth();

    int getTheHeight();
}
