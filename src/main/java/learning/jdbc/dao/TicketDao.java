package learning.jdbc.dao;

import learning.jdbc.dto.TicketFilter;
import learning.jdbc.entity.TicketEntity;
import learning.jdbc.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements Dao<Long,  TicketEntity> {

    private static final TicketDao INSTANCE = new TicketDao();
    private static final String DELETE_SQL = """
            DELETE FROM ticket WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET passenger_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT ticket.id, passenger_no, passenger_name, flight_id, seat_no, cost,
            f.flight_no,
            f.status,
            f.aircraft_id,
            f.arrival_airport_code,
            f.arrival_date,
            f.departure_airport_code,
            f.departure_date
            FROM ticket
            JOIN flight f ON f.id = ticket.flight_id
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE ticket.id = ?
            """;

    private TicketDao() {
    }

    private static final FlightDao flightDao = FlightDao.getInstance();

    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TicketEntity save(TicketEntity ticket) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3,  ticket.getFlight().id());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                ticket.setId(generatedKeys.getLong("id"));
            }
            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(TicketEntity ticket) {
        try (Connection connection = ConnectionManager.get();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlight().id());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.setLong(6, ticket.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<TicketEntity> findById(Long id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            TicketEntity ticketEntity = null;
            if (resultSet.next()) {
                ticketEntity = buildTicket(resultSet);
            }
            return Optional.ofNullable(ticketEntity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static TicketEntity buildTicket(ResultSet resultSet) throws SQLException {
        return new TicketEntity(resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDao.findById(resultSet.getLong("flight_id")).orElse(null)  ,
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost"));
    }

    public List<TicketEntity> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TicketEntity> ticketEntities = new ArrayList<>();
            while (resultSet.next()) {
                ticketEntities.add(buildTicket(resultSet));

            }
            return ticketEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TicketEntity> findByAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var sql = FIND_ALL_SQL + """
                LIMIT ?
                OFFSET ?
                """;
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TicketEntity> ticketEntities = new ArrayList<>();
            while (resultSet.next()) {
                ticketEntities.add(buildTicket(resultSet));
            }
            return ticketEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
