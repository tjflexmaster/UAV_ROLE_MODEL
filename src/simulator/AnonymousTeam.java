package simulator;

public class AnonymousTeam extends Team implements ITeam {

	private String _name;
	
	/**
	 * This is used to automate Team creation
	 */
	AnonymousTeam()
	{
		_com_channels = new ComChannelList();
	}
	
	public void name(String name)
	{
		_name = name;
	}
	
	public String name()
	{
		return _name;
	}
}
