package me.idbi.chatapp.table;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.table.rows.Row;
import me.idbi.chatapp.utils.formatters.Formatter;
import me.idbi.chatapp.utils.formatters.FormatterUtils;
import me.idbi.chatapp.utils.MapUtils;

import java.util.*;

@Getter
@Setter
public class Table {

    private List<Column> columns;
    private int height;
    private List<Integer> bars;
    private Column selectedColumn;

    public Table() {
        this.columns = new ArrayList<>();
        this.height = 0;
        this.bars = new ArrayList<>();
        this.bars.add(2);
    }

    private void updateSize() {
        if (this.height < this.columns.size()) {
            this.height = this.columns.size();
        }
    }
    public void refreshWidth() {
        this.columns.forEach(Column::refreshWidth);
    }

    public Map<Integer, String> getTable() {
        List<Integer> borders = new ArrayList<>();
        updateSize();
        Map<Integer, String> table = new HashMap<>();

        int columnIndex = 0;
        for (Column column : this.columns) {
            for (int i = 0; i < column.getRows().size(); i++) {
                Row row = column.getRows().get(i);
                if(row.isSelectable() && row.isSelected()) {
                    this.selectedColumn = column;
                }

                String line = leftBorder(row.getFilledLine(column.getWidth()));
                if(columnIndex == this.columns.size() - 1) {
                    line = rightBorder(line);
                }


                if(!table.containsKey(i)) {
                    table.put(i, line);
                    borders.add(table.get(i).length());
                    continue;
                }

                table.put(i, table.get(i) + line);
                borders.add(table.get(i).length());
            }

            columnIndex++;
        }

        String bars = "-".repeat(table.get(0).length());
        FormatterUtils cross = Formatter.format(bars);
        cross.change(0, "+");
        cross.change(table.get(0).length() - 1, "+");
        for (Integer border : borders) {
            cross.change(border, "+");
        }

        table = MapUtils.insert(table, 0, cross.toString());

        for (Integer bar : this.bars) {
            table = MapUtils.insert(table, bar, cross.toString());
        }

        table.put(table.size(), cross.toString());
        return table;
    }

    public void addColumn(Column column) {
        this.columns.add(column);

        if(this.height < column.getRows().size()) {
            this.height = column.getRows().size();
        }
    }

    public void addColumn(Column... columns) {
        Arrays.stream(columns).toList().forEach(this::addColumn);
    }


    public Row nextUp() {
        if(this.selectedColumn == null) return null;
        Row selectedRow = this.getSelectedRow();
        if(selectedRow == null) return null;
        int selectedIndex = this.getIndex(selectedRow);
        for (int i = this.selectedColumn.getRows().size() - 1; i >= 0; i--) {
            Row row = this.selectedColumn.getRows().get(i);
            if(!row.isSelectable()) continue;
            if(selectedRow == null) return row;

            if(i < selectedIndex) {
                selectedRow.unselect();
                row.select();
                return row;
            }
        }

        return null;
    }

    public Row nextDown() {
        if(this.selectedColumn == null) return null;
        Row selectedRow = this.getSelectedRow();
        if(selectedRow == null) return null;
        int selectedIndex = this.getIndex(selectedRow);
        for (int i = 0; i < this.selectedColumn.getRows().size(); i++) {
            Row row = this.selectedColumn.getRows().get(i);
            if(!row.isSelectable()) continue;
            if(selectedRow == null) return row;

            if(i > selectedIndex) {
                selectedRow.unselect();
                row.select();
                return row;
            }
        }

        return null;
    }

    public int getIndex(Row row) {
        return this.selectedColumn.getRows().indexOf(row);
    }

    public Row getSelectedRow() {
        if(this.selectedColumn == null) return null;
        return this.selectedColumn.getRows()
                .stream()
                .filter(Row::isSelected)
                .findAny()
                .orElse(null);
    }

    private String leftBorder(String text) {
        return "| " + text + " ";
    }

    private String rightBorder(String text) {
        return text + "|";
    }

    private String allBorder(String text) {
        return "| " + text + " |";
    }

}
