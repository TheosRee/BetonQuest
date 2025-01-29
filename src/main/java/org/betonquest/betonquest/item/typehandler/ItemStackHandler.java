package org.betonquest.betonquest.item.typehandler;

import org.bukkit.inventory.ItemStack;

/**
 * Handles de/-serialization of ItemStack from/into QuestItem string format.
 *
 * @param <M> handled item stack
 */
public interface ItemStackHandler<M extends ItemStack> extends ItemHandler<M, ItemStack> {

}
