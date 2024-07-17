package me.idbi.chatapp.commands;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Command {

    private String command;
    private List<String> aliases;
    private String description;
    private String usage;

    public Command(String command, List<String> aliases, String description, String usage) {
        this.command = command;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
    }

    public Command(String command, String usage, List<String> aliases) {
        this.command = command;
        this.aliases = aliases;
        this.usage = usage;
    }

    public Command(String command, List<String> aliases, String description) {
        this.command = command;
        this.aliases = aliases;
        this.description = description;
    }

    public Command(String command, List<String> aliases) {
        this.command = command;
        this.aliases = aliases;
    }

    public Command(String command) {
        this.command = command;
        this.aliases = new ArrayList<>();
    }
}
