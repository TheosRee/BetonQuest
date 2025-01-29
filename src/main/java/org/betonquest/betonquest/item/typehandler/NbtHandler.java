package org.betonquest.betonquest.item.typehandler;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Handler for raw NBT data.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD"})
public class NbtHandler implements ItemStackHandler<ItemStack> {
    private final Map<String, Object> nbtData = new HashMap<>();

    private Existence existence = Existence.WHATEVER;

    public NbtHandler() {
    }

    public void require(final Map<String, String> nbt) {
        this.existence = Existence.REQUIRED;
        this.nbtData.putAll(nbt);
    }

    @Override
    public Class<ItemStack> clazz() {
        return ItemStack.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("nbts");
    }

    @Nullable
    @Override
    public String serializeToString(final ItemStack stack) {
        final ReadWriteNBT readWriteNBT = NBT.itemStackToNBT(stack);
        if (readWriteNBT == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        for (final String key : readWriteNBT.getKeys()) {
            if ("CustomModelData".equals(key) || "display".equals(key) || key.contains("itemsadder")) {
                continue;
            }
            switch (readWriteNBT.getType(key)) {
                case NBTTagInt -> appendFrame(result, key, "int", readWriteNBT.getInteger(key));
                case NBTTagLong -> appendFrame(result, key, "long", readWriteNBT.getLong(key));
                case NBTTagByte -> appendFrame(result, key, "byte", readWriteNBT.getByte(key));
                case NBTTagDouble -> appendFrame(result, key, "double", readWriteNBT.getDouble(key));
                case NBTTagFloat -> appendFrame(result, key, "float", readWriteNBT.getFloat(key));
                case NBTTagShort -> appendFrame(result, key, "short", readWriteNBT.getShort(key));
                case NBTTagString -> appendFrame(result, key, "string", readWriteNBT.getString(key));
                default -> {
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        result.deleteCharAt(result.length() - 1);
        return "nbts:" + result;
    }

    private void appendFrame(final StringBuilder result, final String key, final String type, final Object value) {
        result.append(key).append(':').append(type).append(':').append(value).append(',');
    }

    /**
     * Parses input data with key:type:value format.
     * Example: "key1:int:123,key2:string:hello,key3:boolean:true"
     */
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    @Override
    public void set(final String argKey, final String data) throws QuestException {
        if (!"nbts".equals(argKey)) {
            return;
        }
        try {
            final String[] entries = data.split(",");
            for (final String entry : entries) {
                final String[] keyTypeValue = entry.split(":", 3);
                if (keyTypeValue.length != 3) {
                    throw new QuestException("Invalid NBT data format: " + entry);
                }
                final String key = keyTypeValue[0];
                final String type = keyTypeValue[1].toLowerCase(Locale.ROOT);
                final String value = keyTypeValue[2];
                switch (type) {
                    case "int":
                    case "integer":
                        nbtData.put(key, Integer.parseInt(value));
                        break;
                    case "long":
                        nbtData.put(key, Long.parseLong(value));
                        break;
                    case "byte":
                        nbtData.put(key, Byte.parseByte(value));
                        break;
                    case "double":
                        nbtData.put(key, Double.parseDouble(value));
                        break;
                    case "float":
                        nbtData.put(key, Float.parseFloat(value));
                        break;
                    case "short":
                        nbtData.put(key, Short.parseShort(value));
                        break;
                    case "string":
                    default:
                        nbtData.put(key, value);
                }
            }
            this.existence = Existence.REQUIRED;
        } catch (final Exception e) {
            this.existence = Existence.FORBIDDEN;
            throw new QuestException("Could not parse NBT data: " + data, e);
        }
    }

    @Override
    public void populate(final ItemStack stack) {
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
            NBT.modifyComponents(stack, nbt -> {
                final ReadWriteNBT customNbt = nbt.getOrCreateCompound("minecraft:custom_data");
                for (final Map.Entry<String, Object> entry : nbtData.entrySet()) {
                    final String nbtKey = entry.getKey();
                    final Object nbtObject = entry.getValue();

                    if (nbtObject instanceof final Integer value) {
                        customNbt.setInteger(nbtKey, value);
                    } else if (nbtObject instanceof final Long value) {
                        customNbt.setLong(nbtKey, value);
                    } else if (nbtObject instanceof final Byte value) {
                        customNbt.setByte(nbtKey, value);
                    } else if (nbtObject instanceof final Double value) {
                        customNbt.setDouble(nbtKey, value);
                    } else if (nbtObject instanceof final Float value) {
                        customNbt.setFloat(nbtKey, value);
                    } else if (nbtObject instanceof final Short value) {
                        customNbt.setShort(nbtKey, value);
                    } else if (nbtObject instanceof final String value) {
                        customNbt.setString(nbtKey, value);
                    }
                }
            });
        } else {
            for (final Map.Entry<String, Object> entry : nbtData.entrySet()) {
                final String nbtKey = entry.getKey();
                final Object nbtObject = entry.getValue();

                NBT.modify(stack, nbt -> {
                    if (nbtObject instanceof final Integer value) {
                        nbt.setInteger(nbtKey, value);
                    } else if (nbtObject instanceof final Long value) {
                        nbt.setLong(nbtKey, value);
                    } else if (nbtObject instanceof final Byte value) {
                        nbt.setByte(nbtKey, value);
                    } else if (nbtObject instanceof final Double value) {
                        nbt.setDouble(nbtKey, value);
                    } else if (nbtObject instanceof final Float value) {
                        nbt.setFloat(nbtKey, value);
                    } else if (nbtObject instanceof final Short value) {
                        nbt.setShort(nbtKey, value);
                    } else if (nbtObject instanceof final String value) {
                        nbt.setString(nbtKey, value);
                    }
                });
            }
        }
    }

    @Override
    public boolean check(final ItemStack stack) {
        if (existence == Existence.WHATEVER) {
            return true;
        }
        final NBTItem nbtItem = new NBTItem(stack);
        if (existence == Existence.FORBIDDEN) {
            return nbtData.keySet().stream().noneMatch(nbtItem::hasKey);
        }
        if (existence == Existence.REQUIRED) {
            for (final Map.Entry<String, Object> entry : nbtData.entrySet()) {
                final String key = entry.getKey();
                final Object expectedValue = entry.getValue();
                if (!nbtItem.hasTag(key)) {
                    return false;
                }
                boolean matches = false;

                if (expectedValue instanceof Integer) {
                    matches = nbtItem.getInteger(key).equals(expectedValue);
                } else if (expectedValue instanceof Double) {
                    matches = nbtItem.getDouble(key).equals(expectedValue);
                } else if (expectedValue instanceof Float) {
                    matches = nbtItem.getFloat(key).equals(expectedValue);
                } else if (expectedValue instanceof Boolean) {
                    matches = nbtItem.getBoolean(key).equals(expectedValue);
                } else if (expectedValue instanceof Short) {
                    matches = nbtItem.getShort(key).equals(expectedValue);
                } else if (expectedValue instanceof Long) {
                    matches = nbtItem.getLong(key).equals(expectedValue);
                } else if (expectedValue instanceof String) {
                    matches = nbtItem.getString(key).equalsIgnoreCase((String) expectedValue);
                }

                if (!matches) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (existence != Existence.REQUIRED) {
            return "";
        }
        // CustomModelData is a special case, it's not an NBT tag, but it's still useful to show it
        if (nbtData.isEmpty()) {
            return "";
        }
        final StringBuilder result = new StringBuilder("nbts:");
        for (final Map.Entry<String, Object> entry : nbtData.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            final String type = getValueType(value);
            if ("CustomModelData".equals(key) || "display".equals(key) || key.contains("itemsadder")) {
                continue;
            }
            if ("unknown".equals(type)) {
                continue;
            }
            result.append(key).append(':')
                    .append(type).append(':')
                    .append(value).append(',');
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public String getValueType(final Object value) {
        if (value instanceof Integer) {
            return "int";
        } else if (value instanceof Double) {
            return "double";
        } else if (value instanceof Float) {
            return "float";
        } else if (value instanceof Boolean) {
            return "boolean";
        } else if (value instanceof Short) {
            return "short";
        } else if (value instanceof Long) {
            return "long";
        } else if (value instanceof String) {
            return "string";
        }
        return "unknown";
    }
}
