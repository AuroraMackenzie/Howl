/**
 * Mackenzie Yao ( Steven Yao )
 * 2026
 * 1.0.0
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Howl {

    private enum EncodingMode {
        CHINESE("chinese", "嗷", "呜", "啊", "~"),
        ENGLISH("english", "ho", "wl", "au", "~");

        final String name;
        final String zero;
        final String one;
        final String two;
        final String three;

        EncodingMode(
                String name,
                String zero,
                String one,
                String two,
                String three) {
            this.name = name;
            this.zero = zero;
            this.one = one;
            this.two = two;
            this.three = three;
        }
    }

    // A TypeWriterSystem
    private static final class TypeWriterSystem {

        private static final double SPEED_FACTOR = 2.0;

        public static void typeWriter(String text, String languageMode)
                throws InterruptedException {
            if (text == null || text.isEmpty()) {
                return;
            }

            for (char c : text.toCharArray()) {
                System.out.print(c);
                int baseDelay = switch (languageMode) {
                    case "1" -> (int) (62.5 / SPEED_FACTOR);
                    case "2", "3" -> (int) (50 / SPEED_FACTOR);
                    default -> (int) (50 / SPEED_FACTOR);
                };

                if (c == '\n') {
                    TimeUnit.MILLISECONDS.sleep((int) (250 / SPEED_FACTOR));
                }
                else if (Character.isWhitespace(c) && c != '\n') {
                    TimeUnit.MILLISECONDS.sleep((int) (125 / SPEED_FACTOR));
                }
                else if (isPunctuationChar(c)) {
                    TimeUnit.MILLISECONDS.sleep(baseDelay);
                    TimeUnit.MILLISECONDS.sleep((int) (166.5 / SPEED_FACTOR));
                }
                else {
                    TimeUnit.MILLISECONDS.sleep(baseDelay);
                }
            }
        }

        private static boolean isPunctuationChar(char c) {
            if (",.;:!?()[]{}'\"".indexOf(c) != -1) {
                return true;
            }
            if ("。，、；：？！「」『』【】《》（）".indexOf(c) != -1) {
                return true;
            }

            int type = Character.getType(c);
            return (
                    type == Character.OTHER_PUNCTUATION ||
                            type == Character.INITIAL_QUOTE_PUNCTUATION ||
                            type == Character.FINAL_QUOTE_PUNCTUATION ||
                            type == Character.DASH_PUNCTUATION ||
                            type == Character.CONNECTOR_PUNCTUATION ||
                            type == Character.START_PUNCTUATION ||
                            type == Character.END_PUNCTUATION ||
                            type == Character.MATH_SYMBOL ||
                            type == Character.CURRENCY_SYMBOL ||
                            type == Character.MODIFIER_SYMBOL ||
                            type == Character.OTHER_SYMBOL
            );
        }

        public static void slowTypeWriter(
                String text,
                String languageMode,
                double slowFactor) throws InterruptedException {
            if (text == null || text.isEmpty()) {
                return;
            }

            for (char c : text.toCharArray()) {
                System.out.print(c);

                int baseDelay = switch (languageMode) {
                    case "1" -> (int) ((62.5 / SPEED_FACTOR) * slowFactor);
                    case "2", "3" -> (int) ((50 / SPEED_FACTOR) * slowFactor);
                    default -> (int) ((50 / SPEED_FACTOR) * slowFactor);
                };

                if (c == '\n') {
                    TimeUnit.MILLISECONDS.sleep((int) ((250 / SPEED_FACTOR) * slowFactor));
                }
                else if (Character.isWhitespace(c) && c != '\n') {
                    TimeUnit.MILLISECONDS.sleep((int) ((125 / SPEED_FACTOR) * slowFactor));
                }
                else if (isPunctuationChar(c)) {
                    TimeUnit.MILLISECONDS.sleep(baseDelay);
                    TimeUnit.MILLISECONDS.sleep((int) ((166.5 / SPEED_FACTOR) * slowFactor));
                }
                else {
                    TimeUnit.MILLISECONDS.sleep(baseDelay);
                }
            }
        }
    }

    private static final class InputLengthLimiter {

        private static final int ENCODE_MAX_CHARS = 10000; // encode character ≈ 10000
        private static final int DECODE_MAX_CHARS = 100000; // decode character ≈ 100000

        public static boolean isEncodeInputTooLong(
                String text,
                String languageMode) {
            if (text == null)
                return false;

            int charCount = text.length();

            // Count the actual number of characters (Chinese characters are counted by the character, and others are counted by the character)
            int wordCount = 0;
            for (char c : text.toCharArray()) {
                if (Character.UnicodeScript.of(c).equals(
                        Character.UnicodeScript.HAN)) {
                    wordCount++;
                }
                else if (!Character.isWhitespace(c) && !isPunctuationChar(c)) {
                    wordCount++;
                }
            }

            boolean charLimitExceeded = charCount > ENCODE_MAX_CHARS;
            boolean wordLimitExceeded = wordCount > 1000; // 1000 words in limited

            return charLimitExceeded || wordLimitExceeded;
        }

        public static boolean isDecodeInputTooLong(String text) {
            if (text == null)
                return false;

            int charCount = text.length();

            return charCount > DECODE_MAX_CHARS;
        }

        public static String getEncodeLimitMessage(String languageMode) {
            return switch (languageMode) {
                case "1" -> String.format(
                        """
                        ⚠️ 编码输入超出限制！
                           字符数限制：%d 字符
                           文字数限制：1000 字
                           请减少输入内容后重试。
                        """,
                        ENCODE_MAX_CHARS);
                case "2" -> String.format(
                        """
                        ⚠️ Encode input exceeds limit!
                           Character limit: %d chars
                           Word limit: 1000 words
                           Please reduce input and try again.
                        """,
                        ENCODE_MAX_CHARS);
                case "3" -> String.format(
                        """
                        ⚠️ ¡La entrada de codificación excede el límite!
                           Límite de caracteres: %d caracteres
                           Límite de palabras: 1000 palabras
                           Reduzca la entrada e intente de nuevo.
                        """,
                        ENCODE_MAX_CHARS);
                default -> String.format(
                        "Input exceeds limit of %d characters.",
                        ENCODE_MAX_CHARS);
            };
        }

        public static String getDecodeLimitMessage(String languageMode) {
            return switch (languageMode) {
                case "1" -> String.format(
                        """
                        ⚠️ 解码输入超出限制！
                           字符数限制：%d 字符
                           请减少输入内容后重试。
                        """,
                        DECODE_MAX_CHARS);
                case "2" -> String.format(
                        """
                        ⚠️ Decode input exceeds limit!
                           Character limit: %d chars
                           Please reduce input and try again.
                        """,
                        DECODE_MAX_CHARS);
                case "3" -> String.format(
                        """
                        ⚠️ ¡La entrada de decodificación excede el límite!
                           Límite de caracteres: %d caracteres
                           Reduzca la entrada e intente de nuevo.
                        """,
                        DECODE_MAX_CHARS);
                default -> String.format(
                        "Input exceeds limit of %d characters.",
                        DECODE_MAX_CHARS);
            };
        }

        public static Map<String, Integer> analyzeText(String text) {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("totalChars", 0);
            stats.put("chineseChars", 0);
            stats.put("englishChars", 0);
            stats.put("digitChars", 0);
            stats.put("punctuationChars", 0);
            stats.put("spaceChars", 0);
            stats.put("specialChars", 0);
            stats.put("wordCount", 0);

            if (text == null || text.isEmpty()) {
                return stats;
            }

            boolean inWord = false;
            for (char c : text.toCharArray()) {
                stats.merge("totalChars", 1, Integer::sum);

                if (Character.UnicodeScript.of(c).equals(
                        Character.UnicodeScript.HAN)) {
                    stats.merge("chineseChars", 1, Integer::sum);
                    if (!inWord) {
                        stats.merge("wordCount", 1, Integer::sum);
                        inWord = true;
                    }
                }
                else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    stats.merge("englishChars", 1, Integer::sum);
                    if (!inWord) {
                        stats.merge("wordCount", 1, Integer::sum);
                        inWord = true;
                    }
                }
                else if (Character.isDigit(c)) {
                    stats.merge("digitChars", 1, Integer::sum);
                }
                else if (isPunctuationChar(c)) {
                    stats.merge("punctuationChars", 1, Integer::sum);
                    inWord = false;
                }
                else if (Character.isWhitespace(c)) {
                    stats.merge("spaceChars", 1, Integer::sum);
                    inWord = false;
                }
                else {
                    stats.merge("specialChars", 1, Integer::sum);
                    if (!Character.isWhitespace(c) && !isPunctuationChar(c)) {
                        if (!inWord) {
                            stats.merge("wordCount", 1, Integer::sum);
                            inWord = true;
                        }
                    }
                    else {
                        inWord = false;
                    }
                }
            }

            return stats;
        }

        // The Report of The TypeWritingSystem
        public static void printAnalysisReport(String text, String languageMode)
                throws InterruptedException {
            Map<String, Integer> stats = analyzeText(text);

            String report = switch (languageMode) {
                case "1" -> String.format(
                        """
                        
                        📊 文本分析报告:
                           总字符数: %d
                           中文字符: %d
                           英文字符: %d
                           数字字符: %d
                           标点符号: %d
                           空格字符: %d
                           特殊字符: %d
                           估计字数: %d
                        """,
                        stats.get("totalChars"),
                        stats.get("chineseChars"),
                        stats.get("englishChars"),
                        stats.get("digitChars"),
                        stats.get("punctuationChars"),
                        stats.get("spaceChars"),
                        stats.get("specialChars"),
                        stats.get("wordCount"));
                case "2" -> String.format(
                        """
                        
                        📊 Text Analysis Report:
                           Total Characters: %d
                           Chinese Characters: %d
                           English Characters: %d
                           Digit Characters: %d
                           Punctuation Characters: %d
                           Space Characters: %d
                           Special Characters: %d
                           Estimated Word Count: %d
                        """,
                        stats.get("totalChars"),
                        stats.get("chineseChars"),
                        stats.get("englishChars"),
                        stats.get("digitChars"),
                        stats.get("punctuationChars"),
                        stats.get("spaceChars"),
                        stats.get("specialChars"),
                        stats.get("wordCount"));
                case "3" -> String.format(
                        """
                        
                        📊 Informe de Análisis de Texto:
                           Caracteres Totales: %d
                           Caracteres Chinos: %d
                           Caracteres Ingleses: %d
                           Caracteres Numéricos: %d
                           Caracteres de Puntuación: %d
                           Caracteres de Espacio: %d
                           Caracteres Especiales: %d
                           Conteo Estimado de Palabras: %d
                        """,
                        stats.get("totalChars"),
                        stats.get("chineseChars"),
                        stats.get("englishChars"),
                        stats.get("digitChars"),
                        stats.get("punctuationChars"),
                        stats.get("spaceChars"),
                        stats.get("specialChars"),
                        stats.get("wordCount"));
                default -> "Analysis report not available.";
            };

            TypeWriterSystem.typeWriter(report, languageMode);
        }

        private static boolean isPunctuationChar(char c) {
            return TypeWriterSystem.isPunctuationChar(c);
        }
    }

    // The Core Of "CompleteSentence"
    private static final class SentenceTerminatingPunctuationDetector {

        private static final List<TerminatingPunctuationPattern> CUSTOM_PUNCTUATION_PATTERNS;

        private static final List<TerminatingPunctuationPattern> TERMINATING_PUNCTUATION_PATTERNS;

        private static final Pattern COMPOUND_TERMINATOR_PATTERN;

        private static volatile int patternListVersion;

        static {
            CUSTOM_PUNCTUATION_PATTERNS = new ArrayList<>();
            COMPOUND_TERMINATOR_PATTERN = Pattern.compile(
                    "(?:(?:[!?！？]){2,}|[!?！？][!?！？])");
            patternListVersion = 1;
            TERMINATING_PUNCTUATION_PATTERNS =
                    initializeTerminatingPunctuationPatterns();
        }

        private SentenceTerminatingPunctuationDetector() {
            throw new UnsupportedOperationException(
                    " Class SentenceTerminatingPunctuationDetector is a is a utility class, should not be instantiated by sb./sth. ");
        }

        private static List<TerminatingPunctuationPattern> initializeTerminatingPunctuationPatterns() {
            List<TerminatingPunctuationPattern> patterns = new ArrayList<>();

            patterns.add(
                    new TerminatingPunctuationPattern(
                            "······",
                            "中文六点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern("......", "英文六点省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            "· · · · · ·",
                            "带空格的中文六点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            ". . . . . .",
                            "带空格的英文六点省略号"));

            patterns.add(
                    new TerminatingPunctuationPattern("·····", "中文五点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern(".....", "英文五点省略号"));

            patterns.add(
                    new TerminatingPunctuationPattern("····", "中文四点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern("....", "英文四点省略号"));

            patterns.add(
                    new TerminatingPunctuationPattern("···", "中文三点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern("...", "英文三点省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            "· · ·",
                            "带空格的中文三点居中省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            ". . .",
                            "带空格的英文三点省略号"));
            patterns.add(
                    new TerminatingPunctuationPattern("…", "中文省略号（单个字符）"));

            patterns.add(
                    new TerminatingPunctuationPattern("！？", "中文感叹号问号组合"));
            patterns.add(
                    new TerminatingPunctuationPattern("？！", "中文问号感叹号组合"));
            patterns.add(
                    new TerminatingPunctuationPattern("!?", "英文感叹号问号组合"));
            patterns.add(
                    new TerminatingPunctuationPattern("?!", "英文问号感叹号组合"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            "！！？？",
                            "中文双重感叹号问号组合"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            "？？！！",
                            "中文双重问号感叹号组合"));

            patterns.add(
                    new TerminatingPunctuationPattern("!!!", "三重感叹号"));
            patterns.add(
                    new TerminatingPunctuationPattern("？？？", "三重中文问号"));
            patterns.add(
                    new TerminatingPunctuationPattern("！！！", "三重中文感叹号"));
            patterns.add(
                    new TerminatingPunctuationPattern("???", "三重英文问号"));
            patterns.add(
                    new TerminatingPunctuationPattern("!!!!", "四重感叹号"));
            patterns.add(
                    new TerminatingPunctuationPattern("？？？？", "四重中文问号"));
            patterns.add(
                    new TerminatingPunctuationPattern("！！！！", "四重中文感叹号"));
            patterns.add(
                    new TerminatingPunctuationPattern("????", "四重英文问号"));
            patterns.add(
                    new TerminatingPunctuationPattern("!!!!!", "五重感叹号"));
            patterns.add(
                    new TerminatingPunctuationPattern("？？？？？", "五重中文问号"));
            patterns.add(
                    new TerminatingPunctuationPattern(
                            "！！！！！",
                            "五重中文感叹号"));

            patterns.add(new TerminatingPunctuationPattern("。", "中文句号"));
            patterns.add(new TerminatingPunctuationPattern("！", "中文感叹号"));
            patterns.add(new TerminatingPunctuationPattern("？", "中文问号"));
            patterns.add(new TerminatingPunctuationPattern(".", "英文句号"));
            patterns.add(new TerminatingPunctuationPattern("!", "英文感叹号"));
            patterns.add(new TerminatingPunctuationPattern("?", "英文问号"));

            patterns.add(new TerminatingPunctuationPattern("；", "中文分号"));
            patterns.add(new TerminatingPunctuationPattern(";", "英文分号"));
            patterns.add(new TerminatingPunctuationPattern("：", "中文冒号"));
            patterns.add(new TerminatingPunctuationPattern(":", "英文冒号"));

            List<TerminatingPunctuationPattern> allPatterns = new ArrayList<>();
            allPatterns.addAll(patterns);
            allPatterns.addAll(CUSTOM_PUNCTUATION_PATTERNS);

            allPatterns.sort(
                    Collections.reverseOrder(
                            Comparator.comparingInt(pattern ->
                                    pattern.getPattern().length())));
            return Collections.unmodifiableList(allPatterns);
        }

        // Completed Sentences Test 1
        public static boolean endsWithTerminatingPunctuation(String text) {
            if (text == null || text.isEmpty()) {
                return false;
            }

            // Method 1
            for (TerminatingPunctuationPattern pattern : TERMINATING_PUNCTUATION_PATTERNS) {
                if (text.endsWith(pattern.getPattern())) {
                    return true;
                }
            }

            // Method 2
            Matcher matcher = COMPOUND_TERMINATOR_PATTERN.matcher(text);
            if (matcher.find()) {
                int end = matcher.end();
                if (end == text.length()) {
                    return true;
                }
                String match = matcher.group();
                if (text.endsWith(match)) {
                    return true;
                }
            }
            return false;
        }

        // Completed Sentences Test 2
        public static String extractTerminatingPunctuation(String text) {
            if (text == null || text.isEmpty()) {
                return "";
            }

            // The Longest in a Priority
            for (TerminatingPunctuationPattern pattern : TERMINATING_PUNCTUATION_PATTERNS) {
                String punctuation = pattern.getPattern();
                if (text.endsWith(punctuation)) {
                    return punctuation;
                }
            }

            // Complex
            Matcher matcher = COMPOUND_TERMINATOR_PATTERN.matcher(text);
            String lastMatch = "";
            while (matcher.find()) {
                lastMatch = matcher.group();
            }

            // Corresponding Endings
            if (!lastMatch.isEmpty() && text.endsWith(lastMatch)) {
                return lastMatch;
            }
            return "";
        }

        // Emit usefuless The Last
        public static String removeTerminatingPunctuation(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }

            String punctuation = extractTerminatingPunctuation(text);
            if (!punctuation.isEmpty()) {
                return text.substring(0, text.length() - punctuation.length());
            }
            return text;
        }

        public static boolean containsTerminatingPunctuation(String text) {
            if (text == null || text.isEmpty()) {
                return false;
            }
            for (TerminatingPunctuationPattern pattern : TERMINATING_PUNCTUATION_PATTERNS) {
                if (text.contains(pattern.getPattern())) {
                    return true;
                }
            }
            return false;
        }

        public static List<PunctuationPosition> findAllTerminatingPunctuations(
                String text) {
            List<PunctuationPosition> positions = new ArrayList<>();

            if (text == null || text.isEmpty()) {
                return positions;
            }
            for (TerminatingPunctuationPattern pattern : TERMINATING_PUNCTUATION_PATTERNS) {
                String punctuation = pattern.getPattern();
                int index = 0;

                while (index < text.length()) {
                    int foundIndex = text.indexOf(punctuation, index);
                    if (foundIndex == -1) {
                        break;
                    }
                    positions.add(
                            new PunctuationPosition(
                                    punctuation,
                                    foundIndex,
                                    pattern.getDescription()));
                    index = foundIndex + punctuation.length();
                }
            }

            // Ordering
            positions.sort(
                    Comparator.comparingInt(PunctuationPosition::getPosition));
            return positions;
        }

        public static Set<String> getAllSupportedTerminatingPunctuations() {
            Set<String> punctuations = new LinkedHashSet<>();
            for (TerminatingPunctuationPattern pattern : TERMINATING_PUNCTUATION_PATTERNS) {
                punctuations.add(pattern.getPattern());
            }
            return Collections.unmodifiableSet(punctuations);
        }

        private static class TerminatingPunctuationPattern {

            private final String pattern;
            private final String description;

            public TerminatingPunctuationPattern(
                    String pattern,
                    String description) {
                this.pattern = pattern;
                this.description = description;
            }

            public String getPattern() {
                return pattern;
            }

            public String getDescription() {
                return description;
            }
        }

        public static class PunctuationPosition {

            private final String punctuation;
            private final int position;
            private final String description;

            public PunctuationPosition(
                    String punctuation,
                    int position,
                    String description) {
                this.punctuation = punctuation;
                this.position = position;
                this.description = description;
            }

            public String getPunctuation() {
                return punctuation;
            }

            public int getPosition() {
                return position;
            }

            public String getDescription() {
                return description;
            }
        }
    }

    private static final class AdvancedPunctuationDetector {

        public enum PunctuationType {
            CHINESE_PERIOD("中文句号"),
            ENGLISH_PERIOD("英文句号"),
            CHINESE_QUESTION_MARK("中文问号"),
            ENGLISH_QUESTION_MARK("英文问号"),
            CHINESE_EXCLAMATION_MARK("中文感叹号"),
            ENGLISH_EXCLAMATION_MARK("英文感叹号"),
            CHINESE_ELLIPSIS_THREE("中文三点省略号"),
            CHINESE_ELLIPSIS_SIX("中文六点省略号"),
            ENGLISH_ELLIPSIS_THREE("英文三点省略号"),
            ENGLISH_ELLIPSIS_SIX("英文六点省略号"),
            SPACED_CHINESE_ELLIPSIS_THREE("带空格的中文三点省略号"),
            SPACED_ENGLISH_ELLIPSIS_THREE("带空格的英文三点省略号"),
            SPACED_CHINESE_ELLIPSIS_SIX("带空格的中文六点省略号"),
            SPACED_ENGLISH_ELLIPSIS_SIX("带空格的英文六点省略号"),
            COMBINATION_EXCLAMATION_QUESTION("感叹号问号组合"),
            COMBINATION_QUESTION_EXCLAMATION("问号感叹号组合"),
            MULTIPLE_EXCLAMATION("多重感叹号"),
            MULTIPLE_QUESTION("多重问号"),
            UNKNOWN("未知类型");

            private final String description;

            PunctuationType(String description) {
                this.description = description;
            }

            public String getDescription() {
                return description;
            }
        }

        public static final class DetectionResult {

            private final boolean isTerminating;
            private final PunctuationType punctuationType;
            private final String punctuationText;
            private final int punctuationLength;

            public DetectionResult(
                    boolean isTerminating,
                    PunctuationType punctuationType,
                    String punctuationText,
                    int punctuationLength) {
                this.isTerminating = isTerminating;
                this.punctuationType = punctuationType;
                this.punctuationText = punctuationText;
                this.punctuationLength = punctuationLength;
            }

            public boolean isTerminating() {
                return isTerminating;
            }

            public PunctuationType getPunctuationType() {
                return punctuationType;
            }

            public String getPunctuationText() {
                return punctuationText;
            }

            public int getPunctuationLength() {
                return punctuationLength;
            }
        }

        private static final Map<String, PunctuationType> PUNCTUATION_MAP =
                createPunctuationMap();

        private static Map<String, PunctuationType> createPunctuationMap() {
            Map<String, PunctuationType> map = new HashMap<>();

            map.put("。", PunctuationType.CHINESE_PERIOD);
            map.put("？", PunctuationType.CHINESE_QUESTION_MARK);
            map.put("！", PunctuationType.CHINESE_EXCLAMATION_MARK);

            map.put(".", PunctuationType.ENGLISH_PERIOD);
            map.put("?", PunctuationType.ENGLISH_QUESTION_MARK);
            map.put("!", PunctuationType.ENGLISH_EXCLAMATION_MARK);

            map.put("···", PunctuationType.CHINESE_ELLIPSIS_THREE);
            map.put("······", PunctuationType.CHINESE_ELLIPSIS_SIX);
            map.put("...", PunctuationType.ENGLISH_ELLIPSIS_THREE);
            map.put("......", PunctuationType.ENGLISH_ELLIPSIS_SIX);
            map.put("· · ·", PunctuationType.SPACED_CHINESE_ELLIPSIS_THREE);
            map.put(". . .", PunctuationType.SPACED_ENGLISH_ELLIPSIS_THREE);
            map.put("· · · · · ·", PunctuationType.SPACED_CHINESE_ELLIPSIS_SIX);
            map.put(". . . . . .", PunctuationType.SPACED_ENGLISH_ELLIPSIS_SIX);
            map.put("…", PunctuationType.CHINESE_ELLIPSIS_THREE);

            map.put("！？", PunctuationType.COMBINATION_EXCLAMATION_QUESTION);
            map.put("？！", PunctuationType.COMBINATION_QUESTION_EXCLAMATION);
            map.put("!?", PunctuationType.COMBINATION_EXCLAMATION_QUESTION);
            map.put("?!", PunctuationType.COMBINATION_QUESTION_EXCLAMATION);

            map.put("!!!", PunctuationType.MULTIPLE_EXCLAMATION);
            map.put("？？？", PunctuationType.MULTIPLE_QUESTION);
            map.put("！！！", PunctuationType.MULTIPLE_EXCLAMATION);
            map.put("???", PunctuationType.MULTIPLE_QUESTION);

            return Map.copyOf(map);
        }

        public static DetectionResult detectTerminatingPunctuation(
                String text) {
            if (text == null || text.isEmpty()) {
                return new DetectionResult(
                        false,
                        PunctuationType.UNKNOWN,
                        "",
                        0);
            }

            for (Map.Entry<String, PunctuationType> entry : PUNCTUATION_MAP.entrySet()) {
                String punctuation = entry.getKey();
                if (text.endsWith(punctuation)) {
                    return new DetectionResult(
                            true,
                            entry.getValue(),
                            punctuation,
                            punctuation.length());
                }
            }

            String punctuation =
                    SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                            text);
            if (!punctuation.isEmpty()) {
                PunctuationType type = PUNCTUATION_MAP.getOrDefault(
                        punctuation,
                        PunctuationType.UNKNOWN);
                return new DetectionResult(
                        true,
                        type,
                        punctuation,
                        punctuation.length());
            }

            String lastTwoChars =
                    text.length() >= 2 ? text.substring(text.length() - 2) : "";

            if (lastTwoChars.matches("[!?！？]{2}")) {
                PunctuationType type = lastTwoChars.matches("[！？]{2}")
                        ? PunctuationType.COMBINATION_EXCLAMATION_QUESTION
                        : PunctuationType.COMBINATION_QUESTION_EXCLAMATION;
                return new DetectionResult(true, type, lastTwoChars, 2);
            }

            if (text.length() >= 3) {
                String lastThreeChars = text.substring(text.length() - 3);
                if (lastThreeChars.matches("(?:[!！]){3}")) {
                    return new DetectionResult(
                            true,
                            PunctuationType.MULTIPLE_EXCLAMATION,
                            lastThreeChars,
                            3);
                }
                if (lastThreeChars.matches("(?:[?？]){3}")) {
                    return new DetectionResult(
                            true,
                            PunctuationType.MULTIPLE_QUESTION,
                            lastThreeChars,
                            3);
                }
            }

            return new DetectionResult(false, PunctuationType.UNKNOWN, "", 0);
        }

        public static String detectLanguageType(String text) {
            if (text == null || text.isEmpty()) {
                return "未知";
            }

            // Counting Chinese characters and English characters
            int chineseCharCount = 0;
            int englishCharCount = 0;

            for (char c : text.toCharArray()) {
                if (c >= '\u4E00' && c <= '\u9FFF') {
                    chineseCharCount++;
                }
                else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    englishCharCount++;
                }
            }

            if (chineseCharCount > englishCharCount * 2) {
                return "中文为主";
            }
            else if (englishCharCount > chineseCharCount * 2) {
                return "英文为主";
            }
            else if (chineseCharCount > 0 && englishCharCount > 0) {
                return "中英混合";
            }
            else if (chineseCharCount > 0) {
                return "纯中文";
            }
            else if (englishCharCount > 0) {
                return "纯英文";
            }
            else {
                return "其他语言";
            }
        }
    }

    // The Setting of Languages
    private static class LanguageConfig {

        String languageName;
        String welcome;
        String initialInputPrompt;
        String subsequentInputPrompt;
        String encodedOutputPrefix;
        String decodedOutputPrefix;
        String binaryMappingTip;
        String continuePrompt;
        String exitPrompt;
        String detectionPrefix;
        String formatUsagePrefix;
        String outputLabel;
        String inputLabel;

        public LanguageConfig(
                String name,
                String welcome,
                String initialInputP,
                String subsequentInputP,
                String encodedOP,
                String decodedOP,
                String binaryM,
                String continueP,
                String exitP,
                String detectionP,
                String formatUsageP,
                String outputL,
                String inputL) {
            languageName = name;
            this.welcome = welcome;
            initialInputPrompt = initialInputP;
            subsequentInputPrompt = subsequentInputP;
            encodedOutputPrefix = encodedOP;
            decodedOutputPrefix = decodedOP;
            binaryMappingTip = binaryM;
            continuePrompt = continueP;
            exitPrompt = exitP;
            detectionPrefix = detectionP;
            formatUsagePrefix = formatUsageP;
            outputLabel = outputL;
            inputLabel = inputL;
        }
    }

    private static final Map<String, LanguageConfig> languageConfigs =
            new HashMap<>();

    static {
        languageConfigs.put(
                "1",
                new LanguageConfig(
                        "中文",
                        "=== 中文模式 ===\n欢迎使用Howl编码器！",
                        "\n请输入要编码/解码的文本: ",
                        "\n请输入: ",
                        "\n编码结果:\n",
                        "\n解码结果:\n",
                        "提示: 数学公式请用双引号\"\"括起来",
                        "\n继续? (输入'退出'结束): ",
                        "\n再见！",
                        "检测到",
                        "使用",
                        "\n输出: ",
                        "输入: "));

        languageConfigs.put(
                "2",
                new LanguageConfig(
                        "English",
                        "=== English Mode ===\nWelcome to Howl Encoder!",
                        "\nEnter text to encode/decode: ",
                        "\nEnter: ",
                        "\nEncoded result:\n",
                        "\nDecoded result:\n",
                        "Tip: Enclose math formulas in quotes \"\"",
                        "\nContinue? (type 'exit' to quit): ",
                        "\nGoodbye!",
                        "Detected",
                        "Using",
                        "Output: ",
                        "Input: "));

        languageConfigs.put(
                "3",
                new LanguageConfig(
                        "Español",
                        "=== Modo Español ===\n¡Bienvenido al codificador Howl!",
                        "\nIngrese texto para codificar/decodificar: ",
                        "\nIngrese: ",
                        "\nResultado codificado:\n",
                        "\nResultado decodificado:\n",
                        "Consejo: Encierre fórmulas matemáticas entre comillas \"\"",
                        "\n¿Continuar? (escriba 'salir' para terminar): ",
                        "\n¡Adiós Dodi!",
                        "Detectado",
                        "Usando",
                        "Resultado: ",
                        "Entrada: "));
    }

    // Pure Chinese?
    private static boolean isPureChinese(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        int chineseCharCount = 0;
        int totalCharCount = 0;

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }

            totalCharCount++;

            if (Character.UnicodeScript.of(c).equals(
                    Character.UnicodeScript.HAN)) {
                chineseCharCount++;
            }
            else if (isChinesePunctuation(c) || isCommonPunctuation(c)) {
                chineseCharCount++;
            }
            else if (Character.isLetter(c) &&
                    Character.UnicodeScript.of(c).equals(
                            Character.UnicodeScript.LATIN)) {
                return false;
            }
            else if (Character.isDigit(c)) {
                return false;
            }
        }

        return (
                totalCharCount > 0 &&
                        ((chineseCharCount * 100) / totalCharCount >= 70));
    }

    // Chinese Punctuations?
    private static boolean isChinesePunctuation(char c) {
        return "。，、；：？！「」『』【】《》（）".indexOf(c) != -1;
    }

    // Commons?
    private static boolean isCommonPunctuation(char c) {
        return ",.;:!?()[]{}'\"".indexOf(c) != -1;
    }

    // The end of Sentences
    private static boolean isSentenceEndPunctuation(String text, int index) {
        if (text == null || index >= text.length()) {
            return false;
        }

        String substring = text.substring(index);
        String punctuation =
                SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                        substring);
        return !punctuation.isEmpty() && substring.startsWith(punctuation);
    }

    private static boolean isEllipsis(String text, int index) {
        if (text == null || index >= text.length()) {
            return false;
        }

        String substring = text.substring(index);
        String punctuation =
                SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                        substring);

        if (!punctuation.isEmpty() && substring.startsWith(punctuation)) {
            AdvancedPunctuationDetector.DetectionResult result =
                    AdvancedPunctuationDetector.detectTerminatingPunctuation(
                            punctuation);
            AdvancedPunctuationDetector.PunctuationType type =
                    result.getPunctuationType();
            return (
                    type == AdvancedPunctuationDetector.PunctuationType.CHINESE_ELLIPSIS_THREE ||
                            type == AdvancedPunctuationDetector.PunctuationType.CHINESE_ELLIPSIS_SIX ||
                            type == AdvancedPunctuationDetector.PunctuationType.ENGLISH_ELLIPSIS_THREE ||
                            type == AdvancedPunctuationDetector.PunctuationType.ENGLISH_ELLIPSIS_SIX ||
                            type == AdvancedPunctuationDetector.PunctuationType.SPACED_CHINESE_ELLIPSIS_THREE ||
                            type == AdvancedPunctuationDetector.PunctuationType.SPACED_ENGLISH_ELLIPSIS_THREE ||
                            type == AdvancedPunctuationDetector.PunctuationType.SPACED_CHINESE_ELLIPSIS_SIX ||
                            type == AdvancedPunctuationDetector.PunctuationType.SPACED_ENGLISH_ELLIPSIS_SIX);
        }
        return false;
    }

    private static int getSentenceEndPunctuationLength(String text, int index) {
        if (text == null || index >= text.length()) {
            return 0;
        }

        char c = text.charAt(index);
        if ("。.!?！？…".indexOf(c) != -1) {
            return 1;
        }

        if (index + 2 < text.length()) {
            String threeChars = text.substring(index, index + 3);
            if (threeChars.equals("...") || threeChars.equals("···")) {
                return 3;
            }
        }

        if (index + 5 < text.length()) {
            String sixChars = text.substring(index, index + 6);
            if (sixChars.equals("......") || sixChars.equals("······")) {
                return 6;
            }
        }

        if (index + 5 < text.length()) {
            String sixSpacedChars = text.substring(index, index + 6);
            if (sixSpacedChars.equals(". . .") || sixSpacedChars.equals("· · ·")) {
                return 6;
            }
        }

        if (index + 6 < text.length()) {
            String sevenSpacedChars = text.substring(index, index + 7);
            if (sevenSpacedChars.equals(". . . ") ||
                    sevenSpacedChars.equals("· · · ")) {
                return 7;
            }
        }

        if (index + 1 < text.length()) {
            String twoChars = text.substring(index, index + 2);
            if (twoChars.equals("!?") ||
                    twoChars.equals("?!") ||
                    twoChars.equals("！？") ||
                    twoChars.equals("？！")) {
                return 2;
            }
        }

        if (index + 2 < text.length()) {
            String threeChars = text.substring(index, index + 3);
            if (threeChars.equals("!!!") ||
                    threeChars.equals("???") ||
                    threeChars.equals("！！！") ||
                    threeChars.equals("？？？")) {
                return 3;
            }
        }
        return 0;
    }

    // "String" --> binary System
    private static String textToBinary(String text) {
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            StringBuilder binary = new StringBuilder();

            for (byte b : bytes) {
                String byteStr = String.format(
                        "%8s",
                        Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                binary.append(byteStr);
            }

            return binary.toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    // binary System --> "String"
    private static String binaryToText(String binaryStr) {
        try {
            int padding = (8 - (binaryStr.length() % 8)) % 8;
            binaryStr = "0".repeat(padding) + binaryStr;

            int byteCount = binaryStr.length() / 8;
            byte[] bytes = new byte[byteCount];

            for (int i = 0; i < byteCount; i++) {
                String byteStr = binaryStr.substring(i * 8, (i + 1) * 8);
                bytes[i] = (byte) Integer.parseInt(byteStr, 2);
            }

            return new String(bytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            return "";
        }
    }

    private static String encodeByWordGroup(String text, EncodingMode mode) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1);

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];

            if (line.isEmpty()) {
                if (lineIndex < lines.length - 1) {
                    result.append("\n");
                }
                continue;
            }

            StringBuilder currentWord = new StringBuilder();

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isWhitespace(c) && c != '\n') {
                    if (currentWord.length() > 0) {
                        String encodedWord = encodeSingleWord(
                                currentWord.toString(),
                                mode);
                        result.append(encodedWord).append(" ");
                        currentWord.setLength(0);
                    }
                    String encodedSpace = encodeSingleWord(" ", mode);
                    result.append(encodedSpace).append(" ");
                    continue;
                }
                if (TypeWriterSystem.isPunctuationChar(c)) {
                    if (currentWord.length() > 0) {
                        String encodedWord = encodeSingleWord(
                                currentWord.toString(),
                                mode);
                        result.append(encodedWord).append(" ");
                        currentWord.setLength(0);
                    }
                    String encodedPunctuation = encodeSingleWord(
                            String.valueOf(c),
                            mode);
                    result.append(encodedPunctuation).append(" ");
                    continue;
                }
                currentWord.append(c);
            }
            if (currentWord.length() > 0) {
                String encodedWord = encodeSingleWord(
                        currentWord.toString(),
                        mode);
                result.append(encodedWord).append(" ");
            }
            if (result.length() > 0 && result.charAt(result.length() - 1) == ' ') {
                result.deleteCharAt(result.length() - 1);
            }
            if (lineIndex < lines.length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    // Encode Single Letters / Words
    private static String encodeSingleWord(String word, EncodingMode mode) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        String binary = textToBinary(word);

        if (binary.length() % 2 != 0) {
            binary = binary + "0"; // 补0
        }

        StringBuilder encoded = new StringBuilder();

        for (int i = 0; i < binary.length(); i += 2) {
            String pair = binary.substring(i, Math.min(i + 2, binary.length()));

            switch (pair) {
                case "00":
                    encoded.append(mode.zero);
                    break;
                case "01":
                    encoded.append(mode.one);
                    break;
                case "10":
                    encoded.append(mode.two);
                    break;
                case "11":
                    encoded.append(mode.three);
                    break;
            }
        }
        return encoded.toString();
    }

    private static String decodeByWordGroup(String encoded, EncodingMode mode) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        String[] groups = encoded.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String group : groups) {
            if (group.isEmpty()) {
                continue;
            }

            String decodedWord = decodeSingleGroup(group, mode);
            result.append(decodedWord);
        }

        return result.toString();
    }

    private static String decodeSingleGroup(String group, EncodingMode mode) {
        if (group == null || group.isEmpty()) {
            return "";
        }

        StringBuilder binary = new StringBuilder();

        if (mode == EncodingMode.CHINESE) {
            for (int i = 0; i < group.length(); i++) {
                char c = group.charAt(i);
                switch (c) {
                    case '嗷':
                        binary.append("00");
                        break;
                    case '呜':
                        binary.append("01");
                        break;
                    case '啊':
                        binary.append("10");
                        break;
                    case '~':
                        binary.append("11");
                        break;
                    default:
                        break;
                }
            }
        }
        else {
            String cleaned = group.replaceAll("\\s+", "");
            for (int i = 0; i < cleaned.length(); i++) {
                if (i + 1 < cleaned.length()) {
                    String pair = cleaned.substring(i, i + 2);
                    if (pair.equals("ho")) {
                        binary.append("00");
                        i++;
                    }
                    else if (pair.equals("wl")) {
                        binary.append("01");
                        i++;
                    }
                    else if (pair.equals("au")) {
                        binary.append("10");
                        i++;
                    }
                    else if (pair.startsWith("~")) {
                        binary.append("11");
                    }
                    else {
                        if (cleaned.charAt(i) == '~') {
                            binary.append("11");
                        }
                    }
                }
                else {
                    if (cleaned.charAt(i) == '~') {
                        binary.append("11");
                    }
                }
            }
        }
        return binaryToText(binary.toString());
    }

    private static boolean isTerminatingPunctuationChar(char c) {
        return "。.!?！？".indexOf(c) != -1;
    }

    private static boolean isTerminatingPunctuation(String text, int index) {
        if (text == null || index >= text.length()) {
            return false;
        }
        char c = text.charAt(index);
        if ("。.!?！？…".indexOf(c) != -1) {
            return true;
        }
        if (index + 2 < text.length()) {
            String threeChars = text.substring(index, index + 3);
            if (threeChars.equals("...") || threeChars.equals("···")) {
                return true;
            }
        }
        if (index + 5 < text.length()) {
            String sixChars = text.substring(index, index + 6);
            if (sixChars.equals("......") || sixChars.equals("······")) {
                return true;
            }
        }
        if (index + 5 < text.length()) {
            String sixSpacedChars = text.substring(index, index + 6);
            if (sixSpacedChars.equals(". . . ") ||
                    sixSpacedChars.equals("· · · ")) {
                return true;
            }
        }
        if (index + 1 < text.length()) {
            String twoChars = text.substring(index, index + 2);
            if (twoChars.equals("!?") ||
                    twoChars.equals("?!") ||
                    twoChars.equals("！？") ||
                    twoChars.equals("？！")) {
                return true;
            }
        }
        if (index + 2 < text.length()) {
            String threeChars = text.substring(index, index + 3);
            if (threeChars.equals("!!!") ||
                    threeChars.equals("???") ||
                    threeChars.equals("！！！") ||
                    threeChars.equals("？？？")) {
                return true;
            }
        }
        return false;
    }

    private static String restorePunctuationForDecoding(
            String text,
            boolean useChineseMode) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n");
        Map<Integer, String> sentenceMap = new TreeMap<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            int sentenceNumber = 0;
            String sentenceContent = line;
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "~(\\d+)$");
            java.util.regex.Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                try {
                    sentenceNumber = Integer.parseInt(matcher.group(1));
                    sentenceContent = line.substring(0, matcher.start());
                }
                catch (NumberFormatException e) {
                    sentenceNumber = sentenceMap.size() + 1;
                }
            }
            else {
                sentenceNumber = sentenceMap.size() + 1;
            }
            sentenceContent = sentenceContent.replaceAll("~\\d*", "");
            if (!useChineseMode && !sentenceContent.trim().isEmpty()) {
                sentenceContent = capitalizeFirstLetter(
                        sentenceContent.trim(),
                        false);
            }
            sentenceMap.put(sentenceNumber, sentenceContent);
        }
        boolean firstSentence = true;
        for (Map.Entry<Integer, String> entry : sentenceMap.entrySet()) {
            String sentence = entry.getValue().trim();
            if (!sentence.isEmpty()) {
                if (!firstSentence) {
                    result.append(" ");
                }
                result.append(sentence);
                firstSentence = false;
            }
        }
        return result.toString().trim();
    }

    private static String processSentenceNumberMarkers(
            String text,
            SentenceTracker tracker,
            int lineNumber) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Pattern markerPattern = Pattern.compile("~(\\d+)");
        Matcher matcher = markerPattern.matcher(text);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        int positionInLine = 0;

        while (matcher.find()) {
            String beforeMarker = text
                    .substring(lastEnd, matcher.start())
                    .trim();

            int sentenceNumber = 0;
            try {
                sentenceNumber = Integer.parseInt(matcher.group(1));
            }
            catch (NumberFormatException e) {
                sentenceNumber = ++positionInLine;
            }

            if (!beforeMarker.isEmpty()) {
                tracker.addSentence(
                        beforeMarker,
                        sentenceNumber,
                        lineNumber,
                        positionInLine++);
                result.append(beforeMarker).append(" ");
            }
            lastEnd = matcher.end();
        }

        String remainingText = text.substring(lastEnd).trim();
        if (!remainingText.isEmpty()) {
            tracker.addSentence(
                    remainingText,
                    ++positionInLine,
                    lineNumber,
                    positionInLine);
            result.append(remainingText).append(" ");
        }
        return matcher.replaceAll("").trim();
    }

    private static String[] splitByTerminatingPunctuation(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        int textLength = text.length();
        int i = 0;

        while (i < textLength) {
            char currentChar = text.charAt(i);
            currentSentence.append(currentChar);
            String remainingText = text.substring(i);
            String punctuation =
                    SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                            remainingText);
            if (!punctuation.isEmpty() && remainingText.startsWith(punctuation)) {
                String sentence = currentSentence.toString().trim();
                if (!sentence.isEmpty()) {
                    sentences.add(sentence);
                }
                currentSentence.setLength(0);
                i += punctuation.length();
            }
            else {
                i++;
            }
        }
        String lastSentence = currentSentence.toString().trim();
        if (!lastSentence.isEmpty()) {
            sentences.add(lastSentence);
        }
        return sentences.toArray(new String[0]);
    }

    private static String decodeWithSentenceTracking(
            String encoded,
            EncodingMode mode) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }
        SentenceTracker tracker = new SentenceTracker();
        boolean hasNewlines = encoded.contains("\n");
        if (hasNewlines) {
            String[] lines = encoded.split("\n", -1);

            for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
                String line = lines[lineIndex];
                String decodedLine = decodeByWordGroup(line, mode);

                if (decodedLine.trim().isEmpty()) {
                    continue;
                }
                processSentenceNumberMarkers(
                        decodedLine,
                        tracker,
                        lineIndex + 1);
            }
        }
        else {
            String decodedText = decodeByWordGroup(encoded, mode);
            processSentenceNumberMarkers(decodedText, tracker, 1);
            if (tracker.getSentenceCount() == 0) {
                String[] sentences = splitByTerminatingPunctuation(decodedText);
                tracker.addSentences(sentences);
            }
        }
        String orderedText = tracker.getOrderedText();

        if (orderedText.isEmpty()) {
            orderedText = tracker.getLineOrderedText();
        }
        orderedText = orderedText.replaceAll("~\\d+", "");
        orderedText = orderedText.replace("~", "");
        boolean useChineseMode = (mode == EncodingMode.CHINESE);
        orderedText = capitalizeFirstLetter(orderedText, useChineseMode);
        orderedText = orderedText.trim().replaceAll("\\s+", " ");
        return orderedText;
    }

    private static String enhancedDecode(String encoded, EncodingMode mode) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        try {
            String decodedWithTracking = decodeWithSentenceTracking(
                    encoded,
                    mode);

            if (decodedWithTracking != null &&
                    !decodedWithTracking.trim().isEmpty()) {
                return decodedWithTracking;
            }
        }
        catch (Exception e) {
            System.err.println("句子跟踪机制失败: " + e.getMessage());
        }

        boolean hasNewlines = encoded.contains("\n");
        String processedEncoded = encoded;

        if (hasNewlines) {
            processedEncoded = encoded.replace("\n", " ");
        }
        processedEncoded = processedEncoded.replaceAll("\\s+", " ");
        String decodedText = decodeByWordGroup(processedEncoded.trim(), mode);
        if (decodedText.isEmpty()) {
            return "";
        }
        boolean useChineseMode = (mode == EncodingMode.CHINESE);
        decodedText = restorePunctuationForDecoding(
                decodedText,
                useChineseMode);
        decodedText = capitalizeFirstLetter(decodedText, useChineseMode);
        decodedText = decodedText.replace("~", "");
        decodedText = decodedText.trim().replaceAll("\\s+", " ");
        return decodedText;
    }

    private static String processPunctuationForEncoding(
            String text,
            boolean useChineseMode) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int i = 0;
        int textLength = text.length();
        int sentenceNumber = 0;

        while (i < textLength) {
            String remainingText = text.substring(i);
            String punctuation =
                    SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                            remainingText);

            boolean foundSentenceEnd = false;

            if (!punctuation.isEmpty() && remainingText.startsWith(punctuation)) {
                foundSentenceEnd = true;
            }
            else {
                // 检查单个字符标点
                char currentChar = text.charAt(i);
                if (isTerminatingPunctuationChar(currentChar)) {
                    punctuation = String.valueOf(currentChar);
                    foundSentenceEnd = true;
                }
            }

            if (foundSentenceEnd) {
                sentenceNumber++;
                result.append(punctuation);
                i += punctuation.length();
                result.append("~").append(sentenceNumber);
                result.append("\n");
                while (i < textLength && Character.isWhitespace(text.charAt(i))) {
                    i++;
                    if (!useChineseMode &&
                            i < textLength &&
                            !Character.isWhitespace(text.charAt(i))) {
                        result.append(" ");
                    }
                }
                continue;
            }
            result.append(text.charAt(i));
            i++;
        }
        if (sentenceNumber > 0) {
            char lastChar = text.charAt(textLength - 1);
            if (!isTerminatingPunctuationChar(lastChar)) {
                result.append("~").append(sentenceNumber + 1);
            }
        }
        return result.toString();
    }

    private static boolean isPunctuation(char c) {
        return (
                Character.getType(c) == Character.OTHER_PUNCTUATION ||
                        Character.getType(c) == Character.INITIAL_QUOTE_PUNCTUATION ||
                        Character.getType(c) == Character.FINAL_QUOTE_PUNCTUATION ||
                        Character.getType(c) == Character.DASH_PUNCTUATION ||
                        Character.getType(c) == Character.CONNECTOR_PUNCTUATION ||
                        Character.getType(c) == Character.START_PUNCTUATION ||
                        Character.getType(c) == Character.END_PUNCTUATION ||
                        ",;:!?()-[]{}'\"".indexOf(c) != -1);
    }

    private static String capitalizeFirstLetter(
            String text,
            boolean useChineseMode) {
        if (useChineseMode || text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder();
        String[] sentences = text.split("(?<=[.!?])\\s+");

        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                boolean foundLetter = false;
                for (int j = 0; j < sentence.length(); j++) {
                    if (Character.isLetter(sentence.charAt(j))) {
                        sentence =
                                sentence.substring(0, j) +
                                        Character.toUpperCase(sentence.charAt(j)) +
                                        sentence.substring(j + 1);
                        foundLetter = true;
                        break;
                    }
                }
                result.append(sentence);
                if (i < sentences.length - 1) {
                    result.append(" ");
                }
            }
        }
        return result.toString().trim();
    }

    private static String enhancedEncode(String text, EncodingMode mode) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        boolean useChineseMode = (mode == EncodingMode.CHINESE);
        String processedText = processPunctuationForEncoding(
                text,
                useChineseMode);
        String encoded = encodeByWordGroup(processedText, mode);
        return encoded;
    }

    private static EncodingMode detectEncodingFormat(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        EncodingMode singleLineMode = detectSingleLineEncodingFormat(text);
        if (singleLineMode != null) {
            return singleLineMode;
        }
        String[] lines = text.split("\n");
        EncodingMode firstLineMode = null;
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            EncodingMode lineMode = detectSingleLineEncodingFormat(trimmedLine);
            if (lineMode == null) {
                return null;
            }
            if (firstLineMode == null) {
                firstLineMode = lineMode;
            }
            else if (firstLineMode != lineMode) {
                return null;
            }
        }
        return firstLineMode;
    }

    private static EncodingMode detectSingleLineEncodingFormat(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String cleaned = text.replaceAll("\\s+", "").replace("\n", "");
        if (cleaned.isEmpty()) {
            return null;
        }
        boolean isChineseEncoded = true;
        int chineseEncodedCount = 0;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == '嗷' || c == '呜' || c == '啊' || c == '~') {
                chineseEncodedCount++;
            }
            else {
                isChineseEncoded = false;
                break;
            }
        }
        if (isChineseEncoded && chineseEncodedCount >= 4) {
            return EncodingMode.CHINESE;
        }
        boolean isEnglishEncoded = true;
        int englishEncodedCount = 0;
        for (int i = 0; i < cleaned.length(); i++) {
            if (i + 1 < cleaned.length()) {
                String pair = cleaned.substring(i, i + 2);
                if (pair.equals("ho") || pair.equals("wl") || pair.equals("au")) {
                    englishEncodedCount++;
                    i++;
                    continue;
                }
            }
            if (cleaned.charAt(i) == '~') {
                englishEncodedCount++;
            }
            else {
                if (i > 0 && cleaned.charAt(i - 1) == '~') {
                    continue;
                }
                isEnglishEncoded = false;
                break;
            }
        }
        if (isEnglishEncoded && englishEncodedCount >= 4) {
            return EncodingMode.ENGLISH;
        }
        String englishPattern = cleaned
                .replace("ho", "")
                .replace("wl", "")
                .replace("au", "")
                .replace("~", "");
        if (englishPattern.length() <= cleaned.length() / 4 &&
                cleaned.length() >= 8) {
            return EncodingMode.ENGLISH;
        }
        return null;
    }

    //  √ / X  Epecial Punctuations
    private static boolean containsSpecialCharacters(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String specialChars =
                "↑↓←→↖↗↘↙↕" +
                        "!@#$%^&*()" +
                        "¡™£¢∞§¶•ªºœ∑´®†¥¨ˆøπåß∂ƒ©˙∆˚¬Ω≈ç√∫˜µ" +
                        "€‹›ﬁﬂ‡°·‚Œ„´‰ˇÁ¨ˆØ∏ÅÍÎÏ˝ÓÔÒ¸˛Ç◊ı˜Â" +
                        "-=[]\\;',./" +
                        "_+{}|:\"<>?" +
                        "–≠\"'«…æ≤≥÷" +
                        "—±\"'»ÚÆ¯˘¿;";

        for (char c : text.toCharArray()) {
            if (specialChars.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    private static String handleSpecialCharactersForEncoding(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text;
    }

    private static String readInputWithBufferedReader(
            BufferedReader reader,
            String prompt,
            LanguageConfig config) throws IOException {
        System.out.print(prompt);
        String firstLine = reader.readLine();

        if (firstLine == null) {
            return ""; // EOF reached
        }

        firstLine = firstLine.trim();

        if (config.languageName.equals("中文") && firstLine.equals("退出")) {
            return "EXIT_COMMAND";
        }
        else if (config.languageName.equals("English") &&
                firstLine.equalsIgnoreCase("exit")) {
            return "EXIT_COMMAND";
        }
        else if (config.languageName.equals("Español") &&
                firstLine.equalsIgnoreCase("salir")) {
            return "EXIT_COMMAND";
        }

        if (firstLine.isEmpty()) {
            return "";
        }

        EncodingMode detectedMode = detectEncodingFormat(firstLine);
        if (detectedMode != null) {
            StringBuilder multiLineInput = new StringBuilder(firstLine);
            long startTime = System.currentTimeMillis();
            long timeout = 500;

            while (System.currentTimeMillis() - startTime < timeout) {
                if (reader.ready()) {
                    String nextLine = reader.readLine();
                    if (nextLine != null) {
                        nextLine = nextLine.trim();
                        if (!nextLine.isEmpty()) {
                            EncodingMode nextMode = detectEncodingFormat(
                                    nextLine);
                            if (nextMode != null && nextMode == detectedMode) {
                                multiLineInput.append("\n").append(nextLine);
                                startTime = System.currentTimeMillis();
                            }
                            else {
                                break;
                            }
                        }
                    }
                }
                else {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            return multiLineInput.toString();
        }
        return firstLine;
    }

    private static class SentenceTracker {

        private final Map<Integer, SentenceInfo> sentenceInfoMap =
                new TreeMap<>();
        private final Map<String, Integer> contentToNumberMap = new HashMap<>();
        private int currentSentenceCounter = 0;

        private static class SentenceInfo {

            final String content;
            final int originalNumber;
            final int lineNumber;
            final long timestamp;

            SentenceInfo(String content, int originalNumber, int lineNumber) {
                this.content = content;
                this.originalNumber = originalNumber;
                this.lineNumber = lineNumber;
                this.timestamp = System.nanoTime(); // Steably Ordering
            }
        }

        public void addSentence(
                String sentence,
                int sentenceNumber,
                int lineNumber,
                int positionInLine) {
            if (sentence == null || sentence.trim().isEmpty()) {
                return;
            }
            String trimmedSentence = sentence.trim();

            if (contentToNumberMap.containsKey(trimmedSentence)) {
                return;
            }
            int finalSentenceNumber;
            if (sentenceNumber > 0) {
                finalSentenceNumber = sentenceNumber;
            }
            else {
                finalSentenceNumber =
                        (lineNumber * 10000) +
                                (positionInLine * 100) +
                                (++currentSentenceCounter);
            }
            SentenceInfo info = new SentenceInfo(
                    trimmedSentence,
                    finalSentenceNumber,
                    lineNumber);
            sentenceInfoMap.put(finalSentenceNumber, info);
            contentToNumberMap.put(trimmedSentence, finalSentenceNumber);
        }

        public void addSentence(String sentence) {
            if (sentence == null || sentence.trim().isEmpty()) {
                return;
            }
            String trimmedSentence = sentence.trim();
            if (contentToNumberMap.containsKey(trimmedSentence)) {
                return;
            }
            int sentenceNumber = ++currentSentenceCounter;
            SentenceInfo info = new SentenceInfo(
                    trimmedSentence,
                    sentenceNumber,
                    0);
            sentenceInfoMap.put(sentenceNumber, info);
            contentToNumberMap.put(trimmedSentence, sentenceNumber);
        }

        public void addSentences(String[] sentences) {
            if (sentences == null || sentences.length == 0) {
                return;
            }
            for (int i = 0; i < sentences.length; i++) {
                String sentence = sentences[i];
                if (sentence != null && !sentence.trim().isEmpty()) {
                    addSentence(sentence.trim(), i + 1, 0, i);
                }
            }
        }

        public void addSentenceWithNumber(String sentence, int sentenceNumber) {
            if (sentence == null || sentence.trim().isEmpty()) {
                return;
            }
            String trimmedSentence = sentence.trim();
            if (contentToNumberMap.containsKey(trimmedSentence)) {
                return;
            }
            SentenceInfo info = new SentenceInfo(
                    trimmedSentence,
                    sentenceNumber,
                    0);
            sentenceInfoMap.put(sentenceNumber, info);
            contentToNumberMap.put(trimmedSentence, sentenceNumber);
            if (sentenceNumber > currentSentenceCounter) {
                currentSentenceCounter = sentenceNumber;
            }
        }

        public String getOrderedText() {
            if (sentenceInfoMap.isEmpty()) {
                return "";
            }
            StringBuilder result = new StringBuilder();
            boolean firstSentence = true;
            for (Map.Entry<Integer, SentenceInfo> entry : sentenceInfoMap.entrySet()) {
                SentenceInfo info = entry.getValue();
                if (!firstSentence) {
                    result.append(" ");
                }
                result.append(info.content);
                firstSentence = false;
            }
            return result.toString();
        }

        public String getLineOrderedText() {
            if (sentenceInfoMap.isEmpty()) {
                return "";
            }
            List<SentenceInfo> sortedInfos = new ArrayList<>(
                    sentenceInfoMap.values());
            sortedInfos.sort((a, b) -> {
                if (a.lineNumber != b.lineNumber) {
                    return Integer.compare(a.lineNumber, b.lineNumber);
                }
                return Long.compare(a.timestamp, b.timestamp);
            });
            StringBuilder result = new StringBuilder();
            boolean firstSentence = true;

            for (SentenceInfo info : sortedInfos) {
                if (!firstSentence) {
                    result.append(" ");
                }
                result.append(info.content);
                firstSentence = false;
            }
            return result.toString();
        }

        public int getSentenceCount() {
            return sentenceInfoMap.size();
        }

        public String[] getAllSentences() {
            List<String> sentences = new ArrayList<>();
            for (SentenceInfo info : sentenceInfoMap.values()) {
                sentences.add(info.content);
            }
            return sentences.toArray(new String[0]);
        }

        public void clear() {
            sentenceInfoMap.clear();
            contentToNumberMap.clear();
            currentSentenceCounter = 0;
        }

        public boolean containsSentence(String sentence) {
            return (sentence != null &&
                    contentToNumberMap.containsKey(sentence.trim()));
        }

        public int getSentenceNumber(String sentence) {
            if (sentence == null) {
                return -1;
            }
            return contentToNumberMap.getOrDefault(sentence.trim(), -1);
        }

        public void merge(SentenceTracker other) {
            if (other == null) {
                return;
            }
            for (Map.Entry<Integer, SentenceInfo> entry : other.sentenceInfoMap.entrySet()) {
                SentenceInfo info = entry.getValue();
                if (!contentToNumberMap.containsKey(info.content)) {
                    int newNumber = sentenceInfoMap.size() + 1;
                    sentenceInfoMap.put(
                            newNumber,
                            new SentenceInfo(
                                    info.content,
                                    newNumber,
                                    info.lineNumber));
                    contentToNumberMap.put(info.content, newNumber);
                }
            }
            currentSentenceCounter = Math.max(
                    currentSentenceCounter,
                    other.currentSentenceCounter);
        }
    }

    // Main Function
    public static void main(String[] args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String languageChoice = "";
            LanguageConfig config = null;
            while (true) {
                System.out.print(
                        "请选择语言 / Select language / Seleccione idioma:\n" +
                                "1. 中文\n" +
                                "2. English\n" +
                                "3. Español\n" +
                                "> ");
                try {
                    languageChoice = reader.readLine();
                }
                catch (IOException e) {
                    System.err.println("读取语言选择时出错: " + e.getMessage());
                    return;
                }
                if (languageChoice == null) {
                    System.out.println("\n输入结束，程序退出。");
                    return;
                }
                languageChoice = languageChoice.trim();
                if (languageConfigs.containsKey(languageChoice)) {
                    config = languageConfigs.get(languageChoice);
                    System.out.println(config.welcome + "\n");
                    break;
                }
                else {
                    System.out.println(
                            "\n无效选择，请重新输入 / Invalid choice, please try again / Elección inválida, intente de nuevo\n");
                }
            }
            try {
                TypeWriterSystem.slowTypeWriter(
                        config.binaryMappingTip + "\n\n",
                        languageChoice,
                        2.0);
            }
            catch (InterruptedException e) {
                System.err.println("打字机效果被中断");
                Thread.currentThread().interrupt();
            }

            // The Major Cycle
            int interactionCount = 0;
            boolean firstInteraction = true;
            while (true) {
                String currentInputPrompt;
                if (firstInteraction) {
                    currentInputPrompt = config.initialInputPrompt;
                    firstInteraction = false;
                }
                else {
                    currentInputPrompt = config.subsequentInputPrompt;
                }
                String input;
                try {
                    input = readInputWithBufferedReader(
                            reader,
                            currentInputPrompt,
                            config);
                }
                catch (IOException e) {
                    System.err.println("读取输入时发生错误: " + e.getMessage());
                    continue;
                }
                if (input.equals("EXIT_COMMAND")) {
                    System.out.println(config.exitPrompt);
                    break;
                }
                if (input.trim().isEmpty()) {
                    continue;
                }
                EncodingMode detectedMode = detectEncodingFormat(input);
                if (detectedMode != null) {
                    if (InputLengthLimiter.isDecodeInputTooLong(input)) {
                        try {
                            TypeWriterSystem.typeWriter(
                                    "\n⚠️ " +
                                            InputLengthLimiter.getDecodeLimitMessage(
                                                    languageChoice),
                                    languageChoice);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }
                else {
                    if (InputLengthLimiter.isEncodeInputTooLong(
                            input,
                            languageChoice)) {
                        try {
                            TypeWriterSystem.typeWriter(
                                    "\n" +
                                            InputLengthLimiter.getEncodeLimitMessage(
                                                    languageChoice),
                                    languageChoice);
                        }
                                catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        try {
                            InputLengthLimiter.printAnalysisReport(
                                    input,
                                    languageChoice);
                        }
                                catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }
                if (detectedMode == null && interactionCount == 0) {
                    System.out.println("\n=== 调试信息 ===");
                    System.out.println("原始文本: \"" + input + "\"");
                }
                if (detectedMode != null) {
                    if (interactionCount == 0) {
                        String formatName = (detectedMode == EncodingMode.CHINESE)
                                ? "中文"
                                : "英文";
                        System.out.print(
                                config.detectionPrefix +
                                        formatName + "编码格式，正在解码...\n");
                    }
                    String decodedText = enhancedDecode(input, detectedMode);
                    if (decodedText.isEmpty()) {
                        if (interactionCount == 0) {
                            System.out.print(config.decodedOutputPrefix);
                            try {
                                TypeWriterSystem.typeWriter(
                                        "(解码失败)\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        else {
                            System.out.print(config.outputLabel);
                            try {
                                TypeWriterSystem.typeWriter(
                                        decodedText + "\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    else {
                        if (interactionCount == 0) {
                            System.out.print(config.decodedOutputPrefix);
                            try {
                                TypeWriterSystem.typeWriter(
                                        decodedText + "\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        else {
                            System.out.print(config.outputLabel);
                            try {
                                TypeWriterSystem.typeWriter(
                                        decodedText + "\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (decodedText.contains("~")) {
                            decodedText = decodedText.replace("~", "");
                            System.out.print(
                                    config.outputLabel + "（已清理~标记）: ");
                            try {
                                TypeWriterSystem.typeWriter(
                                        decodedText + "\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (decodedText.contains("  ")) {
                            decodedText = decodedText.replaceAll("\\s+", " ");
                            System.out.print(
                                    config.outputLabel + "（已规范化空格）: ");
                            try {
                                TypeWriterSystem.typeWriter(
                                        decodedText + "\n",
                                        languageChoice);
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
                else {
                    EncodingMode outputMode;
                    if (isPureChinese(input) && languageChoice.equals("1")) {
                        outputMode = EncodingMode.CHINESE;
                    }
                    else {
                        outputMode = EncodingMode.ENGLISH;
                    }

                    if (interactionCount == 0) {
                        String formatName = (outputMode == EncodingMode.CHINESE)
                                ? "中文"
                                : "英文";
                        System.out.print(
                                config.formatUsagePrefix + formatName + "编码格式\n");
                    }
                    String encoded = enhancedEncode(input, outputMode);
                    if (interactionCount == 0) {
                        System.out.print(config.encodedOutputPrefix);
                        try {
                            TypeWriterSystem.typeWriter(
                                    encoded + "\n",
                                    languageChoice);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    else {
                        System.out.print(config.outputLabel);
                        try {
                            TypeWriterSystem.typeWriter(
                                    encoded + "\n",
                                    languageChoice);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (interactionCount == 0) {
                        int sentenceCount = countSentences(input);
                        System.out.println(
                                "（共编码 " + sentenceCount + " 个句子）");
                    }
                }
                if (interactionCount == 0) {
                    System.out.print(config.continuePrompt);
                }
                else {
                    System.out.print("\n");
                }
                interactionCount++;
            }
            // The Major Cycle is over
        }
        catch (Exception e) {
            System.err.println("程序发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    System.err.println("关闭读取器时出错: " + e.getMessage());
                }
            }
        }
    }

    
    // An auxiliary method
    private static int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        int count = 0;
        int i = 0;
        int textLength = text.length();

        while (i < textLength) {
            String remainingText = text.substring(i);
            String punctuation =
                    SentenceTerminatingPunctuationDetector.extractTerminatingPunctuation(
                            remainingText);

            if (!punctuation.isEmpty() && remainingText.startsWith(punctuation)) {
                count++;
                i += punctuation.length();
            }
            else {
                i++;
            }
        }

        if (count == 0 && !text.trim().isEmpty()) {
            count = 1;
        }

        return count;
    }
}
