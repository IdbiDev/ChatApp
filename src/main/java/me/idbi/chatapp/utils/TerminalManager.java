package me.idbi.chatapp.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.events.clients.ClientTerminalResizeEvent;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.packets.client.DebugMessagePacket;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.packets.client.SendMessageToServerPacket;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.RoomChatView;
import me.idbi.chatapp.view.viewmenus.RoomCreateConfirmView;
import me.idbi.chatapp.view.viewmenus.RoomJoinView;
import me.idbi.chatapp.view.viewmenus.RoomListView;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalManager {
    @Getter
    private final Terminal terminal;
    @Getter
    @Setter
    private boolean canWrite = true;
    @Getter
    private final KeyboardListener keyboardListener;
    @Getter
    private final Thread keyboardThread;
    @Getter
    private final boolean isWindows;
    @Getter
    private final TerminalResizeListener terminalResizeListener;
    @Getter
    private final Thread terminalResizeThread;

    public interface TerminalFormatter {
    }

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
    public static enum Screen implements TerminalFormatter {
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
    public static enum Style implements TerminalFormatter {
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
    public static enum Cursor implements TerminalFormatter {
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
        System.out.print(Cursor.TO_POSITION.format(row, column));
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
        try {
            if (isWindows) {

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (final Exception ignored) {
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

    /**
     * @param color background color
     *              This function will clear the screen!
     */
    public void setBackgroundColor(Color color) {
        clear();
        home();
        System.out.print(color);
        for (int i = 0; i < getHeight(); i++) {
            for (int i1 = 0; i1 < getWidth(); i1++) {
                System.out.print(" ");
            }
            System.out.println();
        }
        home();
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
    public void clearLine(){
        System.out.print("\r" + Screen.CLEAR_LINE);
    }

    public TerminalManager() throws IOException {
        terminal = TerminalBuilder.terminal();
        isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        System.out.println(System.getProperty("os.name") + isWindows);
        clear();
        System.out.print(Cursor.HOME);
          terminal.enterRawMode();
        this.keyboardListener = new KeyboardListener(this);
        keyboardThread = new Thread(this.keyboardListener);
        keyboardThread.start();

        this.terminalResizeListener = new TerminalResizeListener(this);
        terminalResizeThread = new Thread(this.terminalResizeListener);
        terminalResizeThread.start();

    }

    public static class TerminalResizeListener implements Runnable {
        private final TerminalManager terminal;

        TerminalResizeListener(TerminalManager terminal) {
            this.terminal = terminal;
        }

        @Override
        public void run() {
            int lastWidth = terminal.getWidth();
            int lastHeight = terminal.getHeight();
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Main.getClient().sendPacket(new DebugMessagePacket(e.getMessage()));
                    break;
                }
                //Main.debug("View terminal size");
                if (lastWidth != terminal.getWidth() || lastHeight != terminal.getHeight()) {
                    //Main.debug("Terminal size changed");
                    new ClientTerminalResizeEvent(lastWidth, lastHeight, terminal.getWidth(),terminal.getHeight()).callEvent();
                    lastWidth = terminal.getWidth();
                    lastHeight = terminal.getHeight();
                }
            }
        }
    }

    public static class KeyboardListener implements Runnable {

        @Getter
        public static enum KeyboardButtons {
            OTHER(),
            BACKSPACE(8),
            ENTER(13),
            ESCAPE(27),
            PAGE_UP(27, 91, 53, 126),
            PAGE_DOWN(27, 91, 54, 126),
            ARROW_LEFT(27, 79, 68),
            ARROW_RIGHT(27, 79, 67),
            ARROW_UP(27, 79, 65),
            ARROW_DOWN(27, 79, 66);

            private final List<Integer> keys;

            KeyboardButtons(Integer... keys) {
                this.keys = Arrays.asList(keys);
            }
        }

        private final TerminalManager terminal;
        /**
         * Only for the CHATVIEW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */
        @Setter @Getter private String chatBuffer;
        @Setter @Getter private String inputBuffer;
        @Setter @Getter private String inputPrompt;
        @Getter @Setter private boolean inputMode;
        @Getter @Setter private boolean prepareExit;

        public KeyboardListener(TerminalManager terminal) {
            this.terminal = terminal;
            this.chatBuffer = "";
            this.inputBuffer = "";
            this.inputPrompt = "";
            this.inputMode = false;

        }

        @Override
        public void run() {
            NonBlockingReader nonBlockingReader = terminal.getTerminal().reader();
            while (true) {
                try {
                    Thread.sleep(0,750);
                } catch (InterruptedException e) {
                }
                try {
                    List<Integer> keys = new ArrayList<>();
                    while (nonBlockingReader.available() > 0) {
                        keys.add(nonBlockingReader.read());
                    }

                    if (keys.isEmpty()) continue;

                    String lastButton = "";
                    for (int i = 0; i < keys.size(); i++) {
                        lastButton += ((char) (int) keys.get(i)) + "";
                    }

                    lastButton = lastButton.strip();
                    KeyboardButtons button = KeyboardButtons.OTHER;
                    for (KeyboardButtons value : KeyboardButtons.values()) {
                        if (!keys.equals(value.getKeys())) continue;

                        button = value;
                    }

                    switch (button) {
                        case ESCAPE: {
                            if(this.inputMode) {
                                this.inputMode = false;https://www.youtube.com/watch?v=ytTw4KZnop8
                                this.prepareExit = true;
                            } else if (Main.getClientData().getViewManager().getView() instanceof RoomJoinView) {
                                this.chatBuffer = "";
                                this.terminal.canWrite = false;
                                Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);
                                Main.getClientData().getViewManager().refresh();
                            }
                            break;
                        }
                        case BACKSPACE: {
                            if(this.inputMode) {
                                if (this.inputBuffer.isEmpty()) break;
                                this.inputBuffer = this.inputBuffer.substring(0, this.inputBuffer.length() - 1);
                                Main.getClientData().getTerminalManager().clearLine();
                                //Main.getClientData().refreshBuffer();
                                //System.out.print(" ");
                                //Main.getClientData().getTerminalManager().moveCursorLeft(1);
                            } else {
                                if (!this.terminal.isCanWrite()) break;
                                if (this.chatBuffer.isEmpty()) break;

                                this.chatBuffer = this.chatBuffer.substring(0, this.chatBuffer.length() - 1);
                                Main.getClientData().getTerminalManager().clearLine();
                                Main.getClientData().refreshBuffer();
                                //System.out.print(" ");
                                //Main.getClientData().getTerminalManager().moveCursorLeft(1);
                            }
                            break;
                        }
                        case ENTER: {
                            if(this.inputMode) {
                                this.inputMode = false;
                            } else {
                                if (Main.getClientData().getTableManager().getCurrentTable() != null && Main.getClientData().getViewManager().getView() != null) {
                                    if (Main.getClientData().getViewManager().getView() instanceof RoomListView) {
                                        if (Main.getClientData().getTableManager().getCurrentTable().getSelectedRow() == null) {
                                            break;
                                        }
                                        String selectedRoomName = Main.getClientData().getTableManager().getCurrentTable().getSelectedRow().getLine();
                                        if (selectedRoomName.equals("Szoba készítés")) {
                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_CREATE);
                                            break;
                                        }
                                        Room selectedRoom = Main.getClientData().getRooms()
                                                .values()
                                                .stream()
                                                .filter(rm -> rm.getName().equalsIgnoreCase(selectedRoomName))
                                                .findAny()
                                                .orElse(null);
                                        if (selectedRoom == null) break;
                                        if (!selectedRoom.hasPassword() || Main.getClientData().getClientMember().hasPassword(selectedRoom)) {
                                            Main.getClient().sendPacket(new RoomJoinPacket(selectedRoom.getUniqueId(), selectedRoom.getPassword()));
                                        } else {
                                            ((RoomJoinView) ViewType.ROOM_JOIN.getView()).setRoom(selectedRoom);
                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_JOIN);
                                        }
                                    }
                                }

                                if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
                                    if (this.chatBuffer.isEmpty() || this.chatBuffer.isBlank()) break;
                                    Main.getClient().sendPacket(new SendMessageToServerPacket(new ClientMessage(Main.getClient().getName(), Main.getClientData().getCurrentRoom(), this.chatBuffer.strip())));
                                    this.chatBuffer = "";
                                    Main.getClientData().getTerminalManager().clearLine();
                                    Main.getClientData().refreshBuffer();
                                }

                                if (Main.getClientData().getViewManager().getView() instanceof RoomCreateConfirmView view) {
                                    String row = Main.getClientData().getTableManager().getCurrentTable().getSelectedRow().getLine();
                                    switch (row) {
                                        case "Megerősítés": {
                                            Main.getClient().sendPacket(view.getPacket());
                                            Notification.Notifications.ROOM_CREATED.send();
                                            break;
                                        }
                                        case "Szerkesztés": {
                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_CREATE);
                                            break;
                                        }
                                        case "Mégse": {
                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);
                                            Main.getClientData().getViewManager().refresh();
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case PAGE_UP: {
                            if (Main.getClientData().getViewManager().getView() instanceof RoomChatView view) {
                                List<IMessage> clientMessages = Main.getClientData().getCurrentRoom().getMessages()
                                        .stream()
                                        .filter(msg -> ((msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem()))
                                        .toList();
                                Main.getClientData().addScrollState(clientMessages, RoomChatView.getMessagesPerScroll() * 3);
                                Main.getClientData().refreshBuffer();
                            }
                            break;
                        }
                        case PAGE_DOWN: {
                            if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
                                Main.getClientData().removeScrollState(RoomChatView.getMessagesPerScroll() * 3);
                                Main.getClientData().refreshBuffer();
                            }
                            break;
                        }
                        case ARROW_UP: {
                            if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable) {
                                Main.getClientData().getTableManager().nextUp();
                            } else if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {

                            }
                            break;
                        }
                        case ARROW_DOWN: {
                            if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable) {
                                Main.getClientData().getTableManager().nextDown();
                            } else if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {

                            }
                            break;
                        }
                        case ARROW_LEFT: {
                            if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable t) {
                                Main.getClientData().removeRoomListState();
                                Main.getClientData().getViewManager().refresh();
                            }
                            break;
                        }
                        case ARROW_RIGHT: {
                            if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable t) {
                                Main.getClientData().addRoomListState();
                                Main.getClientData().getViewManager().refresh();
                            }
                            break;
                        }
                        case OTHER: {
                            if(this.inputMode) {
                                if(this.inputPrompt.length() + this.inputBuffer.length() + lastButton.length() > this.terminal.getWidth() - 1) break;
                                this.inputBuffer += lastButton;
                            } else {
                                if (!terminal.isCanWrite()) {
                                    break;
                                }
                                if ((Main.getClient().getName() + " > ").length() + chatBuffer.length() + lastButton.length() + 17 > terminal.getWidth() - 1) {
                                    break;
                                }
                                chatBuffer += lastButton;
                                Main.getClientData().refreshBuffer();
                            }
                            break;
                        }
                    }

                    // Main.debug("ACTION: " + button.name());

//                    int key = nonBlockingReader.read();
//                    if (key == 27 && nonBlockingReader.ready()) {
//                        int next_byte = nonBlockingReader.read();
//                        if (next_byte == 79 && nonBlockingReader.ready()) {
//                            switch (nonBlockingReader.read()) {
//                                case 65:
//                                    //Fel nyíl
//                                    if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable)
//                                        Main.getClientData().getTableManager().nextUp();
//                                    else if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
//
//                                    }
//                                    break;
//                                case 66:
//                                    //Le nyíl
//                                    if (Main.getClientData().getViewManager().getView() instanceof IView.Tableable)
//                                        Main.getClientData().getTableManager().nextDown();
//                                    else if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
//
//                                    }
//                                    break;
//                                case 67:
//                                    //jobbra nyíl
//                                    //System.out.println("jobbb");
//                                    break;
//                                case 68:
//                                    //balra nyíl
//                                    //System.out.println("Bal");
//                                    break;
//                            }
//                        } else if (next_byte == 91 && nonBlockingReader.ready()) {
//                            switch (nonBlockingReader.read()) { // 54 - 53
//                                case 54:
//                                    if (nonBlockingReader.read() == 126) { // PAGE DOWN
//                                        if(Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
//                                            Main.getClientData().removeScrollState(RoomChatView.getMessagesPerScroll() * 3);
//                                            Main.getClientData().refreshBuffer();
//                                        }
//                                        break;
//                                    }
//                                    break;
//                                case 53:
//                                    if (nonBlockingReader.read() == 126) { // PAGE UP
//                                        if(Main.getClientData().getViewManager().getView() instanceof RoomChatView view) {
//                                            List<IMessage> clientMessages = Main.getClientData().getCurrentRoom().getMessages()
//                                                    .stream()
//                                                    .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
//                                                    .toList();
//                                            Main.getClientData().addScrollState(clientMessages, RoomChatView.getMessagesPerScroll() * 3);
//                                            Main.getClientData().refreshBuffer();
//                                        }
//                                        break;
//                                    }
//                                    break;
//                            }
//                        }
//                    }
//                    if(this.inputMode) {
//                        switch (key) {
//                            case 13: // Enter
//                                this.inputMode = false;
//                                break;
//                            case 27: // escape
//                                this.inputMode = false;
//                                this.prepareExit = true;
//                                break;
//                            case 8: // Backspace
//                                if (this.inputBuffer.isEmpty()) continue;
//                                this.inputBuffer = this.inputBuffer.substring(0, this.inputBuffer.length() - 1);
//                                Main.getClientData().getTerminalManager().clearLine();
//                                //Main.getClientData().refreshBuffer();
//                                //System.out.print(" ");
//                                //Main.getClientData().getTerminalManager().moveCursorLeft(1);
//
//                                break;
//                            default:
//                                if(this.inputPrompt.length() + this.inputBuffer.length() > this.terminal.getWidth() - 1) {
//                                    continue;
//                                }
//                                this.inputBuffer += (char) key;
//                                break;
//                        }
//                    } else {
//                        switch (key) {
//                            case 13: // Enter
//                                if (Main.getClientData().getTableManager().getCurrentTable() != null && Main.getClientData().getViewManager().getView() != null) {
//                                    if (Main.getClientData().getViewManager().getView() instanceof RoomListView) {
//                                        if (Main.getClientData().getTableManager().getCurrentTable().getSelectedRow() == null) {
//                                            break;
//                                        }
//                                        String selectedRoomName = Main.getClientData().getTableManager().getCurrentTable().getSelectedRow().getLine();
//                                        if (selectedRoomName.equals("Szoba készítés")) {
//                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_CREATE);
//                                            break;
//                                        }
//                                        Room selectedRoom = Main.getClientData().getRooms()
//                                                .values()
//                                                .stream()
//                                                .filter(rm -> rm.getName().equalsIgnoreCase(selectedRoomName))
//                                                .findAny()
//                                                .orElse(null);
//                                        if (selectedRoom == null) break;
//                                        if (!selectedRoom.hasPassword()) {
//                                            Main.getClient().sendPacket(new RoomJoinPacket(selectedRoom.getUniqueId(), selectedRoom.getPassword()));
//                                        } else {
//                                            ((RoomJoinView) ViewType.ROOM_JOIN.getView()).setRoom(selectedRoom);
//                                            Main.getClientData().getViewManager().setView(ViewType.ROOM_JOIN);
//                                        }
//                                    }
//                                }
//                                //Main.debug(Main.getClient().getName() + " > " + buffer);
//                                if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
//                                    if (this.chatBuffer.isEmpty() || this.chatBuffer.isBlank()) continue;
//                                    Main.getClient().sendPacket(new SendMessageToServerPacket(new ClientMessage(Main.getClient().getName(), Main.getClientData().getCurrentRoom(), chatBuffer.strip())));
//                                    chatBuffer = "";
//                                    Main.getClientData().getTerminalManager().clearLine();
//                                    Main.getClientData().refreshBuffer();
//                                }
//                                break;
//                            case 27: // escap
//                                if (Main.getClientData().getViewManager().getView() instanceof RoomJoinView) {
//                                    chatBuffer = "";
//                                    this.terminal.canWrite = false;
//                                    Main.getClient().sendPacket(new RequestRefreshPacket());
//                                    Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);
//                                }
//                                break;
//                            case 8: // Backspace
//                                if (!terminal.isCanWrite()) {
//                                    continue;
//                                }
//                                if (chatBuffer.isEmpty()) continue;
//                                chatBuffer = chatBuffer.substring(0, chatBuffer.length() - 1);
//                                Main.getClientData().getTerminalManager().clearLine();
//                                Main.getClientData().refreshBuffer();
//                                //System.out.print(" ");
//                                //Main.getClientData().getTerminalManager().moveCursorLeft(1);
//
//                                break;
//                            default:
//                                if (!terminal.isCanWrite()) {
//                                    continue;
//                                }
//                                if ((Main.getClient().getName() + " > ").length() + chatBuffer.length() > terminal.getWidth() - 1) {
//                                    continue;
//                                }
//                                chatBuffer += (char) key;
//                                Main.getClientData().refreshBuffer();
//                                //Main.debug(String.valueOf((char) key));
//                                break;
//                        }
//                    }
                } catch (IOException e) {
                    Main.debug(e.getMessage());
                    //throw new RuntimeException(e);
                }
            }
            //System.out.println("KILLPETT TA WHILE LLOOPBÓl");
        }
    }
}


