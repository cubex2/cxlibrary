package cubex2.cxlibrary.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@SideOnly(Side.CLIENT)
public class MultiBakedModel implements IBakedModel
{
    private final Map<BiPredicate<IBlockState, EnumFacing>, IBakedModel> selectors;
    protected final boolean ambientOcclusion;
    protected final boolean gui3D;
    protected final TextureAtlasSprite particleTexture;
    protected final ItemCameraTransforms cameraTransforms;
    protected final ItemOverrideList overrides;

    public MultiBakedModel(Map<BiPredicate<IBlockState, EnumFacing>, IBakedModel> selectorsIn)
    {
        this.selectors = selectorsIn;
        IBakedModel ibakedmodel = selectorsIn.values().iterator().next();
        this.ambientOcclusion = ibakedmodel.isAmbientOcclusion();
        this.gui3D = ibakedmodel.isGui3d();
        this.particleTexture = ibakedmodel.getParticleTexture();
        this.cameraTransforms = ibakedmodel.getItemCameraTransforms();
        this.overrides = ibakedmodel.getOverrides();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        List<BakedQuad> list = Lists.<BakedQuad>newArrayList();

        if (state != null && side != null)
        {
            for (Map.Entry<BiPredicate<IBlockState, EnumFacing>, IBakedModel> entry : selectors.entrySet())
            {
                if (entry.getKey().test(state, side))
                {
                    list.addAll(entry.getValue().getQuads(state, side, rand++));
                }
            }
        }
        return list;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return ambientOcclusion;
    }

    @Override
    public boolean isGui3d()
    {
        return gui3D;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return particleTexture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return cameraTransforms;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    @SideOnly(Side.CLIENT)
    public static class Builder
    {
        private Map<BiPredicate<IBlockState, EnumFacing>, IBakedModel> builderSelectors = Maps.newLinkedHashMap();

        public Builder putModel(BiPredicate<IBlockState, EnumFacing> selector, IBakedModel model)
        {
            builderSelectors.put(selector, model);
            return this;
        }

        public IBakedModel build()
        {
            return new MultiBakedModel(builderSelectors);
        }

    }
}
