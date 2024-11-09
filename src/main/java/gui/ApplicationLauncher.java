package gui;

import java.awt.Color;

import java.util.Locale;

import javax.swing.UIManager;


import configuration.ConfigXML;
import businessLogic.BLFacade;
import businessLogic.BLFacadeFactory;

public class ApplicationLauncher {

	public static void main(String[] args) {
	    ConfigXML c = ConfigXML.getInstance();

	    try {
	        
	        System.out.println(c.getLocale());
	        Locale.setDefault(new Locale(c.getLocale()));
	        System.out.println("Locale: " + Locale.getDefault());

	       
	        MainGUI a = new MainGUI();
	        a.setVisible(true);

	       
	        BLFacade appFacadeInterface;
	        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

	        BLFacadeFactory factory = new BLFacadeFactory();
            appFacadeInterface = factory.BLFacadeCreator(c.isBusinessLogicLocal());

	    
	        MainGUI.setBussinessLogic(appFacadeInterface);

	    } catch (Exception e) {
	      
	        MainGUI a = new MainGUI(); 
	        a.jLabelSelectOption.setText("Error: " + e.toString());
	        a.jLabelSelectOption.setForeground(Color.RED);
	        System.out.println("Error in ApplicationLauncher: " + e.toString());
	    }
	}


}
