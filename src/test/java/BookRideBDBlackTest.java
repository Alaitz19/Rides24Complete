
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class BookRideBDBlackTest {

    private TestDataAccess testDA;
    private DataAccess sut;
    private Traveler traveler;
    private Ride ride;
    private Driver driver;

    @Before
    public void setUp() {
        testDA = new TestDataAccess();
        sut = new DataAccess();

        // Abrir la conexión
        testDA.open();
        sut.open();

        // Crear datos de prueba
        traveler = testDA.addTraveler("Mikel10", "password");
        traveler.setMoney(100.0); // Dinero inicial del viajero

        driver = testDA.createDriver("Driver1", "driverPassword");
        ride = testDA.addRide(driver.getUsername(), "Donostia", "Zarautz", new Date(), 2, 50.0f);
    }

    @After
    public void tearDown() {
        try {
            // Cerrar sesión del viajero solo si existe
            if (traveler != null) {
                testDA.removeTraveler(traveler.getUsername());
            }
            // Cerrar sesión del conductor solo si existe
            if (driver != null) {
                testDA.removeDriver(driver.getUsername());
            }
            // Eliminar viaje solo si existe
            if (ride != null) {
                testDA.removeRide(driver.getUsername(), "Donostia", "Zarautz", ride.getDate());
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        } finally {
            testDA.close(); 
            sut.close(); 
        }
    }
	 // Test case: traveler == null (usuario no existe)
    @Test
    public void testBookRide_TravelerNotFound() {
        String nonExistentUsername = "NonExistentUser";

        // Intentar reservar para un viajero que no está en la base de datos
        boolean result = sut.bookRide(nonExistentUsername, ride, 2, 12.0);

        // Verificar que no se pudo hacer la reserva
        assertFalse("La reserva no debería ser posible porque el viajero no está en la base de datos", result);
    }

    // Test case: ride == null (viaje no existe)
    @Test
    public void testBookRide_RideNotFound() {
        // Intentar reservar un viaje que no existe
        boolean result = sut.bookRide(traveler.getUsername(), null, 2, 5.0);

        // Verificar que no se pudo hacer la reserva
        assertFalse("La reserva no debería ser posible porque el viaje no existe", result);
    }

    // Test case: seats == 0
    @Test
    public void testBookRide_SeatsNotZero() {
      try {
        // Intentar reservar 0 asientos, lo cual debería fallar
        boolean result =sut.bookRide(traveler.getUsername(), ride, 0, 5.0);
        
        
        fail("Se esperaba IllegalArgumentException, pero no se lanzó");
    } catch (IllegalArgumentException e) {
        String expectedMessage = "El desk debe ser mayor que 0.0";
        assertTrue(e.getMessage().contains(expectedMessage));
    }
       
    }
    
    // Test case: desk <= 0 (descuento no válido)
    @Test
    public void testBookRide_DeskLessThanOrEqualToZero() {
        double desk = -5; // Descuento negativo

        try {
            sut.bookRide(traveler.getUsername(), ride, 2, desk);
            fail("Se esperaba IllegalArgumentException, pero no se lanzó");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "El desk debe ser mayor que 0.0";
            assertTrue(e.getMessage().contains(expectedMessage));
        }
    }

    // Test case: ride.getBalioa() == 0 (precio del viaje es cero)
    @Test
    public void testBookRide_RidePriceZero() {
        ride.setPrice(0.0); // Precio del viaje a cero

        try {
            sut.bookRide(traveler.getUsername(), ride, 2, 12.0);
            fail("Se esperaba IllegalArgumentException, pero no se lanzó");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "El precio del viaje no puede ser cero o negativo";
            assertTrue(e.getMessage().contains(expectedMessage));
        }
    }

 // Test case: ride.getnPlaces() < seats (no hay suficientes asientos)
    @Test
    public void testBookRide_NotEnoughSeats() {
        ride.setnPlaces(1); // Solo 1 asiento disponible

        try {
            // Intentar reservar 2 asientos
            boolean result = sut.bookRide(traveler.getUsername(), ride, 2, 5.0);
            assertFalse("No debería poder reservar más asientos de los que hay disponibles", result);
        } catch (Exception e) {
            fail("Se lanzó una excepción inesperada: " + e.getMessage());
        }
    }

    // Test case: traveler.getMoney() < ridePriceDesk (usuario no tiene suficiente dinero)
    @Test
    public void testBookRide_NotEnoughMoney() {
        traveler.setMoney(1.0); // Dinero insuficiente
        ride.setPrice(12.0); // Asegúrate de que el precio del viaje esté configurado

        try {
            // Intentar reservar 2 asientos con un descuento de 0 (sin descuento)
        	System.out.println("Dinero del viajero: " + traveler.getMoney());
        	System.out.println("Precio total: " + ride.getPrice());

            boolean result = sut.bookRide(traveler.getUsername(), ride, 2, 0.0); // Sin descuento
            assertTrue("El viajero no debería tener suficiente dinero para reservar", result);
        } catch (Exception e) {
            fail("Se lanzó una excepción inesperada: " + e.getMessage());
        }
    }
    
}

