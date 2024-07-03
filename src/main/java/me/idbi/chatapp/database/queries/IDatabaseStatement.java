package me.idbi.chatapp.database.queries;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IDatabaseStatement {

    public PreparedStatement getStatement() throws SQLException;
}
