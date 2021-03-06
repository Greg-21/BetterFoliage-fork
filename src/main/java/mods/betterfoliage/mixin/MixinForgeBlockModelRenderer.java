package mods.betterfoliage.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mods.betterfoliage.model.SpecialRenderModel;
import mods.betterfoliage.render.pipeline.RenderCtxForge;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ForgeBlockModelRenderer.class)
public class MixinForgeBlockModelRenderer {

    private static final String renderModelFlat = "Lnet/minecraftforge/client/model/pipeline/ForgeBlockModelRenderer;renderModelFlat(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z";
    private static final String renderModelSmooth = "Lnet/minecraftforge/client/model/pipeline/ForgeBlockModelRenderer;renderModelSmooth(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z";
    private static final String render = "Lnet/minecraftforge/client/model/pipeline/ForgeBlockModelRenderer;render(Lnet/minecraftforge/client/model/pipeline/VertexLighterFlat;Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;ZLjava/util/Random;JLnet/minecraftforge/client/model/data/IModelData;)Z";

    @Redirect(method = {renderModelFlat, renderModelSmooth}, at = @At(value = "INVOKE", target = render), remap = false)
    public boolean render(
            VertexLighterFlat lighter,
            IBlockDisplayReader world,
            IBakedModel model,
            BlockState state,
            BlockPos pos,
            MatrixStack matrixStack,
            boolean checkSides,
            Random rand,
            long seed,
            IModelData modelData
    ) {
        if (model instanceof SpecialRenderModel)
            return RenderCtxForge.render(lighter, world, (SpecialRenderModel) model, state, pos, matrixStack, checkSides, rand, seed, modelData);
        else
            return ForgeBlockModelRenderer.render(lighter, world, model, state, pos, matrixStack, checkSides, rand, seed, modelData);
    }
}
