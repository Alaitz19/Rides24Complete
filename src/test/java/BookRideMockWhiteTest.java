import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.After;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import domain.Booking;

public class BookRideMockWhiteTest {

    static DataAccess sut;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;
    @Mock
    private TypedQuery<Ride> rideQuery;
    @Mock
    TypedQuery<Traveler> travelerQuery;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    private Ride createTestRide(String from, String to, int availableSeats, double price) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;

        try {
            rideDate = sdf.parse("05/10/2026");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Ride(from, to, rideDate, availableSeats, price, new Driver("Ane", "1234"));
    }

    private Traveler createTestTraveler(String username, double money) {
        Traveler traveler = new Traveler(username, "password");
        traveler.setMoney(money);
        return traveler;
    }

    @Test
    public void test1() {
        try {
            Mockito.verify(db, Mockito.times(0)).persist(Mockito.any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test2() {
        String travelerUsername = "Mikel10";
        Ride ride = createTestRide("Donostia", "Zarautz", 2, 10.0);
        Mockito.when(db.find(Traveler.class, travelerUsername)).thenReturn(null);

        try {
            sut.open();
            boolean result = sut.bookRide(travelerUsername, ride, 2, 12.0);
            sut.close();

            assertFalse(result); 
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test3() {
        String travelerUsername = "Mikel10";
        Ride ride = createTestRide("Donostia", "Zarautz", 1, 10.0);
        Mockito.when(db.find(Ride.class, ride.getRideNumber())).thenReturn(ride);

        try {
            sut.open();
            boolean result = sut.bookRide(travelerUsername, ride, 2, 12.0);
            sut.close();

            assertFalse(result); 
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test4() {
        String travelerUsername = "Mikel10"; 
        Traveler traveler = createTestTraveler(travelerUsername, 5.0); 
        Ride ride = createTestRide("Donostia", "Zarautz", 2, 10.0);
        
        Mockito.when(db.find(Traveler.class, travelerUsername)).thenReturn(traveler);
        Mockito.when(db.find(Ride.class, ride.getRideNumber())).thenReturn(ride);

        try {
            sut.open();
            boolean result = sut.bookRide(travelerUsername, ride, 2, 5.0);
            sut.close();

            assertFalse(result); 
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test5() {
        String travelerUsername = "Mikel10";
        double initialBalance = 100.0;
        double ridePrice = 10.0;
        double discount = 2.0;
        int seatsToBook = 2;

        Traveler traveler = createTestTraveler(travelerUsername, initialBalance);
        List<Traveler> travelers = new ArrayList<>();
        travelers.add(traveler);
        Ride ride = createTestRide("Donostia", "Zarautz", 5, ridePrice);
        
        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(travelerQuery);
        Mockito.when(travelerQuery.getResultList()).thenReturn(travelers);

        sut.open(); 
        boolean result = sut.bookRide(travelerUsername, ride, seatsToBook, discount);
        sut.close(); 

        assertTrue(result); 
        verify(db).persist(any(Booking.class));
        verify(db).merge(ride);
        verify(db).merge(traveler);
        verify(et).begin(); 
        verify(et).commit(); 
    }
    
}
