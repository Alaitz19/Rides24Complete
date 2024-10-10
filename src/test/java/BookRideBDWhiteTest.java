import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

import java.util.Date;

public class BookRideBDWhiteTest {
/*
    private TestDataAccess testDA;
    private DataAccess sut;
    private Traveler traveler;
    private Ride ride;
    private Driver driver;

    @Before
    public void setUp() {
        testDA = new TestDataAccess();
        sut = new DataAccess();

        // Abrir la conexi�n
        testDA.open();
        sut.open();

        // Crear datos de prueba
        traveler = testDA.addTraveler("Mikel10", "password");
        traveler.setMoney(100.0); // Dinero inicial del viajero

        driver = testDA.createDriver("Driver1", "driverPassword");
        ride = testDA.addRide(driver.getUsername(), "Donostia", "Zarautz", new Date(), 5, 50.0f);
    }

    @After
    public void tearDown() {
        try {
            // Cerrar sesi�n del viajero solo si existe
            if (traveler != null) {
                testDA.removeTraveler(traveler.getUsername());
            }
            // Cerrar sesi�n del conductor solo si existe
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
   

    @Test
    public void testTravelerNotInDatabase() {
        // Aseg�rate de que el viajero no est� en la base de datos
        String travelerUsername = "Mikel10";
       
        testDA.removeTraveler(travelerUsername);
        try {
            // Intentar reservar para un viajero que no est� en la base de datos
            boolean result = sut.bookRide(travelerUsername, ride, 2, 12.0);

            // Verificar que no se pudo hacer la reserva
            assertFalse("La reserva no deber�a ser posible porque el viajero no est� en la base de datos", result);
        } catch (Exception e) {
            fail("No deber�a lanzar excepci�n: " + e.getMessage());
        }
    }

    @Test
    public void testInsufficientFunds() {
    	 // Crear un nuevo viajero espec�fico para esta prueba
        String travelerUsername = "TravelerWithInsufficientFunds";
        Traveler traveler = testDA.addTraveler(travelerUsername, "password");
        traveler.setMoney(2.0); // Fondos insuficientes para el viaje

        try {
     
            // Intentar hacer la reserva
            boolean result = sut.bookRide(traveler.getUsername(), ride, 2, 0); // Sin descuento
       
            // Verificar que no se puede realizar la reserva debido a fondos insuficientes
            assertFalse("No deber�a poder reservar con fondos insuficientes", result);
        } catch (Exception e) {
            // Si se lanza una excepci�n, el test fallar� con un mensaje
            fail("No deber�a lanzar excepci�n: " + e.getMessage());
        }
    }

    @Test
    public void testNotEnoughSeats() {
        try {
            // Reservar m�s asientos de los disponibles
            boolean result = sut.bookRide(traveler.getUsername(), ride, 6, 0); 
            assertFalse("No deber�a poder reservar m�s asientos de los que hay disponibles", result);
        } catch (Exception e) {
            fail("No deber�a lanzar excepci�n: " + e.getMessage());
        }
    }
    
    @Test
    public void testSuccessfulBooking() {
    	try {
            // Reservar con �xito
            boolean result = sut.bookRide(traveler.getUsername(), ride, 2, 10.0); // Reservar con descuento
            assertTrue("La reserva deber�a ser exitosa", result);
        } catch (Exception e) {
            fail("No deber�a lanzar excepci�n: " + e.getMessage());
        }

      
    }
    */
}
