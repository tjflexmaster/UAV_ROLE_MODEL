package model.actors;

import simulator.*;

public class UAV extends Actor {

	public enum DATA_UAV_VGUI {
		CRASHED

	}

	public enum DATA_UAV_OGUI {

	}

	public enum VISUAL_UAV_OP_COMM {
		LANDED, FLYING, CRASHED

	}

	public enum DATA_UAV_COMM {
		UAV_LANDED_UAVHAG, UAVHAG_INACTIVE_UAV, UAVHAG_LOW_UAV, UAVHAG_CRASHED_UAV, UAVHAG_GOOD_UAV
		
	}

	public UAV(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("UAV");
		
		/* UAV Battery */
	}

	@Override
	protected void initializeInternalVariables() {
		
	}
}
