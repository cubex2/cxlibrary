package cubex2.cxlibrary.inventory;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerCX extends Container
{
    protected void addPlayerSlots(InventoryPlayer inv)
    {
        addSlotRange("player", inv, 0, inv.mainInventory.size());
    }

    protected void addSlotRange(String invName, IInventory inv)
    {
        addSlotRange(invName, inv, 0, inv.getSizeInventory());
    }

    protected void addSlotRange(String invName, IInventory inv, int start, int num)
    {
        for (int i = 0; i < num; i++)
        {
            addSlotToContainer(new SlotCX(invName, inv, start + i));
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (transferStackInSlot(slot, index, stack1, stack))
                return ItemStack.EMPTY;

            if (stack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack1.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack1);
        }

        return stack;

    }

    protected boolean transferStackInSlot(Slot slot, int index, @Nonnull ItemStack stack1, @Nonnull ItemStack stack)
    {
        return false;
    }
}
