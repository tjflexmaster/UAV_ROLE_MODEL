package model.xml_parser;


import java.util.Vector;

import org.jfree.chart.JFreeChart;

import simulator.IActor;
import simulator.ITeam;
import simulator.Team;
import simulator.metrics.MetricContainer;
import simulator.metrics.MetricDisplayPanel;

public class XMLTeam extends Team implements ITeam {
	
	public String _name;
	
	public XMLTeam(String name) {
		this._name = name;
	}
	
	@Override
	public Vector<JFreeChart> getCharts()
	{
	  Vector<JFreeChart> result = new Vector<JFreeChart>();
//	  result.add(_chart);
	  for(IActor a : _actors)
	  {
	    result.addAll(a.getCharts());
	  }
	  return result;
	}
	
  @Override
  public Vector<MetricDisplayPanel> getPanels()
  {
    Vector<MetricDisplayPanel> result = new Vector<MetricDisplayPanel>();

    for(IActor a : _actors)
    {
      if ( a.name().equals("UAVOP") || a.name().equals("ATC"))
        result.addAll(a.getPanels());
    }
    return result;
  }
	
	@Override
  public void setMetrics(MetricContainer c)
  {
    c.actorMetrics.clear();
    for(IActor a : _actors) {
      if ( a.name().equals("UAVOP") || a.name().equals("ATC")) {
        MetricContainer m = new MetricContainer();
        a.setMetrics(m);
        c.actorMetrics.add(m);
      }
    }
    
  }
	
//	public void toFile() {
//    try {
//      String filename = "metrics_" + new Date().getTime() +".csv";
//      System.out.println("Saving file at: " + filename);
//      PrintWriter workloadWriter = new PrintWriter(new File(filename));
//      for ( DecisionWorkloadMetric m : this._dwmMetrics )
//        workloadWriter.println("dwm," + m);
//      for ( ChannelConflictMetric m : this._ccmMetrics )
//        workloadWriter.println("ccm," + m);
//      for ( ChannelLoadMetric m : this._clmMetrics )
//        workloadWriter.println("clm," + m);
//      for ( ActorOutputMetric m : this._aomMetrics )
//        workloadWriter.println("aom," + m);
//      workloadWriter.close();
////      
////      PrintWriter metricsWriter = new PrintWriter(new File("metrics.txt"));
////      for(MetricDataStruct m : metrics) {
////        metricsWriter.println(m.toString());
////      }
////      metricsWriter.close();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//  }
}
