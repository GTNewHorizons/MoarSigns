package gory_moon.moarsigns.integration.tweaker;

import gory_moon.moarsigns.api.MaterialInfo;
import gory_moon.moarsigns.api.ShapedMoarSignRecipe;
import gory_moon.moarsigns.api.ShapedMoarSignRecipe.MatchType;
import gory_moon.moarsigns.api.ShapelessMoarSignRecipe;
import gory_moon.moarsigns.api.SignInfo;
import gory_moon.moarsigns.items.ItemMoarSign;
import gory_moon.moarsigns.items.ModItems;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1710.item.MCItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ZenClass("mods.MoarSigns")
public class Signs {

    private static List<ItemStack> signs = new ArrayList<ItemStack>();

    @ZenMethod
    public static void addShaped(IItemStack output, IIngredient[][] ingredients) {
        MineTweakerAPI.apply(new Add(false, toStack(output), toShapedObjects(ingredients)));
    }

    @ZenMethod
    public static void addShapeless(IItemStack output, IIngredient[] ingredients) {
        MineTweakerAPI.apply(new Add(true, toStack(output), toObjects(ingredients)));
    }

    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    @ZenMethod
    public static IItemStack[] getSigns(IIngredient ingredient) {
        IItemStack[] base = new IItemStack[0];
        if (ingredient instanceof MatchTypeEntry || ingredient instanceof MaterialEntry) {
            if (signs.isEmpty()) {
                ModItems.sign.getSubItemStacks(Signs.signs);
            }
            ArrayList<IItemStack> signs = new ArrayList<IItemStack>();
            if (ingredient instanceof MatchTypeEntry) {
                for (ItemStack stack : Signs.signs) {
                    SignInfo info = ItemMoarSign.getInfo(stack.getTagCompound());
                    if (ingredient.getInternal() == MatchType.ALL)
                        signs.add(new MCItemStack(stack));
                    else if (ingredient.getInternal() == MatchType.METAL && info.isMetal)
                        signs.add(new MCItemStack(stack));
                    else if (ingredient.getInternal() == MatchType.WOOD && !info.isMetal)
                        signs.add(new MCItemStack(stack));
                }
            } else {
                for (ItemStack stack : Signs.signs) {
                    SignInfo info = ItemMoarSign.getInfo(stack.getTagCompound());
                    if (((MaterialInfo) ingredient.getInternal()).materialName.equals(info.material.materialName))
                        signs.add(new MCItemStack(stack));
                }
            }
            return signs.toArray(base);
        }
        return Collections.emptyList().toArray(base);
    }

    @ZenMethod
    public static IItemStack getFirstSign(IIngredient ingredient) {
        IItemStack[] signs = getSigns(ingredient);
        if (signs != null && signs.length > 0)
            return signs[0];
        return null;
    }

    public static ItemStack toStack(IItemStack iStack) {
        return MineTweakerMC.getItemStack(iStack);
    }

    public static ItemStack[] toStacks(IItemStack[] iStack) {
        if (iStack == null) return null;
        else {
            ItemStack[] output = new ItemStack[iStack.length];
            for (int i = 0; i < iStack.length; i++) {
                output[i] = toStack(iStack[i]);
            }

            return output;
        }
    }

    public static Object toObject(IIngredient iStack) {
        if (iStack == null) return null;
        else {
            if (iStack instanceof IOreDictEntry) {
                return toString((IOreDictEntry) iStack);
            } else if (iStack instanceof IItemStack) {
                return toStack((IItemStack) iStack);
            } else if (iStack instanceof MatchTypeEntry) {
                return (MatchType) iStack.getInternal();
            } else if (iStack instanceof MaterialEntry) {
                return (MaterialInfo) iStack.getInternal();
            } else return null;
        }
    }

    public static Object[] toObjects(IIngredient[] ingredient) {
        if (ingredient == null) return null;
        else {
            Object[] output = new Object[ingredient.length];
            for (int i = 0; i < ingredient.length; i++) {
                if (ingredient[i] != null) {
                    output[i] = toObject(ingredient[i]);
                } else output[i] = "";
            }

            return output;
        }
    }

    public static Object[] toShapedObjects(IIngredient[][] ingredients) {
        if (ingredients == null) return null;
        else {
            ArrayList prep = new ArrayList();
            prep.add("abc");
            prep.add("def");
            prep.add("ghi");
            char[][] map = new char[][]{{'a', 'b', 'c'}, {'d', 'e', 'f'}, {'g', 'h', 'i'}};
            for (int x = 0; x < ingredients.length; x++) {
                if (ingredients[x] != null) {
                    for (int y = 0; y < ingredients[x].length; y++) {
                        if (ingredients[x][y] != null && x < map.length && y < map[x].length) {
                            prep.add(map[x][y]);
                            prep.add(toObject(ingredients[x][y]));
                        }
                    }
                }
            }
            return prep.toArray();
        }
    }

    public static String toString(IOreDictEntry entry) {
        return ((IOreDictEntry) entry).getName();
    }

    private static class Add implements IUndoableAction {
        private final boolean isShapeless;
        private final ItemStack output;
        private final Object[] recipe;
        private IRecipe iRecipe;

        public Add(boolean isShapeless, ItemStack output, Object... recipe) {
            this.isShapeless = isShapeless;
            this.output = output;
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            if (isShapeless) iRecipe = new ShapelessMoarSignRecipe(output, true, recipe);
            else iRecipe = new ShapedMoarSignRecipe(output, recipe);
            CraftingManager.getInstance().getRecipeList().add(iRecipe);
        }

        @Override
        public boolean canUndo() {
            return CraftingManager.getInstance().getRecipeList() != null;
        }

        @Override
        public void undo() {
            CraftingManager.getInstance().getRecipeList().remove(iRecipe);
        }

        @Override
        public String describe() {
            return "Adding MoarSign recipe for " + output.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Removing MoarSign recipe for " + output.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class Remove implements IUndoableAction {

        private final ItemStack output;
        private IRecipe iRecipe;

        public Remove(ItemStack output) {
            this.output = output;
        }

        @Override
        public void apply() {
            List<IRecipe> allRecipes = CraftingManager.getInstance().getRecipeList();
            for (IRecipe recipe : allRecipes) {
                if ((recipe instanceof ShapedMoarSignRecipe || recipe instanceof ShapelessMoarSignRecipe) && recipe.getRecipeOutput() != null && ItemStack.areItemStacksEqual(recipe.getRecipeOutput(), output)) {
                    iRecipe = recipe;
                    break;
                }
            }

            CraftingManager.getInstance().getRecipeList().remove(iRecipe);
        }

        @Override
        public boolean canUndo() {
            return CraftingManager.getInstance().getRecipeList() != null && iRecipe != null;
        }

        @Override
        public void undo() {
            CraftingManager.getInstance().getRecipeList().add(iRecipe);
        }

        @Override
        public String describe() {
            return "Removing MoarSign recipe for " + output.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Restoring MoarSign recipe for " + output.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
