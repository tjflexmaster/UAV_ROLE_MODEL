/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Util.Message;
import java.util.Date;

/**
 *
 * @author kent
 */
public class UAV extends Person<Vocab.UAV>
{
    public class State
    {
        public boolean inFlight;
        public boolean gimbalDown;
        public boolean hasProblem;
        public boolean needsAdjustment;
    }
    
    private State state;
    
    private Date hasProblem;
    private Date needsAdjustment;
    
        
    public UAV()
    {
        super(Job.UAV);
     
        state = new State();
        hasProblem = null;
        needsAdjustment = null;
        state.inFlight = false;
        state.needsAdjustment = false;
        state.hasProblem = false;
        state.gimbalDown = false;
    }
    
    @Override
    protected void doTimeRelatedTasksMaybe()
    {
        Message<Vocab.UAV> observation = this.peekAtObservations();
        
        if(observation != null)
        {
            switch(observation.getDetail())
            {
                case video_null:
                case video_possible_hit:
                case video_hit:
                case video_low_quality:
                    getTeam().getVideoGUI().rxMessage(this, new Message<>(Job.UAV, Vocab.VGUI.video, observation.getDetail()));
                    break;
                case adjust_flight:
                    this.setNeedsAdjustment(true);
                    break;
                case problem:
                    this.setProblem(true);
                    break;
                default:
                    throw new AssertionError(observation.getDetail().name());
            }
            
            this.popTopObservation();
        }
    }


    /**
     * @return the inFlight
     */
    public boolean isInFlight() {
        return state.inFlight;
    }

    /**
     * @return the hasProblem
     */
    public Date hasProblem() {
        return hasProblem;
    }

    /**
     * @param hasProblem the hasProblem to set
     */
    public void setProblem(boolean problem) {
        this.hasProblem = problem ? new Date() : null;
    }

    /**
     * @return the needsAdjustment
     */
    public Date getNeedsAdjustment() {
        return needsAdjustment;
    }

    /**
     * @param needsAdjustment the needsAdjustment to set
     */
    public void setNeedsAdjustment(boolean problem) {
        this.needsAdjustment = problem ? new Date() : null;
    }
    
    @Override
    protected void PGUI_Message_Handler(Message<Vocab.UAV> msg)
    {
        switch(msg.getMessage())
        {
            case take_off:
                state.inFlight = true;
                break;
            case modify_flight_path:
                break;
            case land:
                state.inFlight = false;
                break;
            case kill:
                state.inFlight = false;
                break;
            case toggle_gimbal:
                state.gimbalDown = state.gimbalDown; 
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
        }
    }

    /**
     * @return the gimbalDown
     */
    public boolean isGimbalDown() {
        return state.gimbalDown;
    }

    @Override
    public void sortMessageQueues() {
    }
    
}
