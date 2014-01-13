package model.xml_parser;

import simulator.ComChannel;
import simulator.Memory;


public class XMLPredicate<T>
{
  enum Type {
    GT,
    LT,
    EQ,
    NE,
    LTEQ,
    GTEQ
  }
  
  private T _source;
  private String _value;
  private Type _type;
  
  
  public XMLPredicate(String typeString, T source, String value)
  {
    source(source);
    value(value);
    type(typeString);
  }
  
  public void type(String type)
  {
    switch(type) {
      case "eq":
        _type = Type.EQ;
        break;
      case "gt":
        _type = Type.GT;
        break;
      case "lt":
        _type = Type.LT;
        break;
      case "ne":
        _type = Type.NE;
        break;
      case "gteq":
        _type = Type.GTEQ;
        break;
      case "lteq":
        _type = Type.LTEQ;
        break;
      default:
        assert false: "Invalid predicate type";
        break;
    }
  }

  public Type type()
  {
    return _type;
  }

  public void type(Type type)
  {
    this._type = type;
  }

  public String value()
  {
    return _value;
  }

  public void value(String value)
  {
    this._value = value;
  }

  public T source()
  {
    return _source;
  }

  public void source(T source)
  {
    this._source = source;
  }
  
  public boolean test()
  {
    Object obj;
    if ( _source instanceof Memory<?> )
      obj = ((Memory<?>) _source).value();
    else if ( _source instanceof ComChannel<?> )
      obj = ((ComChannel<?>) _source).value();
    else
      return false;
    
    //Handle null values
    if ( obj == null ) {
      return _value == null;
    }
    else if ( _value == null )
      return false;
    
    //We only test 3 types Integer Boolean and String
    if ( obj instanceof Integer )
    {
      Integer lhs = (Integer) obj;
      Integer rhs = (Integer) XMLDataTypes.getObject(XMLDataTypes.INTEGER, _value);
      switch(_type) {
        case EQ:
          return lhs.equals(rhs);
        case GT:
          return (lhs > rhs);
        case LT:
          return (lhs < rhs);
        case NE:
          return !lhs.equals(rhs);
        case GTEQ:
          return (lhs >= rhs);
        case LTEQ:
          return (lhs <= rhs);
      }
      return false;
    }
    else if ( obj instanceof Boolean )
    {
      Boolean lhs = (Boolean) obj;
      Boolean rhs = (Boolean) XMLDataTypes.getObject(XMLDataTypes.BOOLEAN, _value);
      switch(_type) {
        case EQ:
          return lhs.equals(rhs);
        case GT:
          return false;
        case LT:
          return false;
        case NE:
          return !lhs.equals(rhs);
        case GTEQ:
          return false;
        case LTEQ:
          return false;
      }
      return false;
      
    }
    else
    {
      String lhs = (String) obj;
      String rhs = _value;
      switch(_type) {
        case EQ:
          return lhs.equals(rhs);
        case GT:
          return false;
        case LT:
          return false;
        case NE:
          return !lhs.equals(rhs);
        case GTEQ:
          return false;
        case LTEQ:
          return false;
      }
      return false;
    }
  }
  
}


