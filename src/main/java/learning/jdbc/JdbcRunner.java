package learning.jdbc;

import learning.jdbc.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
//        List<Long> flightsBetween = getFlightsBetween(LocalDate.of(2020, 10, 1).atStartOfDay(),
//                LocalDateTime.now());
//        System.out.println(flightsBetween);
        checkMetadata();
    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = """
                select * from ticket where flight_id = ?
                """;
        List<Long> result = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(50); // fetch size
            preparedStatement.setQueryTimeout(10); // query timeout
            preparedStatement.setMaxFieldSize(100); // max field suze
            preparedStatement.setLong(1, flightId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }

    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
                SELECT id FROM flight
                WHERE flight.departure_date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }

    private static void checkMetadata() throws SQLException {
        try (Connection connection = ConnectionManager.get()) {
            var metaData = connection.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
                String catalog = catalogs.getString(1);
                System.out.println(catalog);

                ResultSet schemas = metaData.getSchemas();
                while (schemas.next()) {
                    String tableSchem = schemas.getString("TABLE_SCHEM");
                    System.out.println("   --" + tableSchem);

                    ResultSet tables
                            = metaData.getTables(catalog, tableSchem, "%", null);
                    while (tables.next()) {
                        System.out.println("    --" + tables.getString("TABLE_NAME"));
                    }
                }
            }
        }
    }


}
