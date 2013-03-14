/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Simulation.MissionParams;
import Model.Team;
import Util.Test;
import Util.Timing;

/**
 *
 * @author kent
 */
public class TestRun 
{
    public static void test1()
    {
        Team.DEBUG = true;
        
        Team t = new Team();
        
        MissionParams mp = MissionParams.test1Params();
        
        Team.initTeam(t, mp);
        
        t.goTeam();
        
        while(t.isTeamWorking()) {/* tiddle thumbs*/}
        
        Test.Assert(t.getEnvironment().getDebugInfo().entrySet().size() == 0);
        Test.Assert(t.getPilotGUI().getDebugInfo().entrySet().size() == 0);
        Test.Assert(t.getUav().getDebugInfo().entrySet().size() == 0);
        Test.Assert(t.getVideoGUI().getDebugInfo().entrySet().size() == 0);
        Test.Assert(t.getMissionManager().getDebugInfo().entrySet().size() == 1);
        Test.Assert(t.getPilot().getDebugInfo().entrySet().size() == 1);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().entrySet().size() == 1);
        Test.Assert(t.getParentSearch().getDebugInfo().entrySet().size() == 1);
        
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.search_info) == 1);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.search_info) == 1);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().get(Vocab.VA.search_info) == 1);
        Test.Assert(t.getParentSearch().getDebugInfo().get(Vocab.PS.observation) == 1);
        
        System.out.println("Test 1 passed...");
    }
    
    public static void test2()
    {
        Team.DEBUG = true;
        
        Team t = new Team();
        
        MissionParams mp = MissionParams.test2Params();
        
        Team.initTeam(t, mp);
        
        t.goTeam();
        
        while(t.isTeamWorking()) {/* tiddle thumbs*/}
        
        Test.Assert(t.getEnvironment().getDebugInfo().entrySet().isEmpty());
        Test.Assert(t.getParentSearch().getDebugInfo().entrySet().size() == 1);
        Test.Assert(t.getMissionManager().getDebugInfo().entrySet().size() == 2);
        Test.Assert(t.getPilot().getDebugInfo().entrySet().size() == 2);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().entrySet().size() == 1);
        Test.Assert(t.getPilotGUI().getDebugInfo().entrySet().size() == 2);
        Test.Assert(t.getUav().getDebugInfo().entrySet().size() == 2);
        Test.Assert(t.getVideoGUI().getDebugInfo().entrySet().size() == 0);
        
        Test.Assert(t.getParentSearch().getDebugInfo().get(Vocab.PS.observation) == 2);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.search_info) == 2);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.observation) == 1);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.search_info) == 2);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.modify_flight) == 2);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().get(Vocab.VA.search_info) == 2);
        Test.Assert(t.getPilotGUI().getDebugInfo().get(Vocab.PGUI.take_off) == 1);
        Test.Assert(t.getPilotGUI().getDebugInfo().get(Vocab.PGUI.modify_flight_path) == 1);
        Test.Assert(t.getUav().getDebugInfo().get(Vocab.UAV.take_off) == 1);
        Test.Assert(t.getUav().getDebugInfo().get(Vocab.UAV.modify_flight_path) == 1);
        
        System.out.println("Test 2 passed...");
    }
    
    public static void test3()
    {
        Team.DEBUG = true;
        
        Team t = new Team();
        
        MissionParams mp = MissionParams.test3Params();
        
        Team.initTeam(t, mp);
        
        t.goTeam();
        
        while(t.isTeamWorking()) {/* tiddle thumbs*/}
        
        Test.Assert(t.getEnvironment().getDebugInfo().entrySet().isEmpty());
        Test.Assert(t.getParentSearch().getDebugInfo().entrySet().size() == 3);
        Test.Assert(t.getMissionManager().getDebugInfo().entrySet().size() == 5);
        Test.Assert(t.getPilot().getDebugInfo().entrySet().size() == 5);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().entrySet().size() == 2);
        Test.Assert(t.getPilotGUI().getDebugInfo().entrySet().size() == 3);
        Test.Assert(t.getUav().getDebugInfo().entrySet().size() == 3);
        Test.Assert(t.getVideoGUI().getDebugInfo().entrySet().size() == 2);
        
        Test.Assert(t.getParentSearch().getDebugInfo().get(Vocab.PS.observation) == 3);
        Test.Assert(t.getParentSearch().getDebugInfo().get(Vocab.PS.sitrep) == 2);
        Test.Assert(t.getParentSearch().getDebugInfo().get(Vocab.PS.priority_update) == 1);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.search_info) == 3);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.observation) == 2);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.req_sitrep) == 1);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.annotation) == 2);
        Test.Assert(t.getMissionManager().getDebugInfo().get(Vocab.MM.sitrep) == 4);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.search_info) == 4);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.modify_flight) == 6);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.observation) == 3);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.req_sitrep) == 1);
        Test.Assert(t.getPilot().getDebugInfo().get(Vocab.P.status_details) == 1);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().get(Vocab.VA.search_info) == 4);
        Test.Assert(t.getVideoAnalyst().getDebugInfo().get(Vocab.VA.req_sitrep) == 1);
        Test.Assert(t.getPilotGUI().getDebugInfo().get(Vocab.PGUI.take_off) == 1);
        Test.Assert(t.getPilotGUI().getDebugInfo().get(Vocab.PGUI.modify_flight_path) == 8);
        Test.Assert(t.getPilotGUI().getDebugInfo().get(Vocab.PGUI.req_status_details) == 1);
        Test.Assert(t.getVideoGUI().getDebugInfo().get(Vocab.VGUI.video) == 4);
        Test.Assert(t.getVideoGUI().getDebugInfo().get(Vocab.VGUI.annotate_object) == 2);
        Test.Assert(t.getUav().getDebugInfo().get(Vocab.UAV.take_off) == 1);
        Test.Assert(t.getUav().getDebugInfo().get(Vocab.UAV.modify_flight_path) == 8);
        Test.Assert(t.getUav().getDebugInfo().get(Vocab.UAV.observation) == 6);
        
        System.out.println("Test 3 passed...");
    }
    
    public static void test4()
    {
        Team.DEBUG = true;
        
        Team t = new Team();
        
        MissionParams mp = MissionParams.alpha();
        
        Team.initTeam(t, mp);
        
        t.goTeam();
        
        while(t.isTeamWorking()) {/* tiddle thumbs*/}
        
        System.out.println("Test 4 passed...");
    }
    
    public static void main(String[] args)
    {
        Timing.setTiming(false);
        
        System.out.println("Running tests...");

        Test.Assert(MissionParams.alpha().equals(MissionParams.alpha()));
        
        test1();
        test2();
        test3();

        test4();
        System.out.println("All tests passed!");
    }
}
