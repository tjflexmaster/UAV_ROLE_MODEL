package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Team implements ITeam {
	
	ArrayList<IActor> _actors;
	ArrayList<IComChannel> _com_channels;
	

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		HashMap<IActor, ITransition> result = new HashMap<IActor, ITransition>();
		for( IActor a : _actors ) {
			result.putAll(a.getTransitions());
		}
		
		return result;
	}
	
	void addActor(IActor a) 
	{
		assert _actors.contains(a):"Actor is already a part of the team";
		
		_actors.add(a);
	}
	
	void addComChannel(IComChannel c)
	{
		assert _com_channels.contains(c):"Com channel already defined";
	}

	
}
