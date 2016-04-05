package cubex2.cxlibrary.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotCX extends Slot implements ISlotCX
{
    private final String name;

    public SlotCX(String name, IInventory inventoryIn, int index)
    {
        super(inventoryIn, index, -2000, -2000);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
