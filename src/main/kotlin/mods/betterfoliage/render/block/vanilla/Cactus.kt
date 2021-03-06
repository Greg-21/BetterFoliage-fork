package mods.betterfoliage.render.block.vanilla

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.BetterFoliage
import mods.betterfoliage.config.CACTUS_BLOCKS
import mods.betterfoliage.config.Config
import mods.betterfoliage.model.HalfBakedSpecialWrapper
import mods.betterfoliage.model.HalfBakedWrapperKey
import mods.betterfoliage.model.SpecialRenderModel
import mods.betterfoliage.model.SpriteSetDelegate
import mods.betterfoliage.model.buildTufts
import mods.betterfoliage.model.crossModelsRaw
import mods.betterfoliage.model.crossModelsTextured
import mods.betterfoliage.model.transform
import mods.betterfoliage.model.tuftModelSet
import mods.betterfoliage.model.tuftShapeSet
import mods.betterfoliage.render.lighting.LightingPreferredFace
import mods.betterfoliage.render.lighting.RoundLeafLighting
import mods.betterfoliage.render.pipeline.RenderCtxBase
import mods.betterfoliage.resource.discovery.AbstractModelDiscovery
import mods.betterfoliage.resource.discovery.BakeWrapperManager
import mods.betterfoliage.resource.discovery.ModelBakingContext
import mods.betterfoliage.resource.discovery.ModelDiscoveryContext
import mods.betterfoliage.util.Atlas
import mods.betterfoliage.util.LazyInvalidatable
import mods.betterfoliage.util.Rotation
import mods.betterfoliage.util.get
import mods.betterfoliage.util.horizontalDirections
import mods.betterfoliage.util.randomD
import mods.betterfoliage.util.randomI
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.model.BlockModel
import net.minecraft.util.Direction.DOWN
import net.minecraft.util.ResourceLocation

object StandardCactusDiscovery : AbstractModelDiscovery() {
    override fun processModel(ctx: ModelDiscoveryContext) {
        val model = ctx.getUnbaked()
        if (model is BlockModel && ctx.blockState.block in CACTUS_BLOCKS) {
            BetterFoliage.blockTypes.dirt.add(ctx.blockState)
            ctx.addReplacement(StandardCactusKey)
            ctx.sprites.add(StandardCactusModel.cactusCrossSprite)
        }
        super.processModel(ctx)
    }
}

object StandardCactusKey : HalfBakedWrapperKey() {
    override fun bake(ctx: ModelBakingContext, wrapped: SpecialRenderModel) = StandardCactusModel(wrapped)
}

class StandardCactusModel(
    wrapped: SpecialRenderModel
) : HalfBakedSpecialWrapper(wrapped) {

    val armLighting = horizontalDirections.map { LightingPreferredFace(it) }.toTypedArray()

    override fun render(ctx: RenderCtxBase, noDecorations: Boolean) {
        ctx.checkSides = false
        super.render(ctx, noDecorations)
        if (!Config.enabled || !Config.cactus.enabled) return

        val armSide = ctx.random.nextInt() and 3
        ctx.vertexLighter = armLighting[armSide]
        ctx.renderQuads(cactusArmModels[armSide][ctx.random])
        ctx.vertexLighter = RoundLeafLighting
        ctx.renderQuads(cactusCrossModels[ctx.random])
    }

    companion object {
        val cactusCrossSprite = ResourceLocation(BetterFoliageMod.MOD_ID, "blocks/better_cactus")
        val cactusArmSprites by SpriteSetDelegate(Atlas.BLOCKS) { idx ->
            ResourceLocation(BetterFoliageMod.MOD_ID, "blocks/better_cactus_arm_$idx")
        }
        val cactusArmModels by LazyInvalidatable(BakeWrapperManager) {
            val shapes = Config.cactus.let { tuftShapeSet(0.8, 0.8, 0.8, 0.2) }
            val models = tuftModelSet(shapes, -1) { cactusArmSprites[randomI()] }
            horizontalDirections.map { side ->
                models.transform { move(0.0625 to DOWN).rotate(Rotation.fromUp[side.ordinal]) }.buildTufts()
            }.toTypedArray()
        }
        val cactusCrossModels by LazyInvalidatable(BakeWrapperManager) {
            val models = Config.cactus.let { config ->
                crossModelsRaw(64, config.size, 0.0, 0.0)
                    .transform { rotateZ(randomD(-config.sizeVariation, config.sizeVariation)) }
            }
            crossModelsTextured(models, -1, true) { cactusCrossSprite }
        }
    }
}