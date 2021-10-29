# terminal-io

A package automating many basic terminal interactions that allows students learning Java to focus on backend code rather than boilerplate display code while still allowing for flexible customization.

## Example

```java
import java.util.function.Function;
import terminal_io.TerminalIO;

public class TerminalIO_Example {
	public static void main(String[] args) {
        TerminalIO.setBorders("|");
        TerminalIO.setWidth(16);
        int i = Integer.parseInt(TerminalIO.getResponse("Hello there! Please enter an integer below:", TerminalIO.tryIntegerToString);
        TerminalIO.print(i);
    }
}
```



