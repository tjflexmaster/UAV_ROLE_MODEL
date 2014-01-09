package model.xml_parser;

public class XMLDataTypes
{

  public static final String INTEGER = "Integer";
  public static final String BOOLEAN = "Boolean";
  public static final String STRING = "String";
  
  public static Object getObject(String type, String value)
  {
    if ( type.equals(XMLDataTypes.INTEGER) ) {
      return Integer.parseInt(value);
    } else if ( type.equals(XMLDataTypes.BOOLEAN) ) {
      return Boolean.parseBoolean(value);
    } else
      return value;
  }
}
