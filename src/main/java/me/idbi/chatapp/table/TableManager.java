package me.idbi.chatapp.table;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;

public class TableManager {
    @Getter private Table currentTable;
    @Getter @Setter private Table header;

    public TableManager() {
        this.currentTable = null;
    }

    public void setTable(Table table) {
        this.currentTable = table;
        Main.getClientData().getTerminalManager().clear();
        if(this.header != null) {
            this.header.getTable().values().forEach(System.out::println);
        }
        if(table != null)
            table.getTable().values().forEach(System.out::println);
    }

    public void nextDown() {
        if(this.currentTable == null) return;

        Main.getClientData().getTerminalManager().clear();
        this.currentTable.nextDown();
        if(this.header != null) {
            this.header.getTable().values().forEach(System.out::println);
        }
        this.currentTable.getTable().values().forEach(System.out::println);
    }

    public void nextUp() {
        if(this.currentTable == null) return;

        Main.getClientData().getTerminalManager().clear();
        this.currentTable.nextUp();
        if(this.header != null) {
            this.header.getTable().values().forEach(System.out::println);
        }
        this.currentTable.getTable().values().forEach(System.out::println);
    }
}
