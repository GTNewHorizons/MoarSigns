package gory_moon.moarsigns.client.interfaces;

import gory_moon.moarsigns.client.interfaces.containers.ContainerExchange;
import gory_moon.moarsigns.client.interfaces.containers.InventoryExchange;
import gory_moon.moarsigns.items.ItemSignToolbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiExchange extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation("moarsigns", "textures/gui/sign_exchange.png");

    public EntityPlayer player;

    public GuiExchange(InventoryPlayer inventory, InventoryExchange exchangeInv) {
        super(new ContainerExchange(inventory, exchangeInv));

        player = inventory.player;
        xSize = 226;
        ySize = 162;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        inventorySlots.onContainerClosed(player);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemSignToolbox)) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
