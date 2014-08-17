package gory_moon.moarsigns.client.interfaces;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gory_moon.moarsigns.client.interfaces.buttons.*;
import gory_moon.moarsigns.lib.Info;
import gory_moon.moarsigns.network.PacketHandler;
import gory_moon.moarsigns.network.message.MessageSignUpdate;
import gory_moon.moarsigns.tileentites.TileEntityMoarSign;
import gory_moon.moarsigns.util.Localization;
import gory_moon.moarsigns.util.Utils;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class GuiMoarSign extends GuiBase {

    private List<GuiButton> buttons = new ArrayList<GuiButton>();
    public GuiSignTextField[] guiTextFields = new GuiSignTextField[4];
    public int selectedTextField = 0;

    public boolean showColors = false;
    private GuiColorButton[] colors = new GuiColorButton[16];

    public boolean showTextStyles;
    private GuiTextStyleButton[] styleButtons = new GuiTextStyleButton[6];

    private ButtonCutSign buttonCutSign;
    private ButtonCopySign buttonCopySign;
    private ButtonErase buttonErase;
    public ButtonColorPicker buttonColorPicker;
    public ButtonTextStyle buttonTextStyle;

    public int[] fontSizes = new int[4];

    public static final ResourceLocation texture = new ResourceLocation(Info.TEXTURE_LOCATION, "textures/gui/sign_base.png");


    private TileEntityMoarSign entitySign;
    private int rows = 4;
    private int maxLength = 15;
    private int minOffset = -1;
    private int[] row;

    private int size = 0;

    public GuiMoarSign(TileEntityMoarSign te) {
        entitySign = te;
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();
        buttons.clear();
        Keyboard.enableRepeatEvents(true);

        String[] text = getSignTextWithCode(entitySign.signText);
        for (int i = 0; i < guiTextFields.length; i++) {
            guiTextFields[i] = new GuiSignTextField(fontRendererObj, guiLeft + 10, guiTop + 35 + 17 * i, 90, 16);
            guiTextFields[i].setText(text[i]);
        }

        if (selectedTextField != -1) guiTextFields[selectedTextField].setFocused(true);

        int k = 0;
        int j = 0;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new GuiColorButton(guiLeft + 150 + 5 + 14 * k, guiTop + 30 + 5 + 14 * j, 12, 12, i);
            if (k > 2) {
                k = 0;
                j++;
            } else k++;
        }

        for (int i = 0; i < styleButtons.length; i++) {
            styleButtons[i] = new GuiTextStyleButton(guiLeft + 150 + 5, guiTop + 30 + 5 + 18 * i, 50, 16, i);
        }

        buttonCutSign = new ButtonCutSign(guiLeft + 74, guiTop + 10);
        buttonCopySign = new ButtonCopySign(guiLeft + 95, guiTop + 10);
        buttonErase = new ButtonErase(guiLeft + 137, guiTop + 10);
        buttonColorPicker = new ButtonColorPicker(guiLeft + 158, guiTop + 10);
        buttonTextStyle = new ButtonTextStyle(guiLeft + 179, guiTop + 10);

        buttons.add(new ButtonCut(guiLeft + 11, guiTop + 10));
        buttons.add(new ButtonCopy(guiLeft + 32, guiTop + 10));
        buttons.add(new ButtonPaste(guiLeft + 53, guiTop + 10));
        buttons.add(buttonCutSign);
        buttons.add(buttonCopySign);
        buttons.add(new ButtonPasteSign(guiLeft + 116, guiTop + 10));
        buttons.add(buttonErase);
        buttons.add(buttonColorPicker);
        buttons.add(buttonTextStyle);

        k = 0;
        j = 0;
        for (int i = 0; i < fontSizes.length; i++) {
            buttons.add(new ButtonFontSize(guiLeft + 40 + k * 40, guiTop + 150 + j * 20, i, true));
            buttons.add(new ButtonFontSize(guiLeft + 40 + k * 40, guiTop + 158 + j * 20, i, false));
            if (k >= 1) {
                k = 0;
                j++;
            } else k++;
        }

        update();

        /*SIZE_X = width / 2 + 65;
        SIZE_X2 = width / 2 + 65 + SIZE_W;
        OFFSET_X = width / 2 + 130;
        OFFSET_X2 = width / 2 + 130 + SIZE_W;

        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
        buttonList.add(new GuiButton(1, SIZE_X - 2, SIZE_Y - 24, 54, 20, "+"));
        buttonList.add(new GuiButton(2, SIZE_X - 2, SIZE_Y + 24, 54, 20, "-"));
        buttonList.add(new GuiButton(3, OFFSET_X - 2, OFFSET_Y - 24, 54, 20, "+"));
        buttonList.add(new GuiButton(4, OFFSET_X - 2, OFFSET_Y + 24, 54, 20, "-"));
        entitySign.setEditable(false);         */
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.entitySign.setEditable(true);

        for (int i = 0; i < entitySign.signText.length; i++) {
            entitySign.signText[i] = fontRendererObj.trimStringToWidth(entitySign.signText[i], Math.min(fontRendererObj.getStringWidth(entitySign.signText[i]), maxLength));
            if (i > rows) {
                entitySign.signText[i] = "";
            }
        }

        PacketHandler.INSTANCE.sendToServer(new MessageSignUpdate(entitySign));
    }

    private void updateSize() {
        size = entitySign.fontSize;
        rows = Utils.getRows(size);
        maxLength = Utils.getMaxLength(size);

        row = Info.textPostion[size];
        minOffset = row[0];

        if (row.length > 1 && entitySign.textOffset > minOffset) {
            for (int i = 0; i < row.length; i++) {
                if (entitySign.textOffset < row[i]) {
                    rows = i;
                    break;
                }
            }
        } else {
            rows = 1;
            entitySign.textOffset = entitySign.textOffset < minOffset ? minOffset : entitySign.textOffset;
        }
    }

    @Override
    public void updateScreen() {
        for (GuiTextField guiTextField : guiTextFields) guiTextField.updateCursorCounter();
    }

    /*@Override
    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == 0) {
                entitySign.markDirty();
                mc.thePlayer.closeScreen();
            }
            if (guiButton.id == 1) {
                entitySign.fontSize = isShiftKeyDown() ? entitySign.fontSize + 10 : entitySign.fontSize + 1;
                entitySign.fontSize = entitySign.fontSize + 1 > 20 ? 20 : entitySign.fontSize;
                updateSize();

                if (editLine > rows - 1) {
                    editLine = rows - 1;
                }

            }
            if (guiButton.id == 2) {
                entitySign.fontSize = isShiftKeyDown() ? entitySign.fontSize - 10 : entitySign.fontSize - 1;
                entitySign.fontSize = entitySign.fontSize - 1 < 0 ? 0 : entitySign.fontSize;
                updateSize();
            }

            if (guiButton.id == 3) {
                entitySign.textOffset = isShiftKeyDown() ? entitySign.textOffset + 10 : entitySign.textOffset + 1;
                entitySign.textOffset = entitySign.textOffset + 1 > 0 ? 0 : entitySign.textOffset;
                updateSize();
            }
            if (guiButton.id == 4) {
                entitySign.textOffset = isShiftKeyDown() ? entitySign.textOffset - 10 : entitySign.textOffset - 1;
                entitySign.textOffset = entitySign.textOffset - 1 < minOffset ? minOffset : entitySign.textOffset;
                updateSize();
            }
        }
    }            */

    @Override
    protected void keyTyped(char typedChar, int key) {

        if (selectedTextField != -1) {
            int index = 0;
            for (GuiTextField textField: guiTextFields) {
                if (textField.isFocused()) textField.textboxKeyTyped(typedChar, key);
                entitySign.signText[index++] = textField.getText();
            }
        }

        update();

        if (key == 200) {
            guiTextFields[selectedTextField].setFocused(false);
            selectedTextField = selectedTextField - 1 < 0 ? 3: selectedTextField - 1;
            guiTextFields[selectedTextField].setFocused(true);
        }

        if (key == 208 || key == 28 || key == 156) {
            guiTextFields[selectedTextField].setFocused(false);
            selectedTextField = selectedTextField + 1 > 3 ? 0: selectedTextField + 1;
            guiTextFields[selectedTextField].setFocused(true);
        }

        updateSize();
        /*int l = fontRendererObj.getStringWidth(entitySign.signText[editLine]);

        if (key == 14 && entitySign.signText[editLine].length() > 0) {
            if (l > maxLength)
                entitySign.signText[editLine] = fontRendererObj.trimStringToWidth(entitySign.signText[editLine], 90);

            entitySign.signText[editLine] = entitySign.signText[editLine].substring(0, entitySign.signText[editLine].length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && fontRendererObj.getCharWidth(typedChar) + l < maxLength) {
            entitySign.signText[editLine] = entitySign.signText[editLine] + typedChar;
            l = fontRendererObj.getStringWidth(entitySign.signText[editLine]);
        }                            */

        if (key == 1) {
            mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void drawScreen(int x, int y, float par3) {

        drawDefaultBackground();
        super.drawScreen(x, y, par3);

        bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        for (GuiButton button: buttons) {
            button.drawButton(this, x, y);
        }

        for (GuiTextField textField: guiTextFields) textField.drawTextBox();

        /*
        GL11.glColor4f(1, 1, 1, 1);


        if (SIZE_X - 1 <= x && x <= SIZE_X + SIZE_W && SIZE_Y - 1 <= y && y <= SIZE_Y + SIZE_H) {
            drawRect(SIZE_X - 1, SIZE_Y - 1, SIZE_X2 + 1, SIZE_Y2 + 1, -11250336);
        } else {
            drawRect(SIZE_X - 1, SIZE_Y - 1, SIZE_X2 + 1, SIZE_Y2 + 1, -6250336);
        }

        drawRect(SIZE_X, SIZE_Y, SIZE_X2, SIZE_Y2, -16777216);

        GL11.glColor4f(1, 1, 1, 1);
        if (OFFSET_X - 1 <= x && x <= OFFSET_X + OFFSET_W && OFFSET_Y - 1 <= y && y <= OFFSET_Y + OFFSET_H) {
            drawRect(OFFSET_X - 1, OFFSET_Y - 1, OFFSET_X2 + 1, OFFSET_Y2 + 1, -11250336);
        } else {
            drawRect(OFFSET_X - 1, OFFSET_Y - 1, OFFSET_X2 + 1, OFFSET_Y2 + 1, -6250336);
        }

        drawRect(OFFSET_X, OFFSET_Y, OFFSET_X2, OFFSET_Y2, -16777216);

        drawCenteredString(fontRendererObj, "^v><", width / 2 + 65 + 25, 155 - 3, 0xffffff);
        drawCenteredString(fontRendererObj, FONT_SIZE, width / 2 + 65 + 25, 40, 16777215);

        drawCenteredString(fontRendererObj, String.valueOf(entitySign.textOffset), width / 2 + 130 + 25, 95 - 3, 16777215);
        drawCenteredString(fontRendererObj, TEXT_OFFSET, width / 2 + 158, 40, 16777215);

        */
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) guiLeft + 162F, (float) guiTop - 27.0F, 40.0F);
        float scale = 93.75F;
        GL11.glScalef(-scale, -scale, -scale);

        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

        int i = entitySign.getBlockMetadata();
        entitySign.showInGui = true;
        int k = i & 0b111;

        float f3 = 0.0F;
        if (k == 0 || k == 1) {
            f3 = 180.0F;
        } else if (k == 2) {
            f3 = 180.0F;
        } else if (k == 4) {
            f3 = 90.0F;
        } else if (k == 5) {
            f3 = -90.0F;
        }

        GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
        if (((i & 0b1000) >> 3) == 1) {
            entitySign.blockMetadata = 2;

        }
        GL11.glTranslatef(0.0F, -0.8F, 0.0F);

        TileEntityRendererDispatcher.instance.renderTileEntityAt(entitySign, -0.5D, -0.75D, -0.5D, 0.0F);
        GL11.glPopMatrix();

        if (showColors) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 50.0F);

            bindTexture(texture);
            drawTexturedModalRect(guiLeft + 150, guiTop + 30, 0, 0, 60, 60);
            drawTexturedModalRect(guiLeft + 209, guiTop + 30, 219, 0, 5, 60);
            drawTexturedModalRect(guiLeft + 150, guiTop + 89, 0, 195, 35, 5);
            drawTexturedModalRect(guiLeft + 184, guiTop + 89, 194, 195, 30, 5);

            for (GuiColorButton color : colors) {
                color.draw(this, x, y);
            }

            int k1 = 0;
            int j = 0;
            for (GuiColor color: GuiColor.values()) {
                drawRect(guiLeft + 152 + 4 + k1 * 14, guiTop + 32 + 4 + j * 14, guiLeft + 152 + 14 + k1 * 14, guiTop + 32 + 14 + j * 14, color.getARGB());
                if (k1 > 2) {
                    k1 = 0;
                    j++;
                } else k1++;
            }
            GL11.glPopMatrix();

            for (GuiColorButton button: colors) {
                if (button.inRect(x, y)) {
                    Localization.GUI.COLORS s = Localization.GUI.COLORS.values()[button.getId(this, x, y)];
                    drawHoveringText(Lists.asList(s.translate(), new String[0]), x, y, fontRendererObj);
                }
            }
        }

        if (showTextStyles) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 51.0F);

            bindTexture(texture);

            drawTexturedModalRect(guiLeft + 150, guiTop + 30, 0, 0, 55, 111);
            drawTexturedModalRect(guiLeft + 205, guiTop + 30, 219, 0, 5, 111);

            drawTexturedModalRect(guiLeft + 150, guiTop + 141, 0, 195, 35, 5);
            drawTexturedModalRect(guiLeft + 180, guiTop + 141, 194, 195, 30, 5);

            for (GuiTextStyleButton button: styleButtons) {
                button.draw(this, x, y);
            }
            GL11.glPopMatrix();

            for (GuiTextStyleButton button: styleButtons) {
                if (button.inRect(x, y)) drawHoveringText(Lists.asList(button.getName(), new String[0]), x, y, fontRendererObj);
            }
        }

        for (GuiButton button: buttons) button.hoverText(this, x, y);
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        if (b == 0) {
            boolean noTextFieldClick = false;

            if (showColors) {
                int id;
                for (GuiColorButton button : colors) {
                    id = button.getId(this, x, y);
                    if (id != -1) {
                        showColors = false;
                        guiTextFields[selectedTextField].setFocused(true);
                        guiTextFields[selectedTextField].writeText("{" + Integer.toHexString(GuiColor.values()[id].getNumber()) + "}");
                        update();
                        noTextFieldClick = true;

                        buttonColorPicker.onClick(this, x, y);
                    }
                }
            }

            if (showTextStyles) {
                for (GuiTextStyleButton button : styleButtons) {
                    if (button.inRect(x, y)) {
                        showTextStyles = false;
                        guiTextFields[selectedTextField].setFocused(true);
                        guiTextFields[selectedTextField].writeText("{" + button.getStyleChar(x, y) + "}");
                        update();
                        noTextFieldClick = true;

                        buttonTextStyle.onClick(this, x, y);
                    }
                }
            }

            for (GuiButton button : buttons) {
                if (!button.isDisabled && button.onClick(this, x, y)) {
                    noTextFieldClick = true;
                    update();
                    if (selectedTextField != -1) guiTextFields[selectedTextField].setFocused(true);
                }
            }

            if (!noTextFieldClick) {

                for (GuiTextField guiTextField : guiTextFields) {
                    guiTextField.mouseClicked(x, y, b);
                }

                boolean newSet = false;
                for (int i = 0; i < guiTextFields.length; i++) {
                    if (guiTextFields[i].isFocused()) {
                        selectedTextField = i;
                        newSet = true;
                    }
                }

                if (!newSet) {
                    selectedTextField = -1;
                }
            }
        }

        update();
    }

    int oldSelectedIndex = -1;
    public void update() {

        for (GuiButton button: buttons) {
            button.update(this);
        }

        String s = "";
        String[] array = new String[guiTextFields.length];

        for (int i = 0; i < 4; i++) {
            array[i] = guiTextFields[i].getText();
            s += guiTextFields[i].getText();
        }

        if (!s.equals("")) {
            buttonCopySign.isDisabled = false;
            buttonCutSign.isDisabled = false;
            buttonErase.isDisabled = false;
        } else {
            buttonCopySign.isDisabled = true;
            buttonCutSign.isDisabled = true;
            buttonErase.isDisabled = true;
        }

        entitySign.signText = getSignTextWithColor(array);

        if (oldSelectedIndex != selectedTextField) oldSelectedIndex = selectedTextField;

    }

    public static String[] getSignTextWithColor(String[] array) {
        String[] result = new String[array.length];

        Pattern p = Pattern.compile("([a-z0-9])(?=})+");
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (!s.equals("")) {

                Matcher m = p.matcher(s);
                while (m.find()) {
                    s = s.replace("{" + m.group(1) + "}", "§" + m.group(1));
                }
            }
            result[i] = s;
        }

        return result;
    }

    public static String[] getSignTextWithCode(String[] array) {
        String[] result = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (!s.equals("")) {
                s = s.replaceAll("(§[a-z0-9])+", "{$1}");
                s = s.replaceAll("([§])+", "");
            }
            result[i] = s;
        }

        return result;
    }

}
