package cubex2.cxlibrary.gui.data;

import com.google.common.collect.Maps;
import cubex2.cxlibrary.gui.control.Anchor;
import cubex2.cxlibrary.gui.control.ControlContainer;
import cubex2.cxlibrary.util.ClientUtil;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public class GuiData
{
    private String parent;
    private List<ControlData> controls;

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

            controls.clear();
            controls.addAll(parentControls.values());
        }
    }

    private Map<String, ControlData> asMap()
    {
        Map<String, ControlData> ret = Maps.newHashMap();

        for (ControlData data : controls)
        {
            ret.put(data.name, data);
        }

        return ret;
    }
}
