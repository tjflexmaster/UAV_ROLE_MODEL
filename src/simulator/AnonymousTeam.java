package simulator;

public class AnonymousTeam extends Team implements ITeam {

	private String _name;
	
	/**
	 * This is used to automate Team creation
	 */
	
	public void name(String name)
	{
		_name = name;
	}
	
	public String name()
	{
		return _name;
	}
}
