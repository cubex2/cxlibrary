package cubex2.cxlibrary.gui.data;

import com.google.common.collect.Maps;
import cubex2.cxlibrary.gui.control.Anchor;
import cubex2.cxlibrary.gui.control.ControlContainer;
import cubex2.cxlibrary.gui.control.SlotControl;
import cubex2.cxlibrary.inventory.ISlotCX;
import cubex2.cxlibrary.util.ClientUtil;
import cubex2.cxlibrary.util.Util;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public class GuiData
{
    private String parent;
    private List<ControlData> controls;
    private Map<String, SlotData[]> slots;

    private transient GuiData parentData;

    public Anchor apply(String name, ControlContainer parent, IControlProvider provider)
    {
        if (controls != null)
        {
            loadParent();

            for (ControlData data : controls)
            {
                if (data.name.equals(name))
                {
                    Anchor anchor = new Anchor();
                    data.apply(anchor, parent, provider);
                    return anchor;
                }
            }
        }

        return new Anchor();
    }

    public SlotControl.Builder apply(SlotControl.Builder builder)
    {
        if (builder.slot instanceof ISlotCX)
        {
            String name = ((ISlotCX) builder.slot).getName();

            SlotData[] slotDatas = slots.get(name);
            for (SlotData slotData : slotDatas)
            {
                if (slotData.apply(builder))
                    return builder;
            }
        }
        return builder;
    }

    private void loadParent()
    {
        if (parent != null && parentData == null)
        {
            parentData = ClientUtil.loadGuiData(new ResourceLocation(parent), true);
            parentData.loadParent();

            Map<String, ControlData> parentControls = parentData.asMap();
            for (ControlData control : controls)
            {
                if (parentControls.containsKey(control.name))
                {
                    ControlData toOverride = parentControls.get(control.name);
                    toOverride.claimChildValues(control);
                } else
                {
                    parentControls.put(control.name, control);
                }
            }

            Map<String, SlotData[]> parentSlots = parentData.cloneSlots();
            if (slots != null)
            {
                for (Map.Entry<String, SlotData[]> entry : slots.entrySet())
                {
                    parentSlots.put(entry.getKey(), entry.getValue());
                }
            }

            controls.clear();
            controls.addAll(parentControls.values());
        }
    }

    private Map<String, SlotData[]> cloneSlots()
    {
        Map<String, SlotData[]> ret = Maps.newLinkedHashMap();

        if (slots != null)
        {
            for (Map.Entry<String, SlotData[]> entry : slots.entrySet())
            {
                SlotData[] array = new SlotData[entry.getValue().length];
                ret.put(entry.getKey(), Util.deepClone(array, SlotData.class));
            }
        }

        return ret;
    }

    private Map<String, ControlData> asMap()
    {
        Map<String, ControlData> ret = Maps.newLinkedHashMap();

        for (ControlData data : controls)
        {
            ret.put(data.name, data.clone());
        }

        return ret;
    }
}
