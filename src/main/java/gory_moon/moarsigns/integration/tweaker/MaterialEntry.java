package gory_moon.moarsigns.integration.tweaker;


import gory_moon.moarsigns.api.MaterialInfo;
import minetweaker.api.item.*;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.player.IPlayer;

import java.util.Collections;
import java.util.List;

public class MaterialEntry implements IIngredient {

    private MaterialInfo material;
    private String modID;

    public MaterialEntry(MaterialInfo material, String modID) {
        this.material = material;
        this.modID = modID;
    }

    public MaterialEntry(MaterialInfo material) {
        this(material, null);
    }

    @Override
    public String getMark() {
        return null;
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public List<IItemStack> getItems() {
        return Collections.emptyList();
    }

    @Override
    public List<ILiquidStack> getLiquids() {
        return Collections.emptyList();
    }

    @Override
    public IIngredient amount(int i) {
        return new MaterialEntry(material);
    }

    @Override
    public IIngredient or(IIngredient iIngredient) {
        return new IngredientOr(this, iIngredient);
    }

    @Override
    public IIngredient transform(IItemTransformer iItemTransformer) {
        return null;
    }

    @Override
    public IIngredient only(IItemCondition iItemCondition) {
        return null;
    }

    @Override
    public IIngredient marked(String s) {
        return null;
    }

    @Override
    public boolean matches(IItemStack iItemStack) {
        return false;
    }

    @Override
    public boolean matchesExact(IItemStack item) {
        return false;
    }

    @Override
    public boolean matches(ILiquidStack iLiquidStack) {
        return false;
    }

    @Override
    public boolean contains(IIngredient iIngredient) {
        return false;
    }

    @Override
    public IItemStack applyTransform(IItemStack iItemStack, IPlayer iPlayer) {
        return null;
    }

    @Override
    public boolean hasTransformers() {
        return false;
    }

    @Override
    public Object getInternal() {
        return material;
    }

    public String getModID() {
        return modID;
    }
}
