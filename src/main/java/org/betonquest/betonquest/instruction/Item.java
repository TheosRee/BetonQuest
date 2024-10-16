package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.ShortClassName")
public class Item {
    private final ItemID itemID;

    private final QuestItem questItem;

    private final VariableNumber amount;

    public Item(final ItemID itemID, final VariableNumber amount) throws InstructionParseException {
        this.itemID = itemID;
        this.questItem = new QuestItem(itemID);
        this.amount = amount;
    }

    public ItemID getID() {
        return itemID;
    }

    public QuestItem getItem() {
        return questItem;
    }

    public boolean isItemEqual(final ItemStack item) {
        return questItem.compare(item);
    }

    public VariableNumber getAmount() {
        return amount;
    }
}
