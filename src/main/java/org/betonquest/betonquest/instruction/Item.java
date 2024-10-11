package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.Variable;
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
     * Item id to generate the QuestItem with.
     */
    private final Variable<ItemID> itemID;

    /**
     * The cached Quest Item, if without variables.
     */
    @Nullable
    private final QuestItem questItem;

    /**
     * Size of the stack to create.
     */
    private final VariableNumber amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param itemID the QuestItemID to create
     * @param amount the size to set the created ItemStack to
     * @throws QuestException when the QuestItem could not be created
     */
    public Item(final ItemID itemID, final VariableNumber amount) throws QuestException {
        this.itemID = new Variable<>(itemID);
        this.questItem = new QuestItem(itemID);
        this.amount = amount;
    }

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param itemID the QuestItemID to create
     * @param amount the size to set the created ItemStack to
     * @throws QuestException when the QuestItem could not be created
     */
    public Item(final Variable<ItemID> itemID, final VariableNumber amount) throws QuestException {
        this.itemID = itemID;
        if (itemID.isConstant()) {
            questItem = new QuestItem(itemID.getValue(null));
        } else {
            questItem = null;
        }
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
        return getItem(profile).generate(amount.getValue(profile).intValue(), profile);
    }

    /**
     * Checks if the Item is equal to the stored one.
     *
     * @param item the item to compare
     * @return true if the quest item is equal to the given item
     */
    public boolean isItemEqual(@Nullable final ItemStack item) {
        try {
            return getItem(null).compare(item);
        } catch (final QuestException exception) {
            return false;
        }
    }

    /**
     * Gets the stored ID used to generate the Quest Item.
     *
     * @return item id of the item
     */
    public Variable<ItemID> getID() {
        return itemID;
    }

    /**
     * Gets the Quest Item.
     *
     * @return quest item
     */
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        if (questItem != null) {
            return questItem;
        }
        return new QuestItem(itemID.getValue(profile));
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
