package org.example;

public class Command {
    private String command;
    private String Extra;

    public Command(String command, String Extra) {
        this.command = command;
        this.Extra = Extra;
    }

    public String getCommand() {return command;}
    public String getExtra() {return Extra;}
}
