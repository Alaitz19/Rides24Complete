package businessLogic;

import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import domain.Driver;
import domain.Ride;

public class DriverAdapter extends AbstractTableModel{

	
	protected Driver driver;
	 protected String[] columnNames =
			    new String[] {"From", "To", "Date", "Places", "Price" };
	 public DriverAdapter(Driver d) {
		 super();
		 this.driver=d;
	 }
	@Override
	public int getRowCount() {
		 return driver.getCreatedRides().size();
	}

	@Override 
	public int getColumnCount() {
		 return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		 Iterator<Ride> iterator = driver.getCreatedRides().iterator(); 
		  Ride currentRide = null; 

		  for(int i = 0; i <= rowIndex; i++) { 
			  if(iterator.hasNext()) { 
				  currentRide = iterator.next(); 
			  } else { 
				  return null;
			  }
		  }
		  if(currentRide == null) { 
			  return null;
		  } 
		  switch(columnIndex) { 
		  	case 0:
		  		return currentRide.getFrom(); 
		  	case 1:
		  		return currentRide.getTo();
		  	case 2:
		  		return currentRide.getDate();
		  	case 3:
		  		return currentRide.getnPlaces();
		  	case 4:
		  		return currentRide.getPrice();
		  	default: 
		  		return null;
		  }
	}
}
