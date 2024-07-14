package me.idbi.chatapp.view.viewmenus;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.table.Column;
import me.idbi.chatapp.table.Table;
import me.idbi.chatapp.table.Row;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class RoomListView implements IView, IView.Tableable {
    @Override
    public ViewType getType() {
        return ViewType.ROOM_LIST;
    }
    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public boolean hasThread() {
        return false;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public long getUpdateInterval() {
        return -1;
    }

    @Override
    public void start() {
        Table table = new Table();
        Column column = new Column();
        column.addRow(new Row("Töltés...", false, false, Row.Aligment.CENTER));
        table.addColumn(column);
        Main.getClientData().getTableManager().setTable(table);
    }

    @Override
    public void update() {
        List<Room> rooms = getPageRooms(); // Main.getClientData().getRooms().values().stream().sorted(Comparator.comparing(Room::getName)).toList();
        if (!rooms.isEmpty()) {
            Column name = new Column();
            name.addRow(new Row("Név", false, false, Row.Aligment.CENTER));

            Column infos = new Column(20);
            infos.addRow(new Row("Információk", false, false, Row.Aligment.CENTER));

            Column psw = new Column(20);
            psw.addRow(new Row("Jelszóvédett", false, false, Row.Aligment.CENTER));

            boolean firstSelected = true;
            for (Room room : rooms) {
                name.addRow(new Row(room.getName().strip(), true, firstSelected));
                Main.debug("'" + room.getName() + "'");
                firstSelected = false;
            }

            for (Room room : rooms) {
                String memberInfos = room.getMembers().size() + (room.getMaxMembers() == -1 ? "" : "/" + room.getMaxMembers());
                infos.addRow(new Row(memberInfos, false, false, Row.Aligment.CENTER));
            }

            for (Room room : rooms) {
                psw.addRow(new Row(room.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
            }

            Table table = new Table();
            table.addColumn(name, infos, psw);

            int length = -1;
            for (String value : table.getTable().values()) {
                if (length < value.length()) {
                    length = value.length();
                }
            }

            name.addRow(new Row("Szoba készítés", true, false, Row.Aligment.LEFT));
            infos.addRow(new Row(" ", false, false, Row.Aligment.CENTER));
            psw.addRow(new Row(" ", false, false, Row.Aligment.CENTER));

            Table header = new Table();
            Column column = new Column(el -> el.setWidth(Main.getClientData().getTerminalManager().getWidth() - 4));
            column.addRow(new Row("Szobák | " + Main.getClientData().getRoomListState(), false, false, Row.Aligment.CENTER));
            header.addColumn(column);
            Main.getClientData().getTableManager().setHeader(header);
            Main.getClientData().getTableManager().setTable(table);
        }
    }

    private List<Room> getPageRooms() {
        List<Room> rooms = Main.getClientData().getRooms().values().stream().sorted(Comparator.comparing(Room::getName)).toList();

        int state = Main.getClientData().getScrollState();
        int amountOnPage = Main.getClientData().getTerminalManager().getTerminal().getHeight() - 5;

        int idx1 = rooms.size() - state * amountOnPage;
        int idx2 = rooms.size() - amountOnPage * amountOnPage - amountOnPage;

        idx2 = Math.max(0, idx2);
        idx1 = Math.min(Math.max(idx1, amountOnPage), rooms.size());

        return rooms.subList(idx2, idx1);
    }
}
