package terminal_io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 *
 * @author jlgingrich
 */
public final class TerminalIO {

    /**
     * Singleton IO class for creating refresh-able "screens" in the
     * command-line and easily asking a user for input via the command-line,
     * written by John L. Gingrich
     */
    public static final Scanner USER = new Scanner(System.in); // Scanner for keyboard inputs, can be publicly read but not redefined
    private static final List<String> CONTENTS = new ArrayList<>(); // Stores the list of lines in the buffer
    
    private static int width = 32; // The width of the printed screen
    private static String leftBorder = "";
    private static String rightBorder = "";

    private TerminalIO() {
        /**
         * Private, empty constructor defines this as a singleton
         */
    }

    public static <T> void print(T obj) {
        /**
         * Writes the object to the buffer, and renders the buffer all in one.
         */
        appendLine(obj);
        renderScreen();
    }

    public static void screenWidth(int w) {
        /**
         * Sets the width if the given width is valid and returns the current
         * width of the output screen
         */
        if (width > 0) {
            width = w;
        }
    }

    public static void setBorders(String border) {
        /**
         * Sets the screen borders to the provided non-empty string.
         */
        if (!border.isEmpty()) {
            leftBorder = border;
            rightBorder = border;
        }
    }

    public static void setBorders(String left, String right) {
        /**
         * Sets the screen borders to the provided non-empty strings.
         */
        if (!left.isEmpty()) {
            leftBorder = left;
        }
        if (!right.isEmpty()) {
            rightBorder = right;
        }
    }

    public static <T> void appendToLastLine(T obj) {
        /**
         * Appends the given object to the last line of the buffer
         */
        String currentLine = CONTENTS.get(CONTENTS.size() - 1);
        CONTENTS.set(CONTENTS.size() - 1, currentLine + obj.toString());
    }

    public static <T> void appendLine(T obj) {
        /**
         * Appends the given object to the buffer as a new line
         */
        CONTENTS.add(obj.toString());
    }

    public static void renderScreen() {
        /**
         * Clears the terminal, then writes the buffer into the terminal,
         * keeping the buffer available in case a refresh is needed. The line is
         * automatically terminated before the next word that would make the
         * line longer than the width or, if the word is longer than the width,
         * before the first character longer than the width.
         */
        clearScreen();
        List<String> printArr = new ArrayList<>(CONTENTS); // Stores the list of lines in the buffer
        for (int i = 0; i < printArr.size(); i++) {
            if (printArr.get(i).length() > width) {
                String[] s;
                s = printArr.get(i).split(" ");
                if (s[0].length() > width) {
                    String[] sCopy = new String[s.length + 1];
                    sCopy[0] = s[0].substring(0, width);
                    sCopy[1] = s[0].substring(width);
                    System.arraycopy(s, 1, sCopy, 2, s.length - 1);
                    s = sCopy;
                }
                StringBuilder newLine = new StringBuilder();
                StringBuilder nextLine = new StringBuilder();
                int j = 0;
                while (newLine.length() + s[j].length() <= width) {
                    newLine.append(s[j]);
                    if (newLine.length() + s[j + 1].length() <= width) {
                        newLine.append(" ");
                    }
                    j++;
                }
                while (j < s.length) {
                    nextLine.append(s[j]);
                    if (j + 1 < s.length) {
                        nextLine.append(" ");
                    }
                    j++;
                }
                printArr.set(i, newLine.toString());
                printArr.add(i + 1, nextLine.toString());
            }
            StringBuilder line = new StringBuilder(printArr.get(i));
            while (line.length() < width) {
                line.append(" ");
            }
            System.out.println(leftBorder + " " + line.toString() + " " + rightBorder);
        }
    }

    public static void clear() {
        /**
         * Clears the buffer.
         */
        CONTENTS.clear();
    }

    public static void clearScreen() {
        /**
         * Tries to clear the terminal, printing an error if the process fails
         *
         * From https://www.delftstack.com/howto/java/java-clear-console/
         */
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
            print(e);
        }
    }

    public static String getResponse(String msg, String targetCommand) {
        /**
         * Appends the given message to the buffer, then waits for the user to
         * successfully enter the desired string, asking again if the String
         * provided is invalid, after which it clears the buffer and returns the
         * provided command.
         */
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderScreen();
            command = USER.nextLine().trim();
        } while (!targetCommand.equals(command)); // Try again if the trimmed command isn't the wanted command
        TerminalIO.clear();
        return command;
    }

    public static String getResponse(String msg, String[] validCommands) {
        /**
         * Appends the given message to the buffer, then waits for the user to
         * successfully enter a String that is contained in the given array,
         * asking again if the String provided is invalid, after which it clears
         * the buffer and returns the provided command.
         */
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderScreen();
            command = USER.nextLine().trim();
        } while (!Arrays.asList(validCommands).contains(command));
        TerminalIO.clear();
        return command;
    }

    public static String getResponse(String msg, Collection<String> validCommands) {
        /**
         * Appends the given message to the buffer, then waits for the user to
         * successfully enter a String that is contained in the given collection
         * of Strings, asking again if the String provided is invalid, after
         * which it clears the buffer and returns the provided command.
         */
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderScreen();
            command = USER.nextLine().trim();
        } while (!validCommands.contains(command));
        TerminalIO.clear();
        return command;
    }

    public static String getResponse(String msg, Function<String, Boolean> validator) {
        /**
         * Appends the given message to the buffer, then waits for the user to
         * successfully enter a String for which the validator function returns
         * true, asking again if the String provided is invalid, after which it
         * clears the buffer and returns the provided command.
         */
        String command;
        TerminalIO.appendLine(msg);
        do {
            TerminalIO.renderScreen();
            command = USER.nextLine().trim();
        } while (!validator.apply(command));
        TerminalIO.clear();
        return command;
    }

    public static void getContinue() {
        /**
         * Asks the user to press 'Enter' to continue, asking again if user
         * types anything else, after which it clears the buffer and resumes
         * execution.
         */
        getResponse("Press 'enter' to continue", ""); // No sense returning an empty string
    }
    
    public static void getContinue(String msg) {
        /**
         * Asks the user to press 'Enter' to continue with a custom prompt,
         * asking again if user types anything else, after which it clears the
         * buffer and resumes execution.
         */
        getResponse(msg, ""); // No sense returning an empty string
    }
}
