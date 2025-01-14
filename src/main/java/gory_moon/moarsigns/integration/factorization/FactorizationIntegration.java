package gory_moon.moarsigns.integration.factorization;

import gory_moon.moarsigns.api.ISignRegistration;
import gory_moon.moarsigns.api.SignRegistry;
import gory_moon.moarsigns.lib.ModInfo;
import gory_moon.moarsigns.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class FactorizationIntegration implements ISignRegistration {

    private static final String FACTORIZATION_TAG = "factorization";
    private static final String FACTORIZATION_NAME = "Factorization";
    private Item silverItem = null;
    private Item leadItem = null;
    private Item darkIronItem = null;
    private Item itemBlock = null;

    @Override
    public void registerWoodenSigns(ArrayList<ItemStack> planks) {

    }

    @Override
    public void registerMetalSigns(ArrayList<ItemStack> metals) {
        for (ItemStack stacks : metals) {
            if (silverItem == null && stacks.getUnlocalizedName().equals("item.factorization:silver_ingot")) {
                silverItem = stacks.getItem();
            }

            if (leadItem == null && stacks.getUnlocalizedName().equals("item.factorization:lead_ingot")) {
                leadItem = stacks.getItem();
            }

            if (darkIronItem == null && stacks.getUnlocalizedName().equals("item.factorization:dark_iron_ingot")) {
                darkIronItem = stacks.getItem();
            }

            if (itemBlock == null && stacks.getUnlocalizedName().equals("tile.factorization.ResourceBlock.SILVERBLOCK")) {
                itemBlock = stacks.getItem();
            }

            if (silverItem != null && leadItem != null && darkIronItem != null && itemBlock != null) break;
        }

        SignRegistry.register("silver_sign", null, "silver", "factorization/", false, new ItemStack(silverItem), new ItemStack(itemBlock, 1, 1), ModInfo.ID, FACTORIZATION_TAG).setMetal();
        SignRegistry.register("lead_sign", null, "lead", "factorization/", false, new ItemStack(leadItem), new ItemStack(itemBlock, 1, 2), ModInfo.ID, FACTORIZATION_TAG).setMetal();
        SignRegistry.register("darkiron_sign", null, "darkiron", "factorization/", false, new ItemStack(darkIronItem), new ItemStack(itemBlock, 1, 3), ModInfo.ID, FACTORIZATION_TAG).setMetal();

    }

    @Override
    public String getActivateTag() {
        return FACTORIZATION_TAG;
    }

    @Override
    public String getIntegrationName() {
        return Utils.getModName(FACTORIZATION_TAG);
    }

    @Override
    public String getModName() {
        return FACTORIZATION_NAME;
    }
}
