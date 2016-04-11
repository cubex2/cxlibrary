package cubex2.cxlibrary.gui.control;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cubex2.cxlibrary.inventory.ISlotCX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Set;

public class ScreenContainer extends ScreenCenter
{
    protected RenderItem itemRender;
    protected FontRenderer fontRendererObj;

    private final Container container;
    /** holds the slot currently hovered */
    private Slot theSlot;
    /** Used when touchscreen is enabled. */
    private Slot clickedSlot;
    /** Used when touchscreen is enabled. */
    private boolean isRightMouseClick;
    /** Used when touchscreen is enabled */
    private ItemStack draggedStack;
    private int touchUpX;
    private int touchUpY;
    private Slot returningStackDestSlot;
    private long returningStackTime;
    /** Used when touchscreen is enabled */
    private ItemStack returningStack;
    private Slot currentDragTargetSlot;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp = true;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private Slot lastClickSlot;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot;

    // TODO make slots controls
    private List<SlotControl> slots = Lists.newLinkedList();
    private SlotControl[] hotbarSlots = new SlotControl[9];

    public ScreenContainer(Container container, ResourceLocation location)
    {
        super(location);
        this.container = container;

        for (Slot slot : container.inventorySlots)
        {
            SlotControl slotControl;
            if (slot instanceof ISlotCX)
            {
                slotControl = data.apply(slot(null, slot)).add();
            } else
            {
                slotControl = slot(null, slot).left(slot.xDisplayPosition).top(slot.yDisplayPosition).add();
            }

            slots.add(slotControl);
            if (slot.inventory instanceof InventoryPlayer
                    && slot.getSlotIndex() >= 0 && slot.getSlotIndex() < 9
                    && ((InventoryPlayer) slot.inventory).player == mc.thePlayer)
            {
                hotbarSlots[slot.getSlotIndex()] = slotControl;
            }
        }
    }

    public SlotControl getSlot(String name, int index)
    {
        for (SlotControl slotControl : slots)
        {
            Slot slot = slotControl.getSlot();
            if (slot instanceof ISlotCX && name.equals(((ISlotCX) slot).getName())
                    && slot.getSlotIndex() == index)
            {
                return slotControl;
            }
        }
        return null;
    }

    public SlotControl.Builder slot(String name, Slot slot)
    {
        return new SlotControl.Builder(slot, data, name, window);
    }

    @Override
    public void updateBounds()
    {
        itemRender = mc.getRenderItem();
        fontRendererObj = mc.fontRendererObj;

        super.updateBounds();

        mc.thePlayer.openContainer = container;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        gui.drawDefaultBackground();

        super.draw(mouseX, mouseY, partialTicks);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        theSlot = null;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (SlotControl slot : slots)
        {
            if (slot.isVisible())
                drawSlotAt(slot.getSlot(), slot.getX(), slot.getY());
            if (slot.isMouseOverControl(mouseX, mouseY) && slot.getSlot().canBeHovered())
            {
                if (slot.isEnabled())
                    theSlot = slot.getSlot();
                if (slot.isVisible())
                    drawSlotMouseOver(slot.getX(), slot.getY());
            }
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks)
    {
        super.drawForeground(mouseX, mouseY, partialTicks);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) window.getX(), (float) window.getY(), 0.0F);
        drawDraggedStack(mouseX, mouseY);
        drawReturningStack();
        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    private void drawSlotMouseOver(int x, int y)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);
        drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private void drawReturningStack()
    {
        if (returningStack != null)
        {
            float f = (float) (Minecraft.getSystemTime() - returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                returningStack = null;
            }

            int l2 = returningStackDestSlot.xDisplayPosition - touchUpX;
            int i3 = returningStackDestSlot.yDisplayPosition - touchUpY;
            int l1 = touchUpX + (int) ((float) l2 * f);
            int i2 = touchUpY + (int) ((float) i3 * f);
            drawItemStack(returningStack, l1, i2, null);
        }
    }

    private void drawDraggedStack(int mouseX, int mouseY)
    {
        InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
        ItemStack itemstack = draggedStack == null ? inventoryplayer.getItemStack() : draggedStack;

        if (itemstack != null)
        {
            int j2 = 8;
            int k2 = draggedStack == null ? 8 : 16;
            String s = null;

            if (draggedStack != null && isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int((float) itemstack.stackSize / 2.0F);
            } else if (dragSplitting && dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = dragSplittingRemnant;

                if (itemstack.stackSize == 0)
                {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }

            drawItemStack(itemstack, mouseX - window.getX() - j2, mouseY - window.getY() - k2, s);
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack == null ? 0 : 8), altText);
        zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    private void drawSlotAt(Slot slotIn, int x, int y)
    {
        ItemStack itemstack = slotIn.getStack();
        boolean flag = false;
        boolean flag1 = slotIn == clickedSlot && draggedStack != null && !isRightMouseClick;
        ItemStack itemstack1 = mc.thePlayer.inventory.getItemStack();
        String s = null;

        if (slotIn == clickedSlot && draggedStack != null && isRightMouseClick && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        } else if (dragSplitting && dragSplittingSlots.contains(slotIn) && itemstack1 != null)
        {
            if (dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && container.canDragIntoSlot(slotIn))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = TextFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > slotIn.getItemStackLimit(itemstack))
                {
                    s = TextFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemstack);
                    itemstack.stackSize = slotIn.getItemStackLimit(itemstack);
                }
            } else
            {
                dragSplittingSlots.remove(slotIn);
                updateActivePotionEffects();
            }
        }

        zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (itemstack == null && slotIn.canBeHovered())
        {
            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            if (textureatlassprite != null)
            {
                GlStateManager.disableLighting();
                mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                drawTexturedModalRect(x, y, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(x, y, x + 16, y + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            itemRender.renderItemAndEffectIntoGUI(mc.thePlayer, itemstack, x, y);
            itemRender.renderItemOverlayIntoGUI(fontRendererObj, itemstack, x, y, s);
        }

        itemRender.zLevel = 0.0F;
        zLevel = 0.0F;
    }

    private void updateActivePotionEffects()
    {
        ItemStack itemstack = mc.thePlayer.inventory.getItemStack();

        if (itemstack != null && dragSplitting)
        {
            dragSplittingRemnant = itemstack.stackSize;

            for (Slot slot : dragSplittingSlots)
            {
                ItemStack itemstack1 = itemstack.copy();
                int i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }

                if (itemstack1.stackSize > slot.getItemStackLimit(itemstack1))
                {
                    itemstack1.stackSize = slot.getItemStackLimit(itemstack1);
                }

                dragSplittingRemnant -= itemstack1.stackSize - i;
            }
        }
    }

    private Slot getSlotAtPosition(int x, int y)
    {
        for (SlotControl slot : slots)
        {
            if (slot.isEnabled() && slot.getBounds().contains(x, y))
            {
                return slot.getSlot();
            }
        }

        return null;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, boolean intoControl)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton, intoControl);

        boolean flag = mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        long i = Minecraft.getSystemTime();
        doubleClick = lastClickSlot == slot && i - lastClickTime < 250L && lastClickButton == mouseButton;
        ignoreMouseUp = false;

        if (mouseButton == 0 || mouseButton == 1 || flag)
        {
            boolean isOutsideGui = slot != null && !window.getBounds().contains(mouseX, mouseY);
            int l = -1;

            if (slot != null)
            {
                l = slot.slotNumber;
            }

            if (isOutsideGui)
            {
                l = -999;
            }

            if (mc.gameSettings.touchscreen && isOutsideGui && mc.thePlayer.inventory.getItemStack() == null)
            {
                mc.displayGuiScreen(null);
                return;
            }

            if (l != -1)
            {
                if (mc.gameSettings.touchscreen)
                {
                    if (slot.getHasStack())
                    {
                        clickedSlot = slot;
                        draggedStack = null;
                        isRightMouseClick = mouseButton == 1;
                    } else
                    {
                        clickedSlot = null;
                    }
                } else if (!dragSplitting)
                {
                    if (mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
                        } else
                        {
                            boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            ClickType clicktype = ClickType.PICKUP;

                            if (flag2)
                            {
                                shiftClickedSlot = slot.getHasStack() ? slot.getStack() : null;
                                clicktype = ClickType.QUICK_MOVE;
                            } else if (l == -999)
                            {
                                clicktype = ClickType.THROW;
                            }

                            handleMouseClick(slot, l, mouseButton, clicktype);
                        }

                        ignoreMouseUp = true;
                    } else
                    {
                        dragSplitting = true;
                        dragSplittingButton = mouseButton;
                        dragSplittingSlots.clear();

                        if (mouseButton == 0)
                        {
                            dragSplittingLimit = 0;
                        } else if (mouseButton == 1)
                        {
                            dragSplittingLimit = 1;
                        } else if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }

        lastClickSlot = slot;
        lastClickTime = i;
        lastClickButton = mouseButton;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
        {
            if (clickedMouseButton == 0 || clickedMouseButton == 1)
            {
                if (this.draggedStack == null)
                {
                    if (slot != this.clickedSlot && this.clickedSlot.getStack() != null)
                    {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                } else if (this.draggedStack.stackSize > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false))
                {
                    long i = Minecraft.getSystemTime();

                    if (this.currentDragTargetSlot == slot)
                    {
                        if (i - this.dragItemDropDelay > 500L)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.dragItemDropDelay = i + 750L;
                            --this.draggedStack.stackSize;
                        }
                    } else
                    {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = i;
                    }
                }
            }
        } else if (this.dragSplitting && slot != null && itemstack != null && itemstack.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && container.canDragIntoSlot(slot))
        {
            this.dragSplittingSlots.add(slot);
            this.updateActivePotionEffects();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        int i = window.getX();
        int j = window.getY();
        boolean isOutsideGui = slot != null && !window.getBounds().contains(mouseX, mouseY);
        int k = -1;

        if (slot != null)
        {
            k = slot.slotNumber;
        }

        if (isOutsideGui)
        {
            k = -999;
        }

        if (this.doubleClick && slot != null && state == 0 && container.canMergeSlot(null, slot))
        {
            if (GuiScreen.isShiftKeyDown())
            {
                if (slot.inventory != null && this.shiftClickedSlot != null)
                {
                    for (Slot slot2 : container.inventorySlots)
                    {
                        if (slot2 != null && slot2.canTakeStack(this.mc.thePlayer) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true))
                        {
                            this.handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else
            {
                this.handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
        } else
        {
            if (this.dragSplitting && this.dragSplittingButton != state)
            {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return;
            }

            if (this.ignoreMouseUp)
            {
                this.ignoreMouseUp = false;
                return;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
            {
                if (state == 0 || state == 1)
                {
                    if (this.draggedStack == null && slot != this.clickedSlot)
                    {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    boolean canAdd = Container.canAddItemToSlot(slot, this.draggedStack, false);

                    if (k != -1 && this.draggedStack != null && canAdd)
                    {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);

                        if (this.mc.thePlayer.inventory.getItemStack() != null)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
                            this.touchUpX = mouseX - i;
                            this.touchUpY = mouseY - j;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        } else
                        {
                            this.returningStack = null;
                        }
                    } else if (this.draggedStack != null)
                    {
                        this.touchUpX = mouseX - i;
                        this.touchUpY = mouseY - j;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }

                    this.draggedStack = null;
                    this.clickedSlot = null;
                }
            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
            {
                this.handleMouseClick(null, -999, Container.func_94534_d(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);

                for (Slot slot1 : this.dragSplittingSlots)
                {
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
                }

                this.handleMouseClick(null, -999, Container.func_94534_d(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
            } else if (this.mc.thePlayer.inventory.getItemStack() != null)
            {
                if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                {
                    this.handleMouseClick(slot, k, state, ClickType.CLONE);
                } else
                {
                    boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (flag1)
                    {
                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }

                    this.handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.mc.thePlayer.inventory.getItemStack() == null)
        {
            this.lastClickTime = 0L;
        }

        this.dragSplitting = false;
    }

    private void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }

        this.mc.playerController.windowClick(this.container.windowId, slotId, mouseButton, type, this.mc.thePlayer);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        }

        this.checkHotbarKeys(keyCode);

        if (this.theSlot != null && this.theSlot.getHasStack())
        {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, ClickType.CLONE);
            } else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, GuiScreen.isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }

    private boolean checkHotbarKeys(int keyCode)
    {
        if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode()
                        && (hotbarSlots[i] == null || hotbarSlots[i].isEnabled()))
                {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onClosed()
    {
        super.onClosed();

        if (mc.thePlayer != null)
        {
            container.onContainerClosed(mc.thePlayer);
        }
    }

    @Override
    public boolean doesPauseGame()
    {
        return false;
    }

    @Override
    public void update(int mouseX, int mouseY)
    {
        super.update(mouseX, mouseY);

        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead)
        {
            mc.thePlayer.closeScreen();
        }
    }
}
