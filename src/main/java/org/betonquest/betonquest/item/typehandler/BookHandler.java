package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class BookHandler {
    public final BookPart title = new BookPart(Config.getMessage(Config.getLanguage(), "unknown_title")) {
        @Override
        protected String convertStringToValue(final String value) {
            return ChatColor.translateAlternateColorCodes('&', super.convertStringToValue(value));
        }
    };

    public final BookPart author = new BookPart(Config.getMessage(Config.getLanguage(), "unknown_author"));

    public final AhDefaultTStuff<List<String>> text = new Text();

    public BookHandler() {
    }

    public static class BookPart extends AhDefaultTStuff<String> {
        public BookPart(final String defaultValue) {
            super(true, defaultValue);
        }

        @Override
        protected String convertStringToValue(final String value) {
            return value.replace("_", " ");
        }

        @SuppressWarnings("PMD.InefficientEmptyStringCheck")
        @Override
        public boolean check(@Nullable final String string) {
            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> string != null && string.equals(value);
                case FORBIDDEN -> string == null || string.trim().isEmpty();
            };
        }
    }

    public static class Text extends AhDefaultTStuff<List<String>> {
        public Text() {
            super(true, List.of(""));
        }

        @Override
        protected List<String> convertStringToValue(final String value) {
            final List<String> val = Utils.pagesFromString(value.replace("_", " "));
            val.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
            return val;
        }

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
        @Override
        public boolean check(@Nullable final List<String> list) {
            switch (existence) {
                case WHATEVER:
                    return true;
                case REQUIRED:
                    final List<String> value = get();
                    if (list == null || list.size() != value.size()) {
                        return false;
                    }
                    for (int i = 0; i < value.size(); i++) {
                        // this removes black color codes, bukkit adds them for some reason
                        String line = list.get(i).replaceAll("(§0)?\\n(§0)?", "\n");
                        while (line.startsWith("\"")) {
                            line = line.substring(1);
                        }
                        while (line.endsWith("\"")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        String pattern = value.get(i).replaceAll("(§0)?\\n(§0)?", "\n");
                        while (pattern.startsWith("\"")) {
                            pattern = pattern.substring(1);
                        }
                        while (pattern.endsWith("\"")) {
                            pattern = pattern.substring(0, pattern.length() - 1);
                        }
                        if (!line.equals(pattern)) {
                            return false;
                        }
                    }
                    return true;
                case FORBIDDEN:
                    return list == null || list.isEmpty() || list.size() == 1 && list.get(0).isEmpty();
            }
            return true;
        }
    }
}
