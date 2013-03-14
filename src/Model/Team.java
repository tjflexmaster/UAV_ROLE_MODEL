/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Simulation.MissionParams;
import Role.Environment;
import Role.MissionManager;
import Role.ParentSearch;
import Role.Pilot;
import Role.PilotGUI;
import Role.UAV;
import Role.VideoAnalyst;
import Role.VideoGUI;

/**
 *
 * @author kent
 */
public class Team
{
    private Environment environment;
    private MissionManager missionManager;
    private ParentSearch parentSearch;
    private Pilot pilot;
    private PilotGUI pilotGUI;
    private UAV uav;
    private VideoAnalyst videoAnalyst;
    private VideoGUI videoGUI;
    public static boolean DEBUG = true;
    private boolean isMissionActive;

    
    public void goTeam()
    {
        environment.start();
        missionManager.start();
        parentSearch.start();
        pilot.start();
        pilotGUI.start();
        uav.start();
        videoAnalyst.start();
        videoGUI.start();
    }
    
    public static void initTeam(Team t, MissionParams mp)
    {
        t.isMissionActive = true;
        
        t.setEnvironment(new Environment(mp));
        t.setMissionManager(new MissionManager());
        t.setParentSearch(new ParentSearch());
        t.setPilot(new Pilot());
        t.setPilotGUI(new PilotGUI());
        t.setUav(new UAV());
        t.setVideoAnalyst(new VideoAnalyst());
        t.setVideoGUI(new VideoGUI());
    }
    
    /**
     * @param environment the environment to set
     */
    void setEnvironment(Environment environment) {
        environment.setTeam(this);
        this.environment = environment;
    }

    /**
     * @param missionManager the missionManager to set
     */
    void setMissionManager(MissionManager missionManager) {
        
        missionManager.setTeam(this);
        this.missionManager = missionManager;
        
    }

    /**
     * @param parentSearch the parentSearch to set
     */
    void setParentSearch(ParentSearch parentSearch) {
        parentSearch.setTeam(this);
        this.parentSearch = parentSearch;
    }

    /**
     * @param pilot the pilot to set
     */
    void setPilot(Pilot pilot) {
        pilot.setTeam(this);
        this.pilot = pilot;
    }

    /**
     * @param pilotGUI the pilotGUI to set
     */
    void setPilotGUI(PilotGUI pilotGUI) {
        pilotGUI.setTeam(this);
        this.pilotGUI = pilotGUI;
    }

    /**
     * @param uav the uav to set
     */
    void setUav(UAV uav) {
        uav.setTeam(this);
        this.uav = uav;
    }

    /**
     * @param videoAnalyst the videoAnalyst to set
     */
    void setVideoAnalyst(VideoAnalyst videoAnalyst) {
        videoAnalyst.setTeam(this);
        this.videoAnalyst = videoAnalyst;
    }

    /**
     * @param videoGUI the videoGUI to set
     */
    void setVideoGUI(VideoGUI videoGUI) {
        videoGUI.setTeam(this);
        this.videoGUI = videoGUI;
    }

    /**
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * @return the missionManager
     */
    public MissionManager getMissionManager() {
        return missionManager;
    }

    /**
     * @return the parentSearch
     */
    public ParentSearch getParentSearch() {
        return parentSearch;
    }

    /**
     * @return the pilot
     */
    public Pilot getPilot() {
        return pilot;
    }

    /**
     * @return the pilotGUI
     */
    public PilotGUI getPilotGUI() {
        return pilotGUI;
    }

    /**
     * @return the uav
     */
    public UAV getUav() {
        return uav;
    }

    /**
     * @return the videoAnalyst
     */
    public VideoAnalyst getVideoAnalyst() {
        return videoAnalyst;
    }

    /**
     * @return the videoGUI
     */
    public VideoGUI getVideoGUI() {
        return videoGUI;
    }

    public boolean isMissionActive()
    {
        return isMissionActive;
    }
    
    public boolean isTeamWorking()
    {
        return getEnvironment().isAlive() ||
                getMissionManager().isAlive() ||
                getParentSearch().isAlive() ||
                getPilot().isAlive() ||
                getPilotGUI().isAlive() ||
                getUav().isAlive() ||
                getVideoAnalyst().isAlive() ||
                getVideoGUI().isAlive();
    }

    /**
     * @param isMissionActive the isMissionActive to set
     */
    public void setIsMissionActive(boolean isMissionActive) {
        this.isMissionActive = isMissionActive;
    }
}
