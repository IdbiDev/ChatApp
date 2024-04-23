package me.idbi.chatapp.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.networking.Server;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.RoomJoinView;
import me.idbi.chatapp.view.viewmenus.RoomListView;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.nio.IntBuffer;

public class TerminalManager {
    @Getter private final Terminal terminal;
    @Getter @Setter
    private boolean canWrite = true;
    @Getter private KeyboardListener keyboardListener;

    public interface TerminalFormatter {}

    @Getter
    @AllArgsConstructor
    public static enum Color implements TerminalFormatter {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        MAGENTA("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),
        BLACK_BACKGROUND("\u001B[40m"),
        RED_BACKGROUND("\u001B[41m"),
        GREEN_BACKGROUND("\u001B[42m"),
        YELLOW_BACKGROUND("\u001B[43m"),
        BLUE_BACKGROUND("\u001B[44m"),
        MAGENTA_BACKGROUND("\u001B[45m"),
        CYAN_BACKGROUND("\u001B[46m"),
        WHITE_BACKGROUND("\u001B[47m");

        private final String code;

        @Override
        public String toString() {
            return this.code;
        }
    }
    @Getter
    @AllArgsConstructor
    public static enum Screen implements TerminalFormatter  {
        CLEAR("\u001B[2J"),
        CLEAR_LINE("\u001B[K"),
        SIZE("\u001B[8;%d;%d"); // rows, columns

        private final String code;
        public String format(Object... args) {
            return String.format(code, args);
        }

        @Override
        public String toString() {
            return this.code;
        }
    }
    @Getter
    @AllArgsConstructor
    public static enum Style implements TerminalFormatter  {
        RESET("\u001B[0m"),
        BOLD("\u001B[1m"),
        FAINT("\u001B[2m"),
        ITALIC("\u001B[3m"),
        UNDERLINE("\u001B[4m"),
        BLINK_SLOW("\u001B[5m"),
        BLINK_RAPID("\u001B[6m"),
        REVERSE_VIDEO("\u001B[7m"),
        HIDE_CURSOR("\u001B[?25l"),
        SHOW_CURSOR("\u001B[?25h"),
        CONCEAL("\u001B[8m");

        private final String code;

        @Override
        public String toString() {
            return this.code;
        }
    }
    @Getter
    @AllArgsConstructor
    public static enum Cursor implements TerminalFormatter  {
        UP("\u001B[%dA"),      // Move cursor up by n rows
        DOWN("\u001B[%dB"),    // Move cursor down by n rows
        FORWARD("\u001B[%dC"), // Move cursor forward by n columns
        BACKWARD("\u001B[%dD"),// Move cursor backward by n columns
        TO_POSITION("\u001B[%d;%dH"), // Move cursor to specified position (row, column)
        HOME("\u001B[H"), // Move cursor to specified position (row, column)
        SAVE_POSITION("\u001B[s"),     // Save cursor position
        RESTORE_POSITION("\u001B[u");  // Restore cursor position

        private final String code;
        public String format(Object... args) {
            return String.format(code, args);
        }
        @Override
        public String toString() {
            return this.code;
        }
    }
    public void moveCursor(int row, int column) {
        System.out.print(Cursor.TO_POSITION.format(row,column));
    }
    public void moveCursorUp(int n) {
        System.out.print(Cursor.UP.format(n));
    }
    public void moveCursorDown(int n) {
        System.out.print(Cursor.DOWN.format(n));
    }
    public void moveCursorLeft(int n) {
        System.out.print(Cursor.BACKWARD.format(n));
    }
    public void moveCursorRight(int n) {
        System.out.print(Cursor.FORWARD.format(n));
    }
    public void saveCursor() {
        System.out.print(Cursor.SAVE_POSITION);
    }
    public void restoreCursor() {
        System.out.print(Cursor.RESTORE_POSITION);
    }
    public void clear() {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }
    public void toBold() {
        System.out.print(Style.BOLD);
    }
    public void toUnderline() {
        System.out.print(Style.UNDERLINE);
    }
    public void toItalic() {
        System.out.print(Style.ITALIC);
    }
    public void resetStyle() {
        System.out.print(Style.RESET);
    }
    public void home() {
        System.out.print(Cursor.HOME);
    }
    public int getWidth() {
        return terminal.getWidth();
    }
    public int getHeight() {
        return terminal.getHeight();
    }
    public void showCursor() {
        System.out.print(Style.SHOW_CURSOR);
    }
    public void hideCursor() {
        System.out.print(Style.HIDE_CURSOR);
    }
    public void center(String text) {
        System.out.print(Screen.CLEAR_LINE);
        moveCursorRight(getWidth() / 2 - text.length() / 2);
        System.out.print(text);
    }

    public void center(String text, TerminalFormatter... formats) {
        System.out.print(Screen.CLEAR_LINE);
        moveCursorRight(getWidth() / 2 - text.length() / 2);

        for (TerminalFormatter format : formats) {
            System.out.print(format);
        }
        System.out.print(text);
        resetStyle();
    }
    public TerminalManager() throws IOException {
        terminal = TerminalBuilder.terminal();
        clear();
        System.out.print(Cursor.HOME);
        terminal.enterRawMode();
        this.keyboardListener = new KeyboardListener(this);
        Thread t = new Thread(this.keyboardListener);
        t.start();


    }
    public static class KeyboardListener implements Runnable {

        private final TerminalManager terminal;
        @Getter private String buffer;
        public KeyboardListener(TerminalManager terminal) {
            this.terminal = terminal;
            this.buffer = "";
        }

        @Override
        public void run() {
            NonBlockingReader nonBlockingReader = terminal.getTerminal().reader();
            while (true) {
                if(!terminal.isCanWrite())
                    continue;
                try {
                    int key = nonBlockingReader.read();
                    if(key == 27) {
                        if(nonBlockingReader.read() == 79) {
                            switch (nonBlockingReader.read()) {
                                case 65:
                                    //Fel nyíl
                                    Main.getTableManager().nextUp();
                                    break;
                                case 66:
                                    //Le nyíl
                                    Main.getTableManager().nextDown();
                                    break;
                                case 67:
                                    //jobbra nyíl
                                    //System.out.println("jobbb");
                                    break;
                                case 68:
                                    //balra nyíl
                                    //System.out.println("Bal");
                                    break;
                            }
                        }
                    }
                    switch (key) {
                        case 13: // Enter
                            if(Main.getTableManager().getCurrentTable() != null && Main.getViewManager().getCurrentView() != null) {
                                if(Main.getViewManager().getCurrentView() instanceof RoomListView) {
                                    String selectedRoomName = Main.getTableManager().getCurrentTable().getSelectedRow().getLine();
                                    if(Main.getRooms().containsKey(selectedRoomName)) {
                                        Room selectedRoom = Main.getRooms().get(selectedRoomName);

                                        if(!selectedRoom.hasPassword()) {
                                            Main.getClient().sendPacket(new RoomJoinPacket(selectedRoom.getName(), selectedRoom.getPassword()));
                                        } else {
                                            ((RoomJoinView) ViewType.ROOM_JOIN.getView()).setRoom(selectedRoom);
                                            Main.getViewManager().changeView(ViewType.ROOM_JOIN);
                                        }
                                    }
                                }
                            }
                            buffer = "";
                            break;
                        case 27: // escape
                            if (Main.getViewManager().getCurrentView() instanceof RoomJoinView) {
                                Main.getViewManager().changeView(ViewType.ROOM_LIST);
                            }
                        case 8: // Backspace
                            if(buffer.isEmpty()) continue;
                            buffer = buffer.substring(0, buffer.length() - 1); // utolsót szedje ki char he? na mé -2?XD ahogy megy xd
                            break;
                        default:
                            buffer += (char) key;
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}


