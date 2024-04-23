package me.idbi.chatapp.table.rows;

import lombok.*;
import me.idbi.chatapp.utils.TerminalManager;

import java.util.function.Function;

@Setter
@Getter
public class Row {

    public enum Aligment {
        LEFT, CENTER, RIGHT;
    }

    @Getter private static String selectChar = "> ";
    private String line;
    private boolean selectable;
    private boolean selected;
    private final Aligment alignment;

    public Row(String line, boolean selectable, boolean selected, Aligment alignment) {
        this.line = line;
        this.selectable = selectable;
        this.selected = selected;
        this.alignment = alignment;
    }

    public Row(String line, boolean selectable, boolean selected) {
        this.line = line;
        this.selectable = selectable;
        this.selected = selected;
        this.alignment = Aligment.LEFT;
    }


    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    private String fill(int width) {
        String currentLine = getFormattedLine();
        return switch (this.alignment) {
            case CENTER -> center(currentLine, width);
            case LEFT -> String.format("%-" + width + "s", currentLine);
            case RIGHT -> String.format("%" + width + "s", currentLine);
        };
    }

    public String getFilledLine(int width) {
        return fill(width);
    }

    private String getFormattedLine() {
        return this.selected ? selectChar + this.line : this.line;
    }

    private String center(String s, int size) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(' ');
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
