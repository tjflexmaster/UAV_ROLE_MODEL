package Simulator;

import java.util.ArrayList;


/**
 * 
 * print current state of the simulator and take input from the user
 * @author rob.ivie
 *
 */
public class UDOList extends ArrayList<UDO> {
	
	/**
	 * I have no idea what this means, but it fixed a compilation warning...
	 */
	private static final long serialVersionUID = -8343739743287084503L;

	/**
	 * initialize all of the outputs that will be used during the simulation
	 */
	public UDOList() {
		
		/*	Mission Manager */
		
		this.add(new UDO("MM_POKE_PS"));							//0
		this.add(new UDO("MM_POKE_VO"));							//1
		this.add(new UDO("MM_POKE_OP"));							//2
		this.add(new UDO("MM_POKE_VGUI"));							//3
		this.add(new UDO("MM_ACK_PS"));								//4
		this.add(new UDO("MM_ACK_VO"));								//5
		this.add(new UDO("MM_ACK_OP"));								//6
		this.add(new UDO("MM_END_PS"));								//7
		this.add(new UDO("MM_END_VO"));								//8
		this.add(new UDO("MM_END_OP"));								//9
		this.add(new UDO("MM_END_VGUI"));							//10
		this.add(new UDO("MM_TARGET_DESCRIPTION"));					//11
		this.add(new UDO("MM_TERMINATE_SEARCH_VO"));				//12
		this.add(new UDO("MM_NEW_SEARCH_AOI"));						//13
		this.add(new UDO("MM_TERMINATE_SEARCH_OP"));				//14
		this.add(new UDO("MM_SEARCH_AOI_COMPLETE"));				//15
		this.add(new UDO("MM_SEARCH_FAILED"));						//16
		this.add(new UDO("MM_TARGET_SIGHTING_F"));					//17
		this.add(new UDO("MM_TARGET_SIGHTING_T"));					//18
		this.add(new UDO("MM_FLYBY_REQ_T"));						//19
		this.add(new UDO("MM_FLYBY_REQ_F"));						//20
		this.add(new UDO("MM_ANOMALY_DISMISSED_T"));				//21
		this.add(new UDO("MM_ANOMALY_DISMISSED_F"));				//22
		this.add(new UDO("MM_BUSY_PS"));							//23
		this.add(new UDO("MM_BUSY_OP"));							//24
		this.add(new UDO("MM_BUSY_VO"));							//25
	
		/*	Video Operator */
		
		this.add(new UDO("VO_POKE_MM"));							//26
		this.add(new UDO("VO_END_MM"));								//27
		this.add(new UDO("VO_TARGET_SIGHTING_T_MM"));				//28
		this.add(new UDO("VO_TARGET_SIGHTING_F_MM"));				//29
		this.add(new UDO("VO_POKE_OP"));							//30
		this.add(new UDO("VO_END_OP"));								//31
		this.add(new UDO("VO_BAD_STREAM_OP"));						//32
		this.add(new UDO("VO_POKE_VGUI"));							//33
		this.add(new UDO("VO_END_VGUI"));							//34
		this.add(new UDO("VO_FLYBY_REQ_T_VGUI"));					//35
		this.add(new UDO("VO_FLYBY_REQ_F_VGUI"));					//36
		this.add(new UDO("VO_FLYBY_END_SUCCESS_VGUI"));				//37
		this.add(new UDO("VO_FLYBY_END_FAILED_VGUI"));				//38
		this.add(new UDO("VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI"));	//39
		this.add(new UDO("VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI"));	//40
		this.add(new UDO("VO_ACK_MM"));								//41
		this.add(new UDO("VO_BUSY_MM"));							//42
	
		/*	Video Operator Gui	*/
		
		this.add(new UDO("VGUI_FALSE_POSITIVE"));					//43
		this.add(new UDO("VGUI_TRUE_POSITIVE"));					//44
		this.add(new UDO("VGUI_VALIDATION_REQ_T"));					//45
		this.add(new UDO("VGUI_VALIDATION_REQ_F"));					//46
		this.add(new UDO("VO_FLYBY_REQ_T_OGUI"));					//47
		this.add(new UDO("VO_FLYBY_REQ_F_OGUI"));					//48
		this.add(new UDO("VO_FLYBY_END_FAILED_OGUI"));				//49
		this.add(new UDO("VO_FLYBY_END_SUCCESS_OGUI"));				//50
		this.add(new UDO("MM_FLYBY_REQ_F_OGUI"));					//51
		this.add(new UDO("MM_FLYBY_REQ_T_OGUI"));					//52
		this.add(new UDO("VGUI_NORMAL"));							//53
		this.add(new UDO("VGUI_FLYBY_T"));							//54
		this.add(new UDO("VGUI_FLYBY_F"));							//55
		
		/* UAV Operator */
		
		/* UAV Operator Gui */
		
	}
	
	/**
	 * this method is like ArrayList.get(int) except it takes a string
	 * the string is used to match a UDO of this list
	 * the found UDO is then returned
	 * @param name
	 * @return a UDO object whose name matches the name specified
	 * 		or return null if nothing is found
	 */
	public UDO get(String name) {
		
		for (int UDOIndex = 0; UDOIndex < this.size(); UDOIndex++) {
			
			if (this.get(UDOIndex).getName().equals(name)) {
				
				return this.get(UDOIndex);
				
			}
			
		}
		
		return null;
	}

}
