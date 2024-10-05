import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;

public class BookRideMockBlackTest {
    static DataAccess sut;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private Ride ride;
    private Traveler traveler;

    @Mock
    private TypedQuery<Ride> rideQuery;
    @Mock
    private TypedQuery<Traveler> travelerQuery;

    private static final String USERNAME = "Mikel10";
    private static final int SEATS = 2;
    private static final double DISCOUNT = 5.0;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);
        sut.open();

        // Inicializar datos de prueba
        initializeTestEntities();
    }

    @After
    public void tearDown() {
        persistenceMock.close();
        sut.close();
    }

    private void initializeTestEntities() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;

        try {
            rideDate = sdf.parse("05/10/2026");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Inicializar el Driver
        Driver driver = new Driver("Ane", "1234");

        // Inicializar el Ride
        ride = new Ride("Donostia", "Zarautz", rideDate, 5, 10.0, driver);

        // Inicializar el Traveler
        traveler = new Traveler(USERNAME, "password");
        traveler.setMoney(50.0); // Supongamos que el viajero tiene suficiente dinero
    }

    private void setupTravelerQueryWithResults() {
        when(db.createQuery(any(String.class), eq(Traveler.class))).thenReturn(travelerQuery);
        when(travelerQuery.setParameter(any(String.class), any())).thenReturn(travelerQuery);
        when(travelerQuery.getResultList()).thenReturn(Collections.singletonList(traveler));
    }

    private void setupRideQueryWithResults() {
        when(db.createQuery(any(String.class), eq(Ride.class))).thenReturn(rideQuery);
        when(rideQuery.setParameter(any(String.class), any())).thenReturn(rideQuery);
        when(rideQuery.getResultList()).thenReturn(Collections.singletonList(ride));
    }

    private void setupEmptyTravelerQuery() {
        when(db.createQuery(any(String.class), eq(Traveler.class))).thenReturn(travelerQuery);
        when(travelerQuery.setParameter(any(String.class), any())).thenReturn(travelerQuery);
        when(travelerQuery.getResultList()).thenReturn(Collections.emptyList());
    }

    private void setupEmptyRideQuery() {
        when(db.createQuery(any(String.class), eq(Ride.class))).thenReturn(rideQuery);
        when(rideQuery.setParameter(any(String.class), any())).thenReturn(rideQuery);
        when(rideQuery.getResultList()).thenReturn(Collections.emptyList());
    }
    private void setupRideQueryWithZeroPrice() {
        when(db.createQuery(any(String.class), eq(Ride.class))).thenReturn(rideQuery);
        when(rideQuery.setParameter(any(String.class), any())).thenReturn(rideQuery);
        
        // Crea un Ride con precio 0.0
        Ride rideWithZeroPrice = new Ride("Donostia", "Zarautz", new Date(), 5, 0.0, new Driver("Ane", "1234"));
        
        when(rideQuery.getResultList()).thenReturn(Collections.singletonList(rideWithZeroPrice));
    }

    // Test case: traveler == null (usuario no existe)
    @Test
    public void testBookRide_TravelerNotFound() {
        setupEmptyTravelerQuery(); // Mock vacio para el Traveler

        // Verificar que no se puede reservar el viaje
        assertFalse(sut.bookRide(USERNAME, ride, SEATS, DISCOUNT));
    }

    // Test case: ride == null (viaje no existe)
    @Test
    public void testBookRide_RideNotFound() {
        setupTravelerQueryWithResults(); // Mock lleno para el Traveler
        setupEmptyRideQuery(); // Mock vacio para el Ride

        // Probar con ride nulo
        assertFalse(sut.bookRide(USERNAME, null, SEATS, DISCOUNT));
    }

    // Test case: seats == 0
    @Test
    public void testBookRide_SeatsNotZero() {
        setupTravelerQueryWithResults(); // Mock lleno para el Traveler
        setupRideQueryWithResults(); // Mock lleno para el Ride

        ride.setnPlaces(0); // Sin asientos disponibles

        // Intentar reservar 2 asientos, lo cual debería fallar
        assertFalse("No hay suficientes asientos disponibles", sut.bookRide(USERNAME, ride, 2, DISCOUNT));
    }
    
 // Test case: desk <= 0 (descuento no válido)
    @Test
    public void testBookRide_DeskLessThanOrEqualToZero() {
        setupTravelerQueryWithResults(); // Mock lleno para el Traveler
        setupRideQueryWithResults(); // Mock lleno para el Ride

        double desk = -5; // Descuento negativo
        String expectedMessage = "El desk debe ser mayor que 0.0";

        try {
            sut.bookRide(USERNAME, ride, SEATS, desk);
            fail("Se esperaba IllegalArgumentException, pero no se lanzó"); // Asegurarse de que la excepción se lance
        } catch (IllegalArgumentException e) {
            String actualMessage = e.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }
 // Test case: ride.getBalioa() == 0 (precio del viaje es cero)
    @Test
    public void testBookRide_RidePriceZero() {
        setupTravelerQueryWithResults(); // Mock lleno para el Traveler
        setupRideQueryWithZeroPrice(); // Mock para el Ride con precio 0.0

        String expectedMessage = "El precio del viaje no puede ser cero o negativo";
        try {
            sut.bookRide(USERNAME, ride, SEATS, DISCOUNT);
            fail("Se esperaba IllegalArgumentException, pero no se lanzó"); // Asegurarse de que la excepción se lance
        } catch (IllegalArgumentException e) {
            String actualMessage = e.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }
    // Test case: ride.getnPlaces() < seats (no hay suficientes asientos)
    @Test
    public void testBookRide_NotEnoughSeats() {
        setupTravelerQueryWithResults(); // Mock lleno para el Traveler

        ride.setnPlaces(1); // Solo 1 asiento disponible

        // Intentar reservar 2 asientos
        assertFalse(sut.bookRide(USERNAME, ride, 2, DISCOUNT));
    }

    // Test case: traveler.getMoney() < ridePriceDesk (usuario no tiene suficiente dinero)
    @Test
    public void testBookRide_NotEnoughMoney() {
        traveler.setMoney(9.0); // Dinero insuficiente

        setupTravelerQueryWithResults(); // Mock lleno para el Traveler
        setupRideQueryWithResults(); // Mock lleno para el Ride

        // Intentar reservar 2 asientos con un descuento de 5.0
        assertFalse(sut.bookRide(USERNAME, ride, SEATS, DISCOUNT));
    }

  
} 