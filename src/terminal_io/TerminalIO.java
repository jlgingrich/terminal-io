package terminal_io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.function.Function;

/**
 * A singleton class that automates many basic terminal interactions.
 *
 * @author jlgingrich
 */
public final class TerminalIO {

    /**
     * The Scanner used by TerminalIO to get user inputs.
     */
    public static final Scanner USER = new Scanner(System.in);

    private static final StringBuilder CONTENTS = new StringBuilder();
    private static final char WHITESPACE_CHAR = ' ';

    private static int width = 32;
    private static String leftBorder = "";
    private static String rightBorder = "";

    /**
     * Private constructor defines this as a singleton class.
     */
    private TerminalIO() {
    }

    /**
     * Writes the given object to the buffer, then renders the buffer.
     *
     * @param <T> The type of the object to append.
     * @param obj The object to append.
     */
    public static <T> void print(T obj) {
        appendLine(obj);
        renderDisplay();
    }

    /**
     * Sets the width if the given width is valid and returns the current width
     * of the output display
     *
     * @param w The new width of the terminal display.
     */
    public static void setWidth(int w) {
        /**
         */
        if (width > 0) {
            width = w;
        }
    }

    /**
     * Sets the terminal display borders to the provided string.
     *
     * @param border The string to be used as the left and right borders.
     */
    public static void setBorders(String border) {
        if (!border.isEmpty()) {
            leftBorder = border;
            rightBorder = border;
        }
    }

    /**
     * Sets the terminal display borders to the provided strings.
     *
     * @param left The string to be used as the left border.
     * @param right The string to be used as the right border.
     */
    public static void setBorders(String left, String right) {
        if (!left.isEmpty()) {
            leftBorder = left;
        }
        if (!right.isEmpty()) {
            rightBorder = right;
        }
    }

    /**
     * Appends the object as a string to the current contents.
     *
     * @param <T> The type of the object to append.
     * @param obj The object to append.
     */
    public static <T> void append(T obj) {
        CONTENTS.append(obj.toString());
    }

    /**
     * Appends the object as a string to the current contents, then appends a
     * newline character.
     *
     * @param <T> The type of the object to append.
     * @param obj The object to append.
     */
    public static <T> void appendLine(T obj) {
        CONTENTS.append(obj.toString()).append("\n");
    }

    /**
     * Appends a newline character to the current contents.
     */
    public static void appendNewLine() {
        CONTENTS.append("\n");
    }

    /**
     * Attempt to flush the terminal display, then writes the current contents
     * to the terminal display, wrapping the characters as necessary and
     * appending the border strings to the start and end of each line.
     */
    public static void renderDisplay() {
        clearDisplay();
        StringBuilder currentLine = new StringBuilder();
        for (int i = 0; i < CONTENTS.length(); i++) {
            String c = String.valueOf(CONTENTS.charAt(i));
            if (c.equals("\n")) { // If it reads a newline character, print the completed line and reset
                while (currentLine.length() < width) {
                    currentLine.append(WHITESPACE_CHAR);
                }
                System.out.println(leftBorder + " " + currentLine + " " + rightBorder);
                currentLine.delete(0, currentLine.length());
            } else { // if it reads a normal character
                if (currentLine.length() + 1 > width) { // If the next character won't fit on the same line
                    while (currentLine.length() < width) {
                        currentLine.append(WHITESPACE_CHAR);
                    }
                    System.out.println(leftBorder + " " + currentLine + " " + rightBorder);
                    currentLine.delete(0, currentLine.length());
                }
                currentLine.append(c);
            }
        }
        if (!currentLine.isEmpty()) {
            while (currentLine.length() < width) {
                currentLine.append(WHITESPACE_CHAR);
            }
            System.out.println(leftBorder + " " + currentLine + " " + rightBorder);
        }
    }

    /**
     * Clears the current contents.
     */
    public static void clear() {
        CONTENTS.delete(0, CONTENTS.length());
    }

    /**
     * Attempts to flush the terminal display. Should work for most operating
     * systems, but if it fails, it will print out the resulting exception.
     *
     * @see
     * <a href="https://www.delftstack.com/howto/java/java-clear-console/">Source</a>
     */
    public static void clearDisplay() {
        try {
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.out.print(e);
        }
    }

    /**
     * Appends the given prompt to the current contents, then refreshes the
     * terminal display. This will accept any string on the first attempt.
     *
     * @param msg The message displayed to the user.
     * @return A string containing the user's input.
     */
    public static String getResponse(String msg) {
        String command;
        TerminalIO.appendLine(msg);
        TerminalIO.renderDisplay();
        command = USER.nextLine().trim();
        TerminalIO.clear();
        return command;
    }
/**
     * Appends the given prompt to the current contents, then refreshes the
     * terminal display until the user enters the desired string.
     *
     * @param msg The message displayed to the user.
     * @param desiredCommand The valid response string.
     * @return A string containing the user's last input, which should always be
     * equal to the valid response string.
     */
    public static String getResponse(String msg, String desiredCommand) {
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderDisplay();
            command = USER.nextLine().trim();
        } while (!desiredCommand.equals(command));
        TerminalIO.clear();
        return command;
    }

    /**
     * Appends the given prompt to the current contents, then refreshes the
     * terminal display until the user enters a string contained in the given
     * string array.
     *
     * @param msg The message displayed to the user.
     * @param validCommands An array of all valid response strings.
     * @return A string containing the user's last input.
     */
    public static String getResponse(String msg, String[] validCommands) {
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderDisplay();
            command = USER.nextLine().trim();
        } while (!Arrays.asList(validCommands).contains(command));
        TerminalIO.clear();
        return command;
    }

    /**
     * Appends the given prompt to the current contents, then refreshes the
     * terminal display until the user enters a string contained in the given
     * collection.
     *
     * @param msg The message displayed to the user.
     * @param validCommands A collection of all valid response strings.
     * @return A string containing the user's last input.
     */
    public static String getResponse(String msg, Collection<String> validCommands) {
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderDisplay();
            command = USER.nextLine().trim();
        } while (!validCommands.contains(command));
        TerminalIO.clear();
        return command;
    }

    /**
     * Appends the given prompt to the current contents, then refreshes the
     * terminal display until the user enters a string for which the validator
     * returns true.
     *
     * @param msg The message displayed to the user.
     * @param validator A function that tests user inputs to determine a valid
     * response string.
     * @return A string containing the user's last input.
     */
    public static String getResponse(String msg, Function<String, Boolean> validator) {
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderDisplay();
            command = USER.nextLine().trim();
        } while (!validator.apply(command));
        TerminalIO.clear();
        return command;
    }

    /**
     * Instructs the user to press the 'enter' key, then refreshes the terminal
     * display until the user enters an empty string.
     */
    public static void getContinue() {
        getResponse("Press 'enter' to continue", "");
    }

    /**
     * Presents the user with the given prompt, then refreshes the terminal
     * display until the user enters an empty string.
     *
     * @param msg The message displayed to the user.
     */
    public static void getContinue(String msg) {
        getResponse(msg, "");
    }

    /**
     * A single-argument function that tests if the input can be cleanly
     * converted to an integer. Can be used as a validator.
     *
     * @see #getResponse(java.lang.String, java.util.function.Function)
     */
    public static final Function<String, Boolean> tryStringToInteger = s -> {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    };

    /**
     * A single-argument function that tests if the input can be cleanly
     * converted to a double. Can be used as a validator.
     *
     * @see #getResponse(java.lang.String, java.util.function.Function)
     */
    public static final Function<String, Boolean> tryStringToDouble = s -> {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    };

    /**
     * A single-argument function that tests if the input can be cleanly
     * converted to a long. Can be used as a validator.
     *
     * @see #getResponse(java.lang.String, java.util.function.Function)
     */
    public static final Function<String, Boolean> tryStringToLong = s -> {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    };

    /**
     * A single-argument function that tests if the input can be cleanly
     * converted to a byte. Can be used as a validator.
     *
     * @see #getResponse(java.lang.String, java.util.function.Function)
     */
    public static final Function<String, Boolean> tryStringToByte = s -> {
        try {
            Byte.parseByte(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    };

    /**
     * A single-argument function that tests if the input can be cleanly
     * converted to a char. Can be used as a validator.
     *
     * @see #getResponse(java.lang.String, java.util.function.Function)
     */
    public static final Function<String, Boolean> tryStringToCharacter = s -> {
        return s.length() == 1;
    };
}
