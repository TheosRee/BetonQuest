package org.betonquest.betonquest.instruction;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class NewInstruction {
    /**
     * The raw instruction string.
     */
    protected final String instruction;

    /**
     * The quest package that this instruction belongs to.
     */
    private final QuestPackage pack;

    /**
     * The identifier for this instruction.
     */
    private final ID identifier;

    /**
     * The parts of the instruction. This is the result after tokenizing the raw instruction string.
     */
    private final String[] parts;

    private int nextIndex = 1;

    private int currentIndex = 1;

    @Nullable
    private String lastOptional;

    public NewInstruction(final BetonQuestLogger log, final QuestPackage pack, @Nullable final ID identifier, final String instruction) {
        this(new QuotingTokenizer(), log, pack, useFallbackIdIfNecessary(pack, identifier), instruction);
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
    public NewInstruction(final Tokenizer tokenizer, final BetonQuestLogger log, final QuestPackage pack, final ID identifier, final String instruction) {
        this.pack = pack;
        this.identifier = identifier;
        this.instruction = instruction;
        this.parts = tokenizeInstruction(tokenizer, pack, instruction, log);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction raw instruction string
     * @param parts       parts that the instruction consists of
     */
    public NewInstruction(final QuestPackage pack, final ID identifier, final String instruction, final String... parts) {
        this.pack = pack;
        this.identifier = identifier;
        this.instruction = instruction;
        this.parts = Arrays.copyOf(parts, parts.length);
    }

    private static ID useFallbackIdIfNecessary(final QuestPackage pack, @Nullable final ID identifier) {
        if (identifier != null) {
            return identifier;
        }
        try {
            return new NoID(pack);
        } catch (final ObjectNotFoundException e) {
            throw new IllegalStateException("Could not find instruction: " + e.getMessage(), e);
        }
    }

    private String[] tokenizeInstruction(final Tokenizer tokenizer, final QuestPackage pack, final String instruction, final BetonQuestLogger log) {
        try {
            return tokenizer.tokens(instruction);
        } catch (final TokenizerException e) {
            log.warn(pack, "Could not tokenize instruction '" + instruction + "': " + e.getMessage(), e);
            return new String[0];
        }
    }

    @Override
    public String toString() {
        return instruction;
    }

    /**
     * Get all parts of the instruction. The instruction type is omitted.
     *
     * @return all arguments
     */
    public String[] getAllParts() {
        return Arrays.copyOfRange(parts, 1, parts.length);
    }

    /**
     * Get remaining parts of the instruction. The instruction type is omitted, even if no parts have been consumed yet.
     *
     * @return all arguments joined together
     */
    public String[] getRemainingParts() {
        final String[] remainingParts = Arrays.copyOfRange(parts, nextIndex, parts.length);
        nextIndex = parts.length;
        currentIndex = parts.length - 1;
        return remainingParts;
    }

    public int size() {
        return parts.length;
    }

    public QuestPackage getPackage() {
        return pack;
    }

    public ID getID() {
        return identifier;
    }

    protected String[] getParts() {
        return Arrays.copyOf(parts, parts.length);
    }

    /**
     * Copy this instruction. The copy has no consumed arguments.
     *
     * @return a copy of this instruction
     */
    public Instruction copy() {
        return copy(identifier);
    }

    /**
     * Copy this instruction but overwrite the ID of the copy. The copy has no consumed arguments.
     *
     * @param newID the ID to identify the copied instruction with
     * @return copy of this instruction with the new ID
     */
    public Instruction copy(final ID newID) {
        return new Instruction(getPackage(), newID, instruction, getParts());
    }

    public boolean hasNext() {
        return currentIndex < parts.length - 1;
    }

    public String next() throws InstructionParseException {
        lastOptional = null;
        currentIndex = nextIndex;
        return getPart(nextIndex++);
    }

    public String current() throws InstructionParseException {
        lastOptional = null;
        currentIndex = nextIndex - 1;
        return getPart(currentIndex);
    }

    public String getPart(final int index) throws InstructionParseException {
        if (parts.length <= index) {
            throw new InstructionParseException("Not enough arguments");
        }
        lastOptional = null;
        currentIndex = index;
        return parts[index];
    }

    /**
     * Gets an optional key:value instruction argument or null if the key is not present.
     *
     * @param prefix the prefix of the optional value without ":"
     * @return the value or null
     */
    @Nullable
    public String getOptional(final String prefix) {
        return getOptional(prefix, null);
    }

    /**
     * Gets an optional value or the default value if value is not present.
     *
     * @param prefix        the prefix of the optional value
     * @param defaultString the default value
     * @return the value or the default value
     */
    @Contract("_, !null -> !null")
    @Nullable
    public String getOptional(final String prefix, @Nullable final String defaultString) {
        return getOptionalArgument(prefix).orElse(defaultString);
    }

    /**
     * Gets an optional value with the given prefix.
     *
     * @param prefix the prefix of the optional value
     * @return an {@link Optional} containing the value or an empty {@link Optional} if the value is not present
     */
    public Optional<String> getOptionalArgument(final String prefix) {
        for (final String part : parts) {
            if (part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":")) {
                lastOptional = prefix;
                currentIndex = -1;
                return Optional.of(part.substring(prefix.length() + 1));
            }
        }
        return Optional.empty();
    }

    public boolean hasArgument(final String argument) {
        for (final String part : parts) {
            if (part.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    public <T> T fun(final Argument<T> argument) throws InstructionParseException {
        return fun(argument, next());
    }

    @Contract("_, !null -> !null")
    @Nullable
    public <T> T fun(final Argument<T> argument, @Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        return argument.convert(string);
    }

    public <T> T fun(final VariableArgument<T> argument) throws InstructionParseException {
        return fun(argument, next());
    }

    @Contract("_, !null -> !null")
    @Nullable
    public <T> T fun(final VariableArgument<T> argument, @Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        return argument.convert(BetonQuest.getInstance().getVariableProcessor(), pack, string);
    }

    public <T extends ID> T getID(final IDArgument<T> argument) throws InstructionParseException {
        return getID(argument, next());
    }

    @Contract("_, !null -> !null")
    @Nullable
    public <T extends ID> T getID(final IDArgument<T> argument, @Nullable final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return argument.convert(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading id: " + e.getMessage(), e);
        }
    }

    public <T extends Enum<T>> T getEnum(final Class<T> clazz) throws InstructionParseException {
        return getEnum(next(), clazz);
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    public <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz) throws InstructionParseException {
        return getEnum(string, clazz, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    public <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz, @Nullable final T defaultValue) throws InstructionParseException {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(clazz, string.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new PartParseException("There is no such " + clazz.getSimpleName() + ": " + string, e);
        }
    }

    public String[] getArray() throws InstructionParseException {
        return getArray(next());
    }

    public String[] getArray(@Nullable final String string) {
        if (string == null) {
            return new String[0];
        }
        return StringUtils.split(string, ",");
    }

    public <T> T[] getArray(final Converter<T> converter) throws InstructionParseException {
        return getArray(next(), converter);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(@Nullable final String string, final Converter<T> converter) throws InstructionParseException {
        if (string == null) {
            return (T[]) new Object[0];
        }
        final String[] array = getArray(string);
        if (array.length == 0) {
            return (T[]) new Object[0];
        }

        final T first = converter.convert(array[0]);
        final T[] result = (T[]) Array.newInstance(first.getClass(), array.length);
        result[0] = first;

        for (int i = 1; i < array.length; i++) {
            result[i] = converter.convert(array[i]);
        }
        return result;
    }

    public <T> List<T> getList(final Converter<T> converter) throws InstructionParseException {
        return getList(next(), converter);
    }

    public <T> List<T> getList(@Nullable final String string, final Converter<T> converter) throws InstructionParseException {
        if (string == null) {
            return new ArrayList<>(0);
        }
        final String[] array = getArray(string);
        final List<T> list = new ArrayList<>(array.length);
        for (final String part : array) {
            list.add(converter.convert(part));
        }
        return list;
    }

    public interface Converter<T> {
        T convert(String string) throws InstructionParseException;
    }

    @SuppressWarnings("PMD.ShortClassName")
    public static class Item {
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

    public class PartParseException extends InstructionParseException {
        @Serial
        private static final long serialVersionUID = 2007556828888605511L;

        /**
         * @param message The message
         * @see Exception#Exception(String)
         */
        public PartParseException(final String message) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message);
        }

        /**
         * @param message The message
         * @param cause   The Throwable
         * @see Exception#Exception(String, Throwable)
         */
        public PartParseException(final String message, final Throwable cause) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message, cause);
        }
    }
}
