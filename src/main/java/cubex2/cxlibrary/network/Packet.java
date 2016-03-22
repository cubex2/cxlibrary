package cubex2.cxlibrary.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Extending this class will result in an empty default constructor, a toBytesGenerated and a fromBytesGenerated method
 * to be created on startup if not already present.<br />
 * The two methods do read and write for the following types of fields:<br />
 * primitives<br />
 * {@code String}
 */
public abstract class Packet implements IMessage
{
    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        fromBytesGenerated(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        toBytesGenerated(byteBuf);
    }

    public void fromBytesGenerated(ByteBuf byteBuf)
    {

    }

    public void toBytesGenerated(ByteBuf byteBuf)
    {

    }
}
