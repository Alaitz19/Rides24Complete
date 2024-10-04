import static org.junit.Assert.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


import static org.mockito.Mockito.*;

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
import java.util.*;

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
	@Mock
	private TypedQuery<Driver> driverQuery;

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


	@Test
	// Test case 1: DB not entry
	public void test1() {
		 try {
	            Mockito.verify(db, Mockito.times(0)).persist(Mockito.any());
	        } catch (Exception e) {
	            fail("Unexpected exception: " + e.getMessage());
	        }
	}

	@Test
	// Test case 2: The Traveler "Mikel10" does not exist, should return false.
	public void test2() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date rideDate = null;
		
		try {
			rideDate = sdf.parse("05/10/2026");
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Ride ride = new Ride("Donostia", "Zarautz", rideDate, 2, 10.0, new Driver("Ane", "1234"));
		String travelerUsername = "Mikel10";

		Mockito.when(db.find(Traveler.class, travelerUsername)).thenReturn(null);

		try {
			sut.open();
			boolean result = sut.bookRide(travelerUsername, ride, 2, 12.0);
			sut.close();

			assertFalse(result); // Expect false
		} catch (Exception e) {
			fail();
		}
	}
	    

	    @Test
	    // Test case 3: Traveler tries to book more places than available in ride.
	    public void test3() {
	        String travelerUsername = "Mikel10";
	        String rideFrom = "Donostia";
	        String rideTo = "Zarautz";
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        Date rideDate = null;
	        try {
	            rideDate = sdf.parse("05/10/2026");
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }

	        Ride ride = new Ride(rideFrom, rideTo, rideDate, 1, 10.0, new Driver("Ane", "1234"));
	        Mockito.when(db.find(Ride.class, rideFrom)).thenReturn(ride);

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
	    // Test case 4: Traveler has insufficient money to book the ride.
	    public void test4() {
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date rideDate = null;
			
			try {
				rideDate = sdf.parse("05/10/2026");
			} catch (ParseException e) {

				e.printStackTrace();
			}
			Ride ride = new Ride("Donostia", "Zarautz", rideDate, 2, 10.0, new Driver("Ane", "1234"));
			String travelerUsername = "Mikel10"; 
			Traveler traveler = new Traveler(travelerUsername,"12"); 
		    traveler.setMoney(5.0);
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
	 // Test case 5: Successfully booking a ride.
	 public void test5() {
	    	  // Arrange
	        String travelerUsername = "Mikel10";
	        double initialBalance = 100.0;
	        double ridePrice = 10.0;
	        double discount = 2.0;
	        int seatsToBook = 2;

	        // Create a Traveler with initial balance
	        Traveler traveler = new Traveler(travelerUsername, "password");
	        traveler.setMoney(initialBalance); // Traveler has enough money
	        List<Traveler> tra= new ArrayList<Traveler>();
	        tra.add(traveler);
	        // Create a Driver
	        Driver driver = new Driver("Juan", "driverPassword");

	        // Create a Ride with enough available seats
	        Ride ride = new Ride("Donostia", "Zarautz", new Date(), 5, ridePrice, driver);

	        // Mock the Traveler query
	        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(travelerQuery);
	        Mockito.when(travelerQuery.getResultList()).thenReturn(tra); // Return the traveler

	        // Act
	        sut.open(); // Open the entity manager
	        boolean result = sut.bookRide(travelerUsername, ride, seatsToBook, discount);
	        sut.close(); // Close the entity manager
	        System.out.println(result);
	        // Assert
	        assertTrue(result); // The booking should be successful
	       
	        // Verify interactions
	        verify(db).persist(any(Booking.class)); // Ensure a Booking was persisted
	        verify(db).merge(ride); // Ensure the ride was updated
	        verify(db).merge(traveler); // Ensure the traveler was updated
	        verify(et).begin(); // Ensure the transaction began
	        verify(et).commit(); // Ensure the transaction was committed
	    }
	    
}