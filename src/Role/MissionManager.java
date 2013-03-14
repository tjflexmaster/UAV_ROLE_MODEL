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

public class MissionManager extends Person<Vocab.MM>
{
    public class State
    {
        
    }
    
    private State state;
    private boolean reportToPSNeeded;
    private boolean waitingOnPSitrep;
    private boolean waitingOnVASitrep;
    
    private Vocab.Detail lastPSitrep;
    private Vocab.Detail lastVASitrep;
    
    public MissionManager()
    {
        super(Job.MM);
        
        reportToPSNeeded = false;
        waitingOnPSitrep = false;
        waitingOnVASitrep = false;
    }
    
    @Override
    protected void doTimeRelatedTasksMaybe()
    {
        Message<Vocab.MM> observation = this.peekAtObservations();
        
        if(observation != null)
        {
            switch(observation.getDetail())
            {
                case adjust_flight:
                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.modify_flight, null));
                    break;
                case adjust_search:
                    Timing.delay(0.1, 5);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.search_info, null));
                    getTeam().getVideoAnalyst().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.VA.search_info, null));
                    break;
            }
            
            this.popTopObservation();
        }
        
        Vocab.Detail vid = this.peekAtVideo();
        
        if (vid != null)
        {
            Timing.delay(1, 5);
            
            switch(vid)
            {
                case video_null:
                case video_possible_hit:
                case video_hit:
                case video_low_quality:
                    break;
                default:
                    throw new AssertionError(vid.name());
            }
            
            this.popTopVideo();
        }
    }
    
    
    @Override
    protected void P_Message_Handler(Message<Vocab.MM> msg)
    {
        switch(msg.getMessage())
        {
            case ready_to_rock:
                
                if (getTeam().getVideoAnalyst().isReady())
                {
                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.modify_flight, null));
     
                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.search_info, null));
                }
                    
                break;
            case priority_update:
                
                if (msg.getDetail().equals(Vocab.Detail.problem))
                {
                    Timing.delay(0.5, 4);
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.priority_update, Vocab.Detail.problem));
                }
                else if (msg.getDetail().equals(Vocab.Detail.adjust))
                {
                    Timing.delay(0.5, 4);
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.priority_update, Vocab.Detail.adjust));
                }
                else
                {
                    throw new AssertionError(msg.getDetail().name());
                }
                
                break;
            case sitrep:
                
                waitingOnPSitrep = false;
                this.lastPSitrep = msg.getDetail();
                Timing.delay(0.5, 2);
                
                if (reportToPSNeeded && !waitingOnVASitrep)
                {
                    Vocab.Detail detail = this.averageSitrepDetails(msg.getDetail(), this.lastVASitrep);
                    
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.sitrep, detail));
                    
                    reportToPSNeeded = false;
                }
                else if (msg.getDetail() == Vocab.Detail.problem)
                {
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.sitrep, msg.getDetail()));
                }
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
        
    @Override
    protected void PGUI_Message_Handler(Message<Vocab.MM> msg)
    {
        switch(msg.getMessage())
        {
            case status_details:
                switch(msg.getDetail())
                {
                    case problem:
                    case adjust:
                        Timing.delay(0.5, 2);
                        getTeam().getPilot().rxMessage(this, 
                                new Message<>(Job.MM, Vocab.P.modify_flight, null));
                    case none:
                        break;
                }
                break;

            default:
                throw new AssertionError(msg.getMessage().name());
        }
    }
    
    @Override
    protected void PS_Message_Handler(Message<Vocab.MM> msg)
    {
        switch(msg.getMessage())
        {
            case search_info:
                Timing.delay(0.5, 2);
                getTeam().getPilot().rxMessage(this, 
                        new Message<>(Job.MM, Vocab.P.search_info, null));
                Timing.delay(0.5, 2);
                getTeam().getVideoAnalyst().rxMessage(this, 
                        new Message<>(Job.MM, Vocab.VA.search_info, null));
                
                break;
            case kill_search:
                Timing.delay(0.5, 2);
                getTeam().getPilot().rxMessage(this, 
                        new Message<>(Job.MM, Vocab.P.land, null));
                
                break;
            case req_sitrep:
                reportToPSNeeded = waitingOnPSitrep = waitingOnVASitrep = true;
                Timing.delay(0.2, 1);
                getTeam().getPilot().rxMessage(this, 
                        new Message<>(Job.MM, Vocab.P.req_sitrep, null));
                Timing.delay(0.2, 1);
                getTeam().getVideoAnalyst().rxMessage(this, 
                        new Message<>(Job.MM, Vocab.VA.req_sitrep, null));
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    @Override
    protected void VA_Message_Handler(Message<Vocab.MM> msg)
    {
        switch(msg.getMessage())
        {
            case ready_to_rock:
                
                if (getTeam().getVideoAnalyst().isReady())
                {
                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.modify_flight, null));
     
                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.search_info, null));
                }
                
                break;
            case priority_update:
                
                if (msg.getDetail().equals(Vocab.Detail.problem))
                {
                    Timing.delay(0.5, 4);
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.priority_update, Vocab.Detail.problem));
                }
                else if (msg.getDetail().equals(Vocab.Detail.adjust))
                {
                    Timing.delay(0.5, 4);
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.priority_update, Vocab.Detail.adjust));
                }
                else
                {
                    throw new AssertionError(msg.getDetail().name());
                }
                
                break;
            case req_video_feed:
                
                break;
            case sitrep:
                
                waitingOnVASitrep = false;
                this.lastVASitrep = msg.getDetail();
                Timing.delay(0.5, 3);
                
                if (reportToPSNeeded && !waitingOnPSitrep)
                {
                    Vocab.Detail detail = this.averageSitrepDetails(msg.getDetail(), this.lastPSitrep);
                    
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.sitrep, detail));
                    
                    reportToPSNeeded = false;
                }
                else if (msg.getDetail() == Vocab.Detail.problem)
                {
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.sitrep, msg.getDetail()));
                }
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    @Override
    protected void VGUI_Message_Handler(Message<Vocab.MM> msg)
    {
        switch(msg.getMessage())
        {
            case video:
                switch(msg.getDetail())
                {
                case video_null:
                case video_possible_hit:
                case video_hit:
                    break;
                case video_low_quality:

                    Timing.delay(0.5, 2);
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.modify_flight, null));

                    break;
                default:
                    throw new AssertionError(msg.getDetail().name());
                    
                }
                break;
                
            case annotation:
                
                Timing.delay(0.5, 2);
                
                switch(msg.getDetail())
                {
                case video_possible_hit:
                    getTeam().getPilot().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.P.modify_flight, null));
                    break;
                case video_hit:
                    getTeam().getParentSearch().rxMessage(this, 
                            new Message<>(Job.MM, Vocab.PS.priority_update, Vocab.Detail.adjust));
                    break;
                default:
                    throw new AssertionError(msg.getDetail().name());
                }
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    public Vocab.Detail averageSitrepDetails(Vocab.Detail detA, Vocab.Detail detB)
    {
        int worst = 0;
        
        switch(detA)
        {
            case none:
                break;
            case problem:
                worst = 2;
                break;
            case adjust:
                worst = 1;
                break;
            default:
                throw new AssertionError(detA.name());
        }
        
        switch(detA)
        {
            case none:
                break;
            case problem:
                worst = Math.max(worst, 2);
                break;
            case adjust:
                worst = Math.max(worst, 1);
                break;
            default:
                throw new AssertionError(detA.name());
        }
        
        switch(worst)
        {
            case 0:
                return Vocab.Detail.none;
            case 1:
                return Vocab.Detail.adjust;
            case 2:
                return Vocab.Detail.problem;
            default:
                throw new AssertionError(detA.name());
        }
    }

    @Override
    public void sortMessageQueues() {
    }
}
