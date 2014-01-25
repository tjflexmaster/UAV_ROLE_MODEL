package simulator;

public interface IComLayer
{
  public String name();
  
  public Object value();
  
  public boolean isEqual(Object obj);
  
  public boolean isNotEqual(Object obj);
  
  public boolean isGt(Object obj);
  
  public boolean isLt(Object obj);
  
  public boolean isGtOrEqual(Object obj);
  
  public boolean isLtOrEqual(Object obj);
}
