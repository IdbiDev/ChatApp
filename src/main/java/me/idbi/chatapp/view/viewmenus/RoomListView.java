package me.idbi.chatapp.view.viewmenus;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.table.Column;
import me.idbi.chatapp.table.Table;
import me.idbi.chatapp.table.rows.Row;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RoomListView implements IView {

    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public void show() {
        List<Room> rooms = Main.getRooms().values().stream().toList();
        if(!rooms.isEmpty()) {
            Column name = new Column();
            name.addRow(new Row("Név", false, false, Row.Aligment.CENTER));

            Column infos = new Column(20);
            infos.addRow(new Row("Információk", false, false, Row.Aligment.CENTER));

            Column psw = new Column(20);
            psw.addRow(new Row("Jelszóvédett", false, false, Row.Aligment.CENTER));

            boolean firstSelected = true;
            for (Room room : rooms) {
                name.addRow(new Row(room.getName(), true, firstSelected));
                firstSelected = false;
            }

            for (Room room : rooms) {
                infos.addRow(new Row(room.getMembers().size() + "/" + room.getMaxMembers(), false, false, Row.Aligment.CENTER));
            }

            for (Room room : rooms) {
                psw.addRow(new Row(room.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
            }


            Table table = new Table();
            table.addColumn(name, infos, psw);

            int length = -1;
            for (String value : table.getTable().values()) {
                if(length < value.length()) {
                    length = value.length();
                }
            }

            Table header = new Table();
            Column column = new Column(Main.getTerminalManager().getWidth() - 4);
            column.addRow(new Row("Szobák", false, false, Row.Aligment.CENTER));
            header.addColumn(column);
            Main.getTableManager().setHeader(header);
            Main.getTableManager().setTable(table);
            return;
        }

        Table table = new Table();
        Column column = new Column();
        column.addRow(new Row("Töltés...", false, false, Row.Aligment.CENTER));
        table.addColumn(column);
        Main.getTableManager().setTable(table);
    }
}
