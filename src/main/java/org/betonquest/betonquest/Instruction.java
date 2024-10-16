package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.NewInstruction;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.BlockSelector;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.GodClass", "PMD.CommentRequired",
        "PMD.AvoidFieldNameMatchingTypeName", "PMD.AvoidLiteralsInIfCondition", "PMD.TooManyMethods",
        "PMD.CouplingBetweenObjects"})
public class Instruction extends NewInstruction {
    /**
     * Contract: Returns null when the parameter is null, otherwise the expected object.
     */
    private static final String NULL_NOT_NULL_CONTRACT = "null -> null; !null -> !null";

    public Instruction(final BetonQuestLogger log, final QuestPackage pack, @Nullable final ID identifier, final String instruction) {
        super(log, pack, identifier, instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param tokenizer   Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param log         logger to log failures when parsing the instruction string
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     */
    public Instruction(final Tokenizer tokenizer, final BetonQuestLogger log, final QuestPackage pack, final ID identifier, final String instruction) {
        super(tokenizer, log, pack, identifier, instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction raw instruction string
     * @param parts       parts that the instruction consists of
     */
    public Instruction(final QuestPackage pack, final ID identifier, final String instruction, final String... parts) {
        super(pack, identifier, instruction, parts);
    }

    /**
     * Get the original raw instruction string that was used to tokenize the parts of this instruction.
     *
     * @return the raw instruction string that defined this instruction
     * @deprecated try not to implement your own parsing and use other API of this class instead if possible
     */
    @Deprecated
    public String getInstruction() {
        return toString();
    }

    public VariableLocation getLocation() throws InstructionParseException {
        return getLocation(next());
    }

    /**
     * Gets a location from an (optional) argument.
     *
     * @param prefix argument prefix
     * @return the location if it was defined in the instruction
     * @throws InstructionParseException if the location format is invalid
     */
    public Optional<VariableLocation> getLocationArgument(final String prefix) throws InstructionParseException {
        final Optional<String> argument = getOptionalArgument(prefix);
        if (argument.isPresent()) {
            return Optional.of(getLocation(argument.get()));
        }
        return Optional.empty();
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public VariableLocation getLocation(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), getPackage(), string);
        } catch (final InstructionParseException e) {
            throw new PartParseException("Error while parsing location: " + e.getMessage(), e);
        }
    }

    public VariableNumber getVarNum() throws InstructionParseException {
        return getVarNum(next(), (value) -> {
        });
    }

    public VariableNumber getVarNum(final Variable.ValueChecker<Number> valueChecker) throws InstructionParseException {
        return getVarNum(next(), valueChecker);
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public VariableNumber getVarNum(@Nullable final String string) throws InstructionParseException {
        return getVarNum(string, (value) -> {
        });
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    public VariableNumber getVarNum(@Nullable final String string, final Variable.ValueChecker<Number> valueChecker) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new VariableNumber(getPackage(), string, valueChecker);
        } catch (final InstructionParseException e) {
            throw new PartParseException("Could not parse a number: " + e.getMessage(), e);
        }
    }

    public QuestItem getQuestItem() throws InstructionParseException {
        return getQuestItem(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public QuestItem getQuestItem(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new QuestItem(new ItemID(getPackage(), string));
        } catch (final ObjectNotFoundException | InstructionParseException e) {
            throw new PartParseException("Could not load '" + string + "' item: " + e.getMessage(), e);
        }
    }

    public Item[] getItemList() throws InstructionParseException {
        return getItemList(next());
    }

    /**
     * Gets a list of items from an (optional) argument.
     * If the argument is not given then an empty list will be returned.
     *
     * @param prefix argument prefix
     * @return array of items given; or empty list if there is no such argument
     * @throws InstructionParseException if the item definitions contain errors
     */
    public Item[] getItemListArgument(final String prefix) throws InstructionParseException {
        return getItemList(getOptionalArgument(prefix).orElse(null));
    }

    public Item[] getItemList(@Nullable final String string) throws InstructionParseException {
        final String[] array = getArray(string);
        final Item[] items = new Item[array.length];
        for (int i = 0; i < items.length; i++) {
            try {
                final ItemID item;
                final VariableNumber number;
                if (array[i].contains(":")) {
                    final String[] parts = array[i].split(":", 2);
                    item = getItem(parts[0]);
                    number = getVarNum(parts[1]);
                } else {
                    item = getItem(array[i]);
                    number = getVarNum("1");
                }
                items[i] = new Item(item, number);
            } catch (final InstructionParseException | NumberFormatException e) {
                throw new PartParseException("Error while parsing '" + array[i] + "' item: " + e.getMessage(), e);
            }
        }
        return items;
    }

    public Map<Enchantment, Integer> getEnchantments() throws InstructionParseException {
        return getEnchantments(next());
    }

    @SuppressWarnings({"deprecation", "PMD.ReturnEmptyCollectionRatherThanNull"})
    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public Map<Enchantment, Integer> getEnchantments(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        final Map<Enchantment, Integer> enchants = new HashMap<>();
        final String[] array = getArray(string);
        for (final String enchant : array) {
            final String[] enchParts = enchant.split(":");
            if (enchParts.length != 2) {
                throw new PartParseException("Wrong enchantment format: " + enchant);
            }
            final Enchantment enchantment = Enchantment.getByName(enchParts[0]);
            if (enchantment == null) {
                throw new PartParseException("Unknown enchantment type: " + enchParts[0]);
            }
            final int level;
            try {
                level = Integer.parseInt(enchParts[1]);
            } catch (final NumberFormatException e) {
                throw new PartParseException("Could not parse level in enchant: " + enchant, e);
            }
            enchants.put(enchantment, level);
        }
        return enchants;
    }

    public List<PotionEffect> getEffects() throws InstructionParseException {
        return getEffects(next());
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public List<PotionEffect> getEffects(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        final List<PotionEffect> effects = new ArrayList<>();
        final String[] array = getArray(string);
        for (final String effect : array) {
            final String[] effParts = effect.split(":");
            final PotionEffectType potionEffectType = PotionEffectType.getByName(effParts[0]);
            if (potionEffectType == null) {
                throw new PartParseException("Unknown potion effect" + effParts[0]);
            }
            final int power;
            final int duration;
            try {
                power = Integer.parseInt(effect.split(":")[1]) - 1;
                duration = Integer.parseInt(effect.split(":")[2]) * 20;
            } catch (final NumberFormatException e) {
                throw new PartParseException("Could not parse potion power/duration: " + effect, e);
            }
            effects.add(new PotionEffect(potionEffectType, duration, power));
        }
        return effects;
    }

    public Material getMaterial() throws InstructionParseException {
        return getMaterial(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public Material getMaterial(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Material.matchMaterial(string);
    }

    public BlockSelector getBlockSelector() throws InstructionParseException {
        return getBlockSelector(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public BlockSelector getBlockSelector(@Nullable final String string) throws InstructionParseException {
        return string == null ? null : new BlockSelector(string);
    }

    public EntityType getEntity() throws InstructionParseException {
        return getEnum(next(), EntityType.class);
    }

    public EntityType getEntity(final String string) throws InstructionParseException {
        return getEnum(string, EntityType.class);
    }

    public PotionType getPotion() throws InstructionParseException {
        return getEnum(next(), PotionType.class);
    }

    public PotionType getPotion(final String string) throws InstructionParseException {
        return getEnum(string, PotionType.class);
    }

    public EventID getEvent() throws InstructionParseException {
        return getEvent(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public EventID getEvent(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new EventID(getPackage(), string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading event: " + e.getMessage(), e);
        }
    }

    public ConditionID getCondition() throws InstructionParseException {
        return getCondition(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ConditionID getCondition(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ConditionID(getPackage(), string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading condition: " + e.getMessage(), e);
        }
    }

    public ObjectiveID getObjective() throws InstructionParseException {
        return getObjective(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ObjectiveID getObjective(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ObjectiveID(getPackage(), string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading objective: " + e.getMessage(), e);
        }
    }

    public ItemID getItem() throws InstructionParseException {
        return getItem(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ItemID getItem(@Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ItemID(getPackage(), string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading item: " + e.getMessage(), e);
        }
    }

    public byte getByte() throws InstructionParseException {
        return getByte(next(), (byte) 0);
    }

    public byte getByte(@Nullable final String string, final byte def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Byte.parseByte(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse byte value: " + string, e);
        }
    }

    public int getPositive() throws InstructionParseException {
        return getPositive(next(), 0);
    }

    public int getPositive(@Nullable final String string, final int def) throws InstructionParseException {
        final int number = getInt(string, def);
        if (number <= 0) {
            throw new InstructionParseException("Number cannot be less than 1");
        }
        return number;
    }

    public int getInt() throws InstructionParseException {
        return getInt(next(), 0);
    }

    public int getInt(@Nullable final String string, final int def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public long getLong() throws InstructionParseException {
        return getLong(next(), 0);
    }

    public long getLong(@Nullable final String string, final long def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Long.parseLong(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public double getDouble() throws InstructionParseException {
        return getDouble(next(), 0.0);
    }

    public double getDouble(@Nullable final String string, final double def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Double.parseDouble(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse decimal value: " + string, e);
        }
    }

    public interface Converter<T> extends NewInstruction.Converter<T> {
    }

    @SuppressWarnings("PMD.ShortClassName")
    public static class Item extends org.betonquest.betonquest.instruction.Item {
        public Item(final ItemID itemID, final VariableNumber amount) throws InstructionParseException {
            super(itemID, amount);
        }
    }

    public class PartParseException extends NewInstruction.PartParseException {
        @Serial
        private static final long serialVersionUID = 2007556828888605511L;

        /**
         * @param message The message
         * @see Exception#Exception(String)
         */
        public PartParseException(final String message) {
            super(message);
        }

        /**
         * @param message The message
         * @param cause   The Throwable
         * @see Exception#Exception(String, Throwable)
         */
        public PartParseException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
