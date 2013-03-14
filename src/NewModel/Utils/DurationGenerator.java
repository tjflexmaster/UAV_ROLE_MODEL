package NewModel.Utils;

import java.util.Random;

import NewModel.Roles.RoleState;

public class DurationGenerator {
	
//	public static int getDuration(RoleState state)
//	{
//		int min,max;
//		Random rand = new Random();
//		
//		switch(state) {
//			//Add cases for specific RoleStates
//			case PS_SEARCH_POKE_MM:
//			case PS_TERMINATE_SEARCH_POKE_MM:
//				min = 1;
//				max = 30;
//				break;
//			case PS_SEARCH_TX_MM:
//			case PS_TERMINATE_SEARCH_TX_MM:
//				min = 10;
//				max = 30;
//				break;
//			case PS_SEARCH_COMPLETE_ACK_MM:
//			case PS_SEARCH_END_MM:
//			case PS_TARGET_SIGNTING_ACK_MM:
//			case PS_TERMINATE_SEARCH_END_MM:
//			case MM_SEARCH_ACK_PS:
//			case MM_TERMINATE_SEARCH_ACK_PS:
//			case MM_SEARCH_COMPLETE_END_PS:
//			case MM_TARGET_SIGHTING_END_PS:
//				min = 1;
//				max = 1;
//				break;
//			default:
//				min = 2;
//				max = 10;
//				break;
//		}
//		
//		return rand.nextInt(max - min + 1) + min;
//	}
	
	
	public static int getRandDuration(int min, int max)
	{
		if ( min > max ) {
			return 0;
		}
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}

}
