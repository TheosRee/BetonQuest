package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Item id to generate the QuestItem with.
     */
    private final ItemID itemID;

    /**
     * Size of the stack to create.
     */
    private final VariableNumber amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param featureAPI the feature api creating new items
     * @param itemID     the QuestItemID to create
     * @param amount     the size to set the created ItemStack to
     * @throws QuestException when the QuestItem could not be created
     */
    public Item(final FeatureAPI featureAPI, final ItemID itemID, final VariableNumber amount) throws QuestException {
        this.itemID = itemID;
        this.featureAPI = featureAPI;
        this.amount = amount;
    }

    /**
     * Generates the item stack.
     *
     * @param profile the profile for variable resolving
     * @return the generated bukkit item
     * @throws QuestException when the generation fails
     */
    public ItemStack generate(final Profile profile) throws QuestException {
        return featureAPI.getItem(itemID).generate(amount.getValue(profile).intValue(), profile);
    }

    /**
     * Checks if the Item matches.
     *
     * @param item the item to compare
     * @return true if the given item matches the quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    public boolean matches(@Nullable final ItemStack item) throws QuestException {
        return featureAPI.getItem(itemID).matches(item);
    }

    /**
     * Gets the stored ID used to generate the Quest Item.
     *
     * @return item id of the item
     */
    public ItemID getID() {
        return itemID;
    }

    /**
     * Gets the Quest Item.
     *
     * @return the stored quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    public QuestItem getItem() throws QuestException {
        return featureAPI.getItem(itemID);
    }

    /**
     * Gets the amount to set.
     *
     * @return the stores amount
     */
    public VariableNumber getAmount() {
        return amount;
    }
}
