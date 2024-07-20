package learning.jdbc;

import learning.jdbc.dao.FlightDao;
import learning.jdbc.dao.TicketDao;
import learning.jdbc.dto.TicketFilter;
import learning.jdbc.entity.FlightEntity;
import learning.jdbc.entity.TicketEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {

    public static void main(String[] args) {
        var ticket = TicketDao.getInstance().findById(5L);
    }

    public static void saveTicket() {
        TicketDao instance = TicketDao.getInstance();
        FlightEntity flight = FlightDao.getInstance().findById(3L).orElse(null);
        TicketEntity ticketEntity = new TicketEntity("123456", "Test",
                flight,
                "B3",
                BigDecimal.TEN);
        TicketEntity savedTicket = instance.save(ticketEntity);
    }

    public static void deleteTicket() {
        TicketDao instance = TicketDao.getInstance();
        boolean delete = instance.delete(56L);
        System.out.println(delete);
    }

    public static void updateTicketCost(Long ticketId, Double cost) {
        TicketDao instance = TicketDao.getInstance();
        Optional<TicketEntity> maybeTicket = instance.findById(ticketId);
        maybeTicket.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(cost));
            instance.update(ticket);
        });
    }

    public static List<TicketEntity> findAll() {
        TicketDao instance = TicketDao.getInstance();
        return instance.findAll();
    }

    public static List<TicketEntity> findAll(TicketFilter ticketFilter) {
        return TicketDao.getInstance().findByAll(ticketFilter);
    }
}
