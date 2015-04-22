package me.bayes.vertx.vest.handler.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A URI template parser that parses JAX-RS specific URI templates.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class UriTemplateParser {
    /* package */ static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Set<Character> RESERVED_REGEX_CHARACTERS = initReserved();

    private static Set<Character> initReserved() {
        char[] reserved = {
            '.', '^', '&', '!',
            '?', '-', ':', '<',
            '(', '[', '$', '=',
            ')', ']', ',', '>',
            '*', '+', '|'};

        Set<Character> s = new HashSet<Character>(reserved.length);
        for (char c : reserved) {
            s.add(c);
        }
        return s;
    }
    private static final Pattern TEMPLATE_VALUE_PATTERN = Pattern.compile("[^/]+?");

    private interface CharacterIterator {

        boolean hasNext();

        char next();

        char peek();

        int pos();
    }

    private static final class StringCharacterIterator implements CharacterIterator {

        private int pos;
        private String s;

        public StringCharacterIterator(final String s) {
            this.s = s;
            this.pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < s.length();
        }

        @Override
        public char next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return s.charAt(pos++);
        }

        @Override
        public char peek() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return s.charAt(pos);
        }

        @Override
        public int pos() {
            if (pos == 0) {
                throw new IllegalStateException("Iterator not used yet.");
            }

            return pos - 1;
        }
    }
    private final String template;
    private final StringBuffer regex = new StringBuffer();
    private final StringBuffer normalizedTemplate = new StringBuffer();
    private final StringBuffer literalCharactersBuffer = new StringBuffer();
    private final Pattern pattern;
    private final List<String> names = new ArrayList<String>();
    private final List<Integer> groupCounts = new ArrayList<Integer>();
    private final Map<String, Pattern> nameToPattern = new HashMap<String, Pattern>();
    private int numOfExplicitRegexes;
    private int literalCharacters;

    /**
     * Parse a template.
     *
     * @param template the template.
     * @throws IllegalArgumentException if the template is null, an empty string
     *         or does not conform to a JAX-RS URI template.
     */
    public UriTemplateParser(final String template) throws IllegalArgumentException {
        if (template == null || template.length() == 0) {
            throw new IllegalArgumentException();
        }

        this.template = template;
        parse(new StringCharacterIterator(template));
        try {
            pattern = Pattern.compile(regex.toString());
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template expression '"
                    + regex + "'",
                    ex);
        }
    }

    /**
     * Get the template.
     *
     * @return the template.
     */
    public final String getTemplate() {
        return template;
    }

    /**
     * Get the pattern.
     *
     * @return the pattern.
     */
    public final Pattern getPattern() {
        return pattern;
    }

    /**
     * Get the normalized template.
     * <p>
     * A normalized template is a template without any explicit regular
     * expressions.
     *
     * @return the normalized template.
     */
    public final String getNormalizedTemplate() {
        return normalizedTemplate.toString();
    }

    /**
     * Get the map of template names to patterns.
     *
     * @return the map of template names to patterns.
     */
    public final Map<String, Pattern> getNameToPattern() {
        return nameToPattern;
    }

    /**
     * Get the list of template names.
     *
     * @return the list of template names.
     */
    public final List<String> getNames() {
        return names;
    }

    /**
     * Get the capturing group counts for each template variable.
     *
     * @return the capturing group counts.
     */
    public final List<Integer> getGroupCounts() {
        return groupCounts;
    }

    /**
     * Get the group indexes to capturing groups.
     * <p>
     * Any nested capturing groups will be ignored and the
     * the group index will refer to the top-level capturing
     * groups associated with the templates variables.
     *
     * @return the group indexes to capturing groups.
     */
    public final int[] getGroupIndexes() {
        if (names.isEmpty()) {
            return EMPTY_INT_ARRAY;
        }

        int[] indexes = new int[names.size() + 1];
        indexes[0] = 1;
        for (int i = 1; i < indexes.length; i++) {
            indexes[i] = indexes[i - 1] + groupCounts.get(i - 1);
        }
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != i + 1) {
                return indexes;
            }
        }
        return EMPTY_INT_ARRAY;
    }

    /**
     * Get the number of explicit regular expressions.
     *
     * @return the number of explicit regular expressions.
     */
    public final int getNumberOfExplicitRegexes() {
        return numOfExplicitRegexes;
    }

    /**
     * Get the number of literal characters.
     *
     * @return the number of literal characters.
     */
    public final int getNumberOfLiteralCharacters() {
        return literalCharacters;
    }

    /**
     * Encode literal characters of a template.
     *
     * @param characters the literal characters
     * @return the encoded literal characters.
     */
    protected String encodeLiteralCharacters(final String characters) {
        return characters;
    }

    private void parse(final CharacterIterator ci) {
        try {
            while (ci.hasNext()) {
                char c = ci.next();
                if (c == '{') {
                    processLiteralCharacters();
                    parseName(ci);
                } else {
                    literalCharactersBuffer.append(c);
                }
            }
            processLiteralCharacters();
        } catch (NoSuchElementException ex) {
            throw new IllegalArgumentException(
                    "Invalid syntax for the template, \"" + template
                    + "\". Check if a path parameter is terminated with a '}'.",
                    ex);
        }
    }

    private void processLiteralCharacters() {
        if (literalCharactersBuffer.length() > 0) {
            literalCharacters += literalCharactersBuffer.length();

            String s = encodeLiteralCharacters(literalCharactersBuffer.toString());

            normalizedTemplate.append(s);

            // Escape if reserved regex character
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (RESERVED_REGEX_CHARACTERS.contains(c)) {
                    regex.append("\\");
                }
                regex.append(c);
            }

            literalCharactersBuffer.setLength(0);
        }
    }

    private void parseName(final CharacterIterator ci) {
        char c = consumeWhiteSpace(ci);

        StringBuilder nameBuffer = new StringBuilder();
        if (Character.isLetterOrDigit(c) || c == '_') {
            // Template name character
            nameBuffer.append(c);
        } else {
            throw new IllegalArgumentException("Illegal character '" + c
                    + "' at position " + ci.pos() + " is not as the start of a name");
        }

        String nameRegexString = "";
        while (true) {
            c = ci.next();
            // "\\{(\\w[-\\w\\.]*)
            if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.') {
                // Template name character
                nameBuffer.append(c);
            } else if (c == ':') {
                nameRegexString = parseRegex(ci);
                break;
            } else if (c == '}') {
                break;
            } else if (c == ' ') {
                c = consumeWhiteSpace(ci);

                if (c == ':') {
                    nameRegexString = parseRegex(ci);
                    break;
                } else if (c == '}') {
                    break;
                } else {
                    // Error
                    throw new IllegalArgumentException("Illegal character '" + c
                            + "' at position " + ci.pos() + " is not allowed after a name");
                }
            } else {
                throw new IllegalArgumentException("Illegal character '" + c
                        + "' at position " + ci.pos() + " is not allowed as part of a name");
            }
        }
        String name = nameBuffer.toString();
        names.add(name);

        try {
            if (nameRegexString.length() > 0) {
                numOfExplicitRegexes++;
            }
            Pattern namePattern = (nameRegexString.length() == 0)
                    ? TEMPLATE_VALUE_PATTERN : Pattern.compile(nameRegexString);
            if (nameToPattern.containsKey(name)) {
                if (!nameToPattern.get(name).equals(namePattern)) {
                    throw new IllegalArgumentException("The name '" + name
                            + "' is declared "
                            + "more than once with different regular expressions");
                }
            } else {
                nameToPattern.put(name, namePattern);
            }

            // Determine group count of pattern
            Matcher m = namePattern.matcher("");
            int g = m.groupCount();
            groupCounts.add(g + 1);

            regex.append('(').
                    append(namePattern).
                    append(')');
            normalizedTemplate.append('{').
                    append(name).
                    append('}');
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the expression '" + nameRegexString
                    + "' associated with the name '" + name + "'",
                    ex);
        }
    }

    private String parseRegex(final CharacterIterator ci) {
        StringBuilder regexBuffer = new StringBuilder();

        int braceCount = 1;
        while (true) {
            char c = ci.next();
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    break;
                }
            }
            regexBuffer.append(c);
        }

        return regexBuffer.toString().trim();
    }

    private char consumeWhiteSpace(final CharacterIterator ci) {
        char c;
        do {
            c = ci.next();
        } while (Character.isWhitespace(c));

        return c;
    }
}
