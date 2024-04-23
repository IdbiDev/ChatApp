package me.idbi.chatapp.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.idbi.chatapp.table.rows.Row;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Column {

    private List<Row> rows = new ArrayList<>();
    private int width;

    public Column(int width) {
        this.width = Math.max(1, width);
        this.rows = new ArrayList<>();
    }

    public Column() {
        this.width = 1;
        this.rows = new ArrayList<>();
    }

    public void addRow(Row row) {
        this.rows.add(row);

        if(this.width < (Row.getSelectChar() + row.getFilledLine(this.width)).length() && row.isSelectable()) {
            this.width = (Row.getSelectChar() + row.getFilledLine(this.width)).length();
        }
        else if(this.width < row.getFilledLine(this.width).length()) {
            this.width = row.getFilledLine(this.width).length();
        }
    }
}
