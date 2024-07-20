package learning.jdbc.entity;

import java.math.BigDecimal;

public class TicketEntity {

    private Long id;
    private String passengerNo;
    private String passengerName;
    private FlightEntity flight;
    private String seatNo;
    private BigDecimal cost;

    public TicketEntity(Long id, String passengerNo, String passengerName,
                        FlightEntity flight, String seatNo, BigDecimal cost) {
        this(passengerNo, passengerName, flight, seatNo, cost);
        this.id = id;
    }

    public TicketEntity(String passengerNo, String passengerName,
                        FlightEntity flight, String seatNo, BigDecimal cost) {
        this.passengerNo = passengerNo;
        this.passengerName = passengerName;
        this.flight = flight;
        this.seatNo = seatNo;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(String passengerNo) {
        this.passengerNo = passengerNo;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public FlightEntity  getFlight() {
        return flight;
    }

    public void setFlight(FlightEntity flight) {
        this.flight = flight;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "TicketEntity{" +
               "id=" + id +
               ", passengerNo='" + passengerNo + '\'' +
               ", passengerName='" + passengerName + '\'' +
               ", flight=" + flight +
               ", seatNo='" + seatNo + '\'' +
               ", cost=" + cost +
               '}';
    }
}
