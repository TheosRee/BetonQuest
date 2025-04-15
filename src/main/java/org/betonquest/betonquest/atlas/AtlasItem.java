package org.betonquest.betonquest.atlas;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AtlasItem implements QuestItem {
    /**
     * Atlas Item material Key.
     */
    private static final NamespacedKey ITEM_KEY = new NamespacedKey("atlas", "material");

    /**
     * Item Registry to get ItemStacks from the material.
     */
    private final ItemRegistry itemRegistry;

    /**
     * Material of this item.
     */
    private final String material;

    public AtlasItem(final String material) {
        this.material = material;
    }

    @Override
    public String getName() {
        return material.toLowerCase().replace("_", " ");
    }

    @Override
    public List<String> getLore() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
        ItemStack itemStack = itemRegistry.getItem(material);
        if (itemStack == null) {
            itemStack = new ItemStack(Material.BARRIER);
        }
        itemStack.setAmount(stackSize);
        return itemStack;
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        if (item == null || item.getType().isEmpty() || !item.hasItemMeta()) {
            return false;
        }
        final String otherMaterial = item.getItemMeta().getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING);
        return material.equals(otherMaterial);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof AtlasItem otherItem && material.equals(otherItem.material);
    }
}
