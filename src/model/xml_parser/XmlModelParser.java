package model.xml_parser;

import java.io.File;
import java.io.IOException;

import simulator.ComChannel;
import simulator.ComChannelList;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class XmlModelParser {

	public XmlModelParser(String xml_path)
	{
		try {
		  Builder parser = new Builder();
		  Document doc = parser.build(new File(xml_path));
//		  System.out.println(doc.toXML());
		  //This should print out the xml.
		  
		  //Get the Team
		  Element team_element = doc.getRootElement();
		  XMLTeam team = new XMLTeam(team_element.getAttribute("name").getValue());
		  
		  parseActors(team, team_element);
		  
		}
		catch (ParsingException ex) {
		  System.err.println("Error parsing the model!");
		}
		catch (IOException ex) {
		  System.err.println("Cannot open xml string.");
		}
	}
	
	private void parseActors(XMLTeam team, Element team_e)
	{
		Elements actors = parseElementArray(team_e, "actors", "actor");
		assert actors.size() >= 1 : "Actors are missing";
		for(int i=0; i < actors.size(); i++) {
			Element actor_e = actors.get(i);
			XMLActor actor = new XMLActor(actor_e.getAttributeValue("name"));
			System.out.println(actor_e.toXML());
			
			//Parse out an Actors available channels
			Elements channels = parseElementArray(actor_e, "channels", "channel");
			ComChannelList channel_list = new ComChannelList();
			for(int j=0; j < channels.size(); j++) {
				Element channel_e = channels.get(j);
				ComChannel<>
				ArrayList<ComChannel>
			}
			team.addActor(actor);
		}
	}
	
	/**
	 * Helper method for extracting arrays from the xml
	 * @param parent
	 * @param parent_name
	 * @param child_name
	 * @return
	 */
	private Elements parseElementArray(Element parent,
									   String parent_name,
									   String child_name)
	{
		Element wrapper = parent.getChildElements(parent_name).get(0);
		Elements children = wrapper.getChildElements(child_name); 
		return children;
	}
	
	private ComChannel.Type getComChannelType(String type)
	{
		if ( type == "visual" )
			return ComChannel.Type.VISUAL;
		else if ( type == "audio" )
			return ComChannel.Type.AUDIO;
		else
			return ComChannel.Type.DATA;
		
	}
}
