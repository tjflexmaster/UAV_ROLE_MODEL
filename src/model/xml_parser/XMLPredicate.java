package model.xml_parser;

import simulator.ComChannel;
import simulator.IComLayer;
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
  private Object _value;
  private Type _type;
  private String _layer;
  
  
  public XMLPredicate(String typeString, T source, Object value)
  {
    source(source);
    value(value);
    type(typeString);
    layer(null);
  }
  
  public XMLPredicate(String typeString, T source, Object value, String layer)
  {
    source(source);
    value(value);
    type(typeString);
    layer(layer);
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
      case "neq":
        _type = Type.NE;
        break;
      case "gteq":
        _type = Type.GTEQ;
        break;
      case "lteq":
        _type = Type.LTEQ;
        break;
      default:
        assert false: "Invalid predicate type(" + type +")";
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

  public Object value()
  {
    return _value;
  }

  public void value(Object value)
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
  
  public String layer()
  {
    return _layer;
  }
  
  public void layer(String layer)
  {
    if ( layer == "" )
      _layer = null;
    else
      _layer = layer;
  }
  
  public boolean test()
  {
    IComLayer layer;
    if ( _source instanceof Memory )
      layer = ((Memory) _source).layer();
    else if ( _source instanceof ComChannel )
      layer = ((ComChannel) _source).getLayer(_layer);
    else
      return false;
    
    //Handle null values
    if ( layer == null || layer.value() == null ) {
      switch(_type) {
        case NE: 
          return !(_value == null);
        default:
          return _value == null;
      }
    } else if ( _value == null )
      return false;
    
    //Test the ComLayer
    switch(_type) {
      case EQ:
        return layer.isEqual(_value);
      case GT:
        return layer.isGt(_value);
      case LT:
        return layer.isLt(_value);
      case NE:
        return layer.isNotEqual(_value);
      case GTEQ:
        return layer.isGtOrEqual(_value);
      case LTEQ:
        return layer.isLtOrEqual(_value);
    }
    return false;
  }
  
}


