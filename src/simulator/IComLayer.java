package simulator;

public interface IComLayer
{
  //Those data types which are allowed by the simlation framework
  public enum DataType
  {
    STRING,
    INTEGER,
    BOOLEAN
  }
  
  public String name();
  
  public Object value();
  
  public void value(Object obj);
  
  public DataType dataType();
  
  public boolean isEqual(Object obj);
  
  public boolean isNotEqual(Object obj);
  
  public boolean isGt(Object obj);
  
  public boolean isLt(Object obj);
  
  public boolean isGtOrEqual(Object obj);
  
  public boolean isLtOrEqual(Object obj);
}
