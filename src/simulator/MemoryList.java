package simulator;

import java.util.HashMap;

@SuppressWarnings("serial")
public class MemoryList extends HashMap<String, Memory>
{

  public MemoryList add(Memory item) {
    this.put(item.name(), item);
    return this;
  }

  /**
   * Get Memory by name
   */
  public Memory getMemory(String name)
  {
    return this.get(name);
  }

  public MemoryList getUniqueMemory()
  {
    MemoryList result = new MemoryList();
    for( Memory mem : this.values() ) {
      if ( !result.containsKey(mem.name()) ) {
        result.add(mem);
      }
    }
    return result;
  }
}
