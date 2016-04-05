package cubex2.cxlibrary.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import cubex2.cxlibrary.gui.data.GuiData;
import cubex2.cxlibrary.gui.data.SlotData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ClientUtil
{
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(new TypeToken<Map<String, SlotData[]>>() {}.getType(), (JsonDeserializer<Map<String, SlotData[]>>) (json, typeOfT, context) -> {
        Map<String, SlotData[]> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet())
        {
            map.put(entry.getKey(), context.deserialize(entry.getValue(), new TypeToken<SlotData[]>() {}.getType()));
        }
        return map;
    }).create();

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
