package simulator;

public class ComLayerFactory
{
  public static IComLayer createLayer(String name, Object obj)
  {
    if ( obj instanceof String ) 
    {
      return new StringLayer(name, (String) obj);
    } 
    else if ( obj instanceof Integer )
    {
      return new IntegerLayer(name, (Integer) obj);
    }
    else if ( obj instanceof Boolean )
    {
      return new BooleanLayer(name, (Boolean) obj);
    }
    else
    {
      assert false : "Unknown Layer object type cannot create ComLayer";
      return null;
    }
  }
  
  public static IComLayer createLayer(String name, 
                                      IComLayer.DataType dataType)
  {
    IComLayer layer = null; 
    switch(dataType)
    {
      case STRING:
        layer = new StringLayer(name);
        break;
      case BOOLEAN:
        layer = new BooleanLayer(name);
        break;
      case INTEGER:
        layer = new IntegerLayer(name);
        break;
      default:
        assert false: "Invalid DataType!  Cannot create ComLayer";
        break;
    }
    
    return layer;
  }
  
}
