package me.idbi.chatapp.utils.formatters;

public class FormatterUtils {

    private String text;

    public FormatterUtils(String text) {
        this.text = text;
    }

    public FormatterUtils change(int index, String character) {
        try {
            this.text = this.text.substring(0, index) + character + this.text.substring(index + 1);
        } catch (StringIndexOutOfBoundsException e) {}
        return this;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
