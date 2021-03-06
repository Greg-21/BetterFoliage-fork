package mods.betterfoliage.resource.generated

import mods.betterfoliage.util.Atlas
import mods.betterfoliage.util.bytes
import mods.betterfoliage.util.loadSprite
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.lang.Math.max

data class CenteredSprite(val sprite: ResourceLocation, val aspectHeight: Int = 1, val aspectWidth: Int = 1, val atlas: Atlas = Atlas.BLOCKS) {

    fun register(pack: GeneratedTexturePack) = pack.register(atlas, this, this::draw)

    fun draw(resourceManager: IResourceManager): ByteArray {
        val baseTexture = resourceManager.loadSprite(atlas.file(sprite))

        val frameWidth = baseTexture.width
        val frameHeight = baseTexture.width * aspectHeight / aspectWidth
        val frames = baseTexture.height / frameHeight
        val size = max(frameWidth, frameHeight)

        val resultTexture = BufferedImage(size, size * frames, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = resultTexture.createGraphics()

        // iterate all frames
        for (frame in 0 until frames) {
            val baseFrame = baseTexture.getSubimage(0, size * frame, frameWidth, frameHeight)
            val resultFrame = BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR)

            resultFrame.createGraphics().apply {
                drawImage(baseFrame, (size - frameWidth) / 2, (size - frameHeight) / 2, null)
            }
            graphics.drawImage(resultFrame, 0, size * frame, null)
        }

        return resultTexture.bytes
    }
}