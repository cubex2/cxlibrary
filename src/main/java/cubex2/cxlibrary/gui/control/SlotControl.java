package cubex2.cxlibrary.gui.control;

import cubex2.cxlibrary.gui.data.GuiData;
import net.minecraft.inventory.Slot;

import java.util.Collections;

public class SlotControl extends Control
{
    private final ScreenContainer screenContainer;
    private String hoveringText;
    private final Slot slot;

    public SlotControl(Slot slot, Anchor anchor, ControlContainer parent)
    {
        super(anchor, parent);
        screenContainer = (ScreenContainer) screen;
        this.slot = slot;
    }

    public void setHoveringText(String hoveringText)
    {
        this.hoveringText = hoveringText;
    }

    public Slot getSlot()
    {
        return slot;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks)
    {
        if (isMouseOverControl(mouseX, mouseY))
        {
            if (hoveringText != null)
            {
                screen.gui.drawHoveringText(Collections.singletonList(hoveringText), mouseX, mouseY);
            } else if (mc.thePlayer.inventory.getItemStack() == null && slot.getHasStack())
            {
                screen.gui.renderToolTip(slot.getStack(), mouseX, mouseY);
            }
        }
    }

    public static class Builder extends ControlBuilder<SlotControl>
    {
        public final Slot slot;

        public Builder(Slot slot, GuiData data, String name, ControlContainer parent)
        {
            super(data, name, parent);
            this.slot = slot;
            anchor.size(16, 16);
        }

        @Override
        protected SlotControl createInstance()
        {
            return new SlotControl(slot, anchor, parent);
        }
    }
}
