package learning.jdbc;

import learning.jdbc.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {

    public static void main(String[] args) throws SQLException {
        batch();
    }

    public static void transaction() throws SQLException {
        long flightId = 9;
        String deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        String deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?";
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;
        try {
            connection = ConnectionManager.get();
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql);

            connection.setAutoCommit(false); // отключаем autocommit

            deleteTicketsStatement.setLong(1, flightId);
            deleteFlightStatement.setLong(1, flightId);

            deleteTicketsStatement.executeUpdate();
            deleteFlightStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }

    // Batch запросы
    public static void batch() throws SQLException {
        long flightId = 8;
        String deleteFlightSql = "DELETE FROM flight WHERE id = " + flightId;
        String deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = " + flightId;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.get();
            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);

            connection.setAutoCommit(false); // отключаем autocommit

            int[] ints = statement.executeBatch();

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}
