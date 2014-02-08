package model;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import model.xml_parser.XmlModelParser;
import simulator.*;
import simulator.Simulator.*;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = Simulator.getSim();
		
		final JPanel panel = new JPanel();
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(panel);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        
        //This is where a real application would open the file.
        try {
          //"src/test/UAS_in_NAS.xml"
          XmlModelParser xml = new XmlModelParser(file);
          
          sim.setup(xml.getTeam(), DebugMode.DEBUG, DurationMode.MIN);
          
          sim.run();
        } catch (AssertionError e) {
          System.out.print(e.getMessage());
        }
        
    }
		
	}
}
