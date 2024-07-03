package me.idbi.chatapp.database.queries;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DatabaseUpdateStatement implements IDatabaseStatement {

    private final String table;
    private final List<IDatabaseQuery> queries;

    public DatabaseUpdateStatement(String table, IDatabaseQuery... queries) {
        this.table = table;
        this.queries = Arrays.asList(queries);
    }

    @Override
    public PreparedStatement getStatement() throws SQLException {
        Connection conn = new DatabaseManager().getConnection();

        DatabaseSetQuery sets = null;
        DatabaseWhereQuery wheres = null;
        for (IDatabaseQuery query : this.queries) {
            if (query instanceof DatabaseSetQuery set) {
                if (sets == null) {
                    sets = set;
                    continue;
                }
                sets.append(query);
            }
            else if (query instanceof DatabaseWhereQuery where) {
                if (wheres == null) {
                    wheres = where;
                    continue;
                }
                wheres.append(query);
            }
        }

        return conn.prepareStatement("UPDATE " + this.table + " SET " + sets + " WHERE " + wheres);

        new DatabaseUpdateStatement("column",
                Set.of("UUID", 123).append("NAME", "kbalu"),
                Where.of("UUID", 321).append("NAME", "Adbi20014"));
    }
}
