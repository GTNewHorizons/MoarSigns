package gory_moon.moarsigns.client.interfaces;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class GuiBase extends GuiScreen {

    public int guiLeft;
    public int guiTop;
    public int xSize = 224;
    public int ySize = 200;

    public GuiRectangle overlay;

    public static void bindTexture(ResourceLocation resource) {
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(resource);
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
        super.drawHoveringText(par1List, par2, par3, font);
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public boolean isOnOverlay(int x, int y) {
        return overlay != null && overlay.inRect(x, y);
    }

}