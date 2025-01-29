package org.betonquest.betonquest.item.typehandler;

import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handles de/-serialization of ItemMeta from/into QuestItem string format.
 *
 * @param <M> handled meta
 */
public interface ItemMetaHandler<M extends ItemMeta> extends ItemHandler<M, ItemMeta> {
    /**
     * Gets the class of meta this Handler works on.
     *
     * @return the ItemMeta class for the Handler
     */
    Class<M> metaClass();

    @Override
    default Class<M> clazz() {
        return metaClass();
    }
}
