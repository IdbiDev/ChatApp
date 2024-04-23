package me.idbi.chatapp;

import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.table.Column;
import me.idbi.chatapp.table.rows.Row;
import me.idbi.chatapp.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientSide {

    public static void main(String[] args) {

        Room room1 = new Room("Patrik's Room", null, null, new ArrayList<>(), 32);
        Room room2 = new Room("Sósmogyi's Room", null, null, new ArrayList<>(), 32);
        Room room3 = new Room("Adrián's Room", null, null, new ArrayList<>(), 32);
        Room room4 = new Room("GYVAKK admin", null, null, new ArrayList<>(), 32);

        Column column = new Column(3);
        column.addRow(new Row("Név", false, false, Row.Aligment.CENTER));
        column.addRow(new Row(room1.getName(), true, true));
        column.addRow(new Row(room2.getName(), true, false));
        column.addRow(new Row(room3.getName(), true, false));
        column.addRow(new Row(room4.getName(), true, false));

        Column column1 = new Column(22);
        column1.addRow(new Row("Információk", false, false, Row.Aligment.CENTER));
        column1.addRow(new Row(room1.getMembers().size() + "/" + room1.getMaxMembers(), false, false, Row.Aligment.CENTER));
        column1.addRow(new Row(room2.getMembers().size() + "/" + room2.getMaxMembers(), false, false, Row.Aligment.CENTER));
        column1.addRow(new Row(room3.getMembers().size() + "/" + room3.getMaxMembers(), false, false, Row.Aligment.CENTER));
        column1.addRow(new Row(room4.getMembers().size() + "/" + room4.getMaxMembers(), false, false, Row.Aligment.CENTER));

        Column column2 = new Column(17);
        column2.addRow(new Row("Jelszóvédett", false, false, Row.Aligment.CENTER));
        column2.addRow(new Row(room1.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
        column2.addRow(new Row(room2.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
        column2.addRow(new Row(room3.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
        column2.addRow(new Row(room4.hasPassword() ? "Igen" : "Nem", false, false, Row.Aligment.CENTER));
        Table table = new Table();
        table.addColumn(column);
        table.addColumn(column1);
        table.addColumn(column2);

        table.getTable().values().forEach(System.out::println);
        // System.out.println(table.getSelectedRow().getLine());
//        table.nextDown();
//        table.getTable().values().forEach(System.out::println);
//        table.nextDown();
//        table.getTable().values().forEach(System.out::println);
//        table.nextUp();
//        table.getTable().values().forEach(System.out::println);
//        Client client = new Client("127.0.0.1",5000);
//        while(true) {
//
//        }
    }
}