package org.betonquest.betonquest.compatibility.simplemagic;

import com.mc_atlas.simplemagic.api.SimpleMagicService;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
public class HasSpellCondition implements OnlineCondition {
    private final SimpleMagicService simpleMagic;

    private final int count;

    public HasSpellCondition(final SimpleMagicService simpleMagic, final int count) {
        this.simpleMagic = simpleMagic;
        this.count = count;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        int remaining = count;
        for (final ItemStack item : profile.getPlayer().getInventory()) {
            if (item != null && simpleMagic.isSpellBook(item) && !simpleMagic.isDudSpellBook(item)) {
                remaining -= item.getAmount();
                if (remaining <= 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
