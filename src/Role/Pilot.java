/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Util.Message;
import Util.Timing;

/**
 *
 * @author kent
 */
public class Pilot extends Person<Vocab.P>
{
    public class State
    {
        
    }
    
    private State state;
    
    private boolean ready;
    private boolean waitingOnStatDetails4Sitrep;
    private boolean flyingUAV;
    
    public Pilot()
    {
        super(Job.P);
        ready = false;
        waitingOnStatDetails4Sitrep = false;
        flyingUAV = false;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready;
    }
 
    @Override
    protected void doTimeRelatedTasksMaybe()
    {
        Message<Vocab.P> observation = this.peekAtObservations();
        
        if(observation != null)
        {
            switch(observation.getDetail())
            {
                case problem:
                    Timing.delay(0.4, 3);
                    getTeam().getPilotGUI().rxMessage(this, 
                            new Message<>(Job.P, Vocab.PGUI.modify_flight_path, null));
                    getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(Job.P, Vocab.MM.sitrep, Vocab.Detail.problem));
                    
                    break;
                case adjust_flight:
                    Timing.delay(0.4, 3);
                    getTeam().getPilotGUI().rxMessage(this, 
                            new Message<>(Job.P, Vocab.PGUI.modify_flight_path, null));
                    getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(Job.P, Vocab.MM.sitrep, Vocab.Detail.adjust));
                    
                    break;
                case subtle:
                    Timing.delay(0.4, 3);
                    getTeam().getPilotGUI().rxMessage(this, 
                            new Message<>(Job.P, Vocab.PGUI.modify_flight_path, null));
                    
                    break;
                
                default:
                    throw new AssertionError(observation.getDetail().name());
                
            }
            
            this.popTopObservation();
        }
    }
    
    @Override
    protected void MM_Message_Handler(Message<Vocab.P> msg)
    {
        switch(msg.getMessage())
        {
            case search_info:
                if(ready)
                {
                    this.rxMessage(this, 
                        new Message<>(Job.MM, Vocab.P.modify_flight, null));
                }
                else
                {
                    Timing.delay(0.4, 1.5);
                    ready = true;
                    
                }
                break;
            case modify_flight:
                Timing.delay(0.4, 1.5);
                if (flyingUAV)
                {
                    getTeam().getPilotGUI().rxMessage(this, 
                            new Message<>(Job.P, Vocab.PGUI.modify_flight_path, null));
                }
                else
                {
                    getTeam().getPilotGUI().rxMessage(this, 
                            new Message<>(Job.P, Vocab.PGUI.take_off, null));
                    flyingUAV = true;
                }
                
                break;
                
            case req_sitrep:
                waitingOnStatDetails4Sitrep = true;
                Timing.delay(0.4, 1.0);
                getTeam().getPilotGUI().rxMessage(this, 
                        new Message<>(Job.P, Vocab.PGUI.req_status_details, null));
                
                break;
            case land:
                Timing.delay(1.4, 3.0);
                getTeam().getPilotGUI().rxMessage(this, 
                        new Message<>(Job.P, Vocab.PGUI.land, null));
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    @Override
    protected void PGUI_Message_Handler(Message<Vocab.P> msg)
    {
        
        Timing.delay(1.4, 3.0);
        switch(msg.getMessage())
        {
            case status_details:
                if (waitingOnStatDetails4Sitrep || msg.getDetail() == Vocab.Detail.problem)
                {
                    waitingOnStatDetails4Sitrep = false;
                    getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(Job.P, Vocab.MM.sitrep, msg.getDetail()));
                }
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    @Override
    protected void VA_Message_Handler(Message<Vocab.P> msg)
    {
        switch(msg.getMessage())
        {
            case modify_flight:
                Timing.delay(0.4, 5);
                getTeam().getPilotGUI().rxMessage(this, 
                        new Message<>(Job.P, Vocab.PGUI.modify_flight_path, null));
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }

    @Override
    public void sortMessageQueues() {
    }
    
}
