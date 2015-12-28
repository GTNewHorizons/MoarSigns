package gory_moon.moarsigns.client.renderers;

import gory_moon.moarsigns.blocks.Blocks;
import gory_moon.moarsigns.client.ModelMoarSign;
import gory_moon.moarsigns.tileentites.TileEntityMoarSign;
import gory_moon.moarsigns.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class MoarSignRenderer extends TileEntitySpecialRenderer {

    private ModelMoarSign modelMoarSign;
    private ResourceLocation tempTexture = new ResourceLocation("textures/entity/sign.png");

    public MoarSignRenderer() {
        this.modelMoarSign = new ModelMoarSign();
    }

    @SuppressWarnings("unused")
    public void renderTileEntityMoarSignAt(TileEntityMoarSign tileentity, double x, double y, double z, float partialTickTime) {
        ResourceLocation texture = tileentity.getResourceLocation();

        Block block = tileentity.getBlockType();
        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f2;

        if (!tileentity.showInGui && (block == Blocks.signStandingWood || block == Blocks.signStandingMetal)) {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
            float f3 = (float) (tileentity.getBlockMetadata() * 360) / 16.0F;
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            this.modelMoarSign.stick.showModel = true;
        } else {
            int i = tileentity.getBlockMetadata();

            int side = i & 7;

            f2 = 0.0F;

            boolean flatSign = !tileentity.showInGui && ((i & 8) >> 3) == 1;
            boolean groundSign = false;

            if (flatSign) {
                groundSign = (i & 1) == 1;

                if (groundSign) {
                    int rotation = (i & 6) >> 1;
                    f2 = 0F;

                    if (rotation == 1) f2 = 90F;
                    else if (rotation == 2) f2 = 180F;
                    else if (rotation == 3) f2 = -90F;
                } else {
                    int rotation = (i & 6) >> 1;
                    f2 = 180F;

                    if (rotation == 1) f2 = -90F;
                    else if (rotation == 2) f2 = 0F;
                    else if (rotation == 3) f2 = 90F;
                }
            } else {
                if (side == 2) {
                    f2 = 180.0F;
                }

                if (side == 4) {
                    f2 = 90.0F;
                }

                if (side == 5) {
                    f2 = -90.0F;
                }

            }

            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            if (flatSign && !groundSign) GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            if (flatSign && groundSign) {
                GL11.glRotatef(270.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            }
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            this.modelMoarSign.stick.showModel = false;
        }

        if (texture != null) bindTexture(texture);
        else bindTexture(tempTexture);
        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);
        modelMoarSign.render();
        GL11.glPopMatrix();
        FontRenderer fontRenderer = func_147498_b();

        int[] sizes = tileentity.rowSizes;
        boolean[] rows = tileentity.visibleRows;
        int[] offset = tileentity.rowLocations;

        for (int row = 0; row < rows.length; row++) {
            if (!rows[row]) continue;

            float size = sizes[row];
            GL11.glPushMatrix();
            f2 = 0.016666668F * f1 + (size / 1000F);
            GL11.glTranslatef(size > 0 ? 0.01F : 0.0F, 0.5F * f1 - ((float) 0.02 * size) - (size < 2 ? 0 : size < 7 ? 0.01F : size < 11 ? 0.02F : size < 16 ? 0.03F : size < 20 ? 0.035F : 0.037F), 0.07F * f1);
            GL11.glScalef(f2, -f2, f2);
            GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
            GL11.glDepthMask(false);

            int maxLength = Utils.getMaxLength((int) size) - Utils.toPixelWidth(fontRenderer, Utils.getStyleOffset(tileentity.signText[row], tileentity.shadowRows[row]));
            String s = fontRenderer.trimStringToWidth(tileentity.signText[row], Math.min(maxLength, fontRenderer.getStringWidth(tileentity.signText[row])));

            GL11.glDisable(GL11.GL_LIGHTING);

            fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, (-tileentity.signText.length * 5) + offset[row] - 2, 0, tileentity.shadowRows[row]);

            GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }


        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
        renderTileEntityMoarSignAt((TileEntityMoarSign) tileentity, x, y, z, partialTickTime);
    }
}
