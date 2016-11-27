package cubex2.cxlibrary.gui;

import cubex2.cxlibrary.gui.control.Screen;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiCX extends GuiScreen implements IGuiCX
{
    private final Screen screen;

    public GuiCX(Screen screen)
    {
        this.screen = screen;
        this.screen.setGui(this);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        screen.updateBounds();
    }

    @Override
    public void updateScreen()
    {
        ScaledResolution resolution = new ScaledResolution(mc);
        int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
        int mouseY = resolution.getScaledHeight() - Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight - 1;

        screen.update(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        screen.draw(mouseX, mouseY, partialTicks);
        screen.drawForeground(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        screen.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException
    {
        screen.mouseClicked(mouseX, mouseY, button, true);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button)
    {
        screen.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick)
    {
        screen.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick);
    }

    @Override
    public void onGuiClosed()
    {
        screen.onClosed();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return screen.doesPauseGame();
    }

    @Override
    public void renderTheToolTip(@Nonnull ItemStack stack, int x, int y)
    {
        renderToolTip(stack, x, y);
    }

    @Override
    public void drawTheHoveringText(List<String> textLines, int x, int y)
    {
        drawHoveringText(textLines, x, y);
    }

    @Override
    public void drawTheDefaultBackground()
    {
        drawDefaultBackground();
    }

    @Override
    public int getTheWidth()
    {
        return width;
    }

    @Override
    public int getTheHeight()
    {
        return height;
    }
}
