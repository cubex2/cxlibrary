package cubex2.cxlibrary.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cubex2.cxlibrary.gui.data.GuiData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ClientUtil
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, GuiData> guiCache = Maps.newHashMap();

    public static String readResource(ResourceLocation location)
    {
        String ret = null;

        InputStream is = null;
        try
        {
            is = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            ret = IOUtils.toString(is, Charsets.UTF_8);

        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(is);
        }

        return ret;
    }

    public static GuiData loadGuiData(ResourceLocation location, boolean useCache)
    {
        if (useCache && guiCache.containsKey(location))
        {
            System.out.println("Using cache for " + location);
            return guiCache.get(location);
        }

        String json = ClientUtil.readResource(location);
        GuiData data = gson.fromJson(json, GuiData.class);
        guiCache.put(location, data);
        return data;
    }
}
