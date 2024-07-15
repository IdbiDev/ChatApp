package me.idbi.chatapp.database;

import dorkbox.util.Sys;
import me.idbi.chatapp.Main;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseDriver {
    public CompletableFuture<Integer> exec(String query, Object... args) {
        CompletableFuture<Integer> cmp = new CompletableFuture<>();
        try {
            PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement(query);
            int paramcount = 1;
            for(Object o : args){
                if (o instanceof Integer d){
                    statement.setInt(paramcount,d);
                }else if (o instanceof String d){
                    statement.setString(paramcount,d);
                }else if (o instanceof Boolean d){
                    statement.setBoolean(paramcount,d);
                }else if (o instanceof Float d){
                    statement.setFloat(paramcount,d);
                }else if (o instanceof Double d){
                    statement.setDouble(paramcount,d);
                }else if (o instanceof Date d){
                    statement.setDate(paramcount, new java.sql.Date(d.getTime()));
                }else if (o instanceof Long d){
                    statement.setLong(paramcount,d);
                }else if (o instanceof String[] d){
                    statement.setArray(paramcount, Main.getDatabaseManager().getConnection().createArrayOf("text", d));
                } else if(o instanceof UUID d) {
                    statement.setString(paramcount, d.toString());
                }
                paramcount++;
            }

            System.out.println(statement);
            cmp.complete(statement.executeUpdate());

        } catch (SQLException e) {
            e.printStackTrace();
            cmp.complete(null);
        }

        return cmp;

    }

    public CompletableFuture<ResultSet> poll(String query, Object... args) {
        CompletableFuture<ResultSet> cmp = new CompletableFuture<>();
        try {
            PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement(query);
            int paramcount = 1;
            for(Object o : args){
                if (o instanceof Integer d){
                    statement.setInt(paramcount,d);
                } else if (o instanceof String d){
                    statement.setString(paramcount,d);
                } else if (o instanceof Boolean d){
                    statement.setBoolean(paramcount,d);
                } else if (o instanceof Float d){
                    statement.setFloat(paramcount,d);
                } else if (o instanceof Double d){
                    statement.setDouble(paramcount,d);
                } else if (o instanceof Date d) {
                    statement.setDate(paramcount, new java.sql.Date(d.getTime()));
                } else if (o instanceof Long d) {
                    statement.setLong(paramcount,d);
                } else if (o instanceof String[] d) {
                    statement.setArray(paramcount, Main.getDatabaseManager().getConnection().createArrayOf("text", d));
                } else if(o instanceof UUID d) {
                    statement.setString(paramcount, d.toString());
                }
                paramcount++;
            }
            cmp.complete(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            cmp.complete(null);
        }
        return cmp;
    }
}