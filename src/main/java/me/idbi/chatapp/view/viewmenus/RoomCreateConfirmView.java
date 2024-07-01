package me.idbi.chatapp.view.viewmenus;

import dorkbox.util.Sys;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.CreateRoomPacket;
import me.idbi.chatapp.table.Column;
import me.idbi.chatapp.table.Row;
import me.idbi.chatapp.table.Table;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

@Setter
public class RoomCreateConfirmView implements IView, IView.Tableable {
    @Getter
    private CreateRoomPacket packet;


    @Override
    public ViewType getType() {
        return ViewType.ROOM_CREATE_CONFIRM;
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
        Column name = new Column(20);
        name.addRow(new Row("Név", false, false, Row.Aligment.CENTER));
        name.addRow(new Row(packet.getName(), false, false));

        Column infos = new Column(10);
        infos.addRow(new Row("Maximum felhasználók", false, false, Row.Aligment.CENTER));
        infos.addRow(new Row(packet.getFormattedMaxMembers(), false, false, Row.Aligment.CENTER));

        Column psw = new Column(20);
        psw.addRow(new Row("Jelszó", false, false, Row.Aligment.CENTER));
        psw.addRow(new Row(packet.getPassword() == null ? "-" : packet.getPassword(), false, false, Row.Aligment.CENTER));


        Table roomTable = new Table();
        roomTable.addColumn(name, infos, psw);

        int length = -1;
        for (String value : roomTable.getTable().values()) {
            if (length < value.length()) {
                length = value.length();
            }
        }

        Table manageTable = new Table();
        Column manageColumn = new Column(20);
        manageColumn.addRow(new Row("Kezelés", false, false, Row.Aligment.CENTER));
        manageColumn.addRow(new Row("Megerősítés", true, true, Row.Aligment.LEFT));
        manageColumn.addRow(new Row("Szerkesztés", true, false, Row.Aligment.LEFT));
        manageColumn.addRow(new Row("Mégse", true, false, Row.Aligment.LEFT));
        manageTable.addColumn(manageColumn);

        Main.getClientData().getTableManager().setHeader(roomTable);
        Main.getClientData().getTableManager().setTable(manageTable);
    }

    @Override
    public void update() {
    }
}
