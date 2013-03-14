/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Util.Message;
import Util.Timing;
import Vocab.MM;

/**
 *
 * @author kent
 */
public class ParentSearch extends Person<Vocab.PS>
{
    public class State
    {
        
    }
    
    private State state;
    
    public ParentSearch()
    {
        super(Job.PS);
        
    }
    
    @Override
    protected void doTimeRelatedTasksMaybe()
    {
        Message<Vocab.PS> observation = this.peekAtObservations();
        
        if(observation != null)
        {
            switch(observation.getDetail())
            {
                case adjust_search:
                    this.getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(
                            Job.PS, 
                            Vocab.MM.search_info, 
                            Vocab.Detail.adjust_search));
                    break;
                case update_status:
                    this.getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(
                            Job.PS, 
                            Vocab.MM.req_sitrep, 
                            Vocab.Detail.update_status));
                    break;
                default:
                    throw new AssertionError(observation.getDetail().name());
                
            }
            
            this.popTopObservation();
        }
    }
    
    @Override
    protected void MM_Message_Handler(Message<Vocab.PS> msg)
    {
        switch(msg.getMessage())
        {
            case search_done:
                //@TODO end simulation?
                break;
            case priority_update:
                Timing.delay(1, 10);
                    
                if(msg.getDetail().equals(Vocab.Detail.problem))
                {
                    this.getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(
                                    Job.PS, 
                                    MM.search_info, 
                                    Vocab.Detail.adjust_search));
                }
                
                break;
            case sitrep:
                Timing.delay(1, 10);
                
                if(msg.getDetail().equals(Vocab.Detail.problem))
                {
                    this.getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(Job.PS, MM.search_info, null));
                }
                else if(msg.getDetail().equals(Vocab.Detail.adjust))
                {
                    this.getTeam().getMissionManager().rxMessage(this, 
                            new Message<>(Job.PS, MM.search_info, null));
                }
                
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
        }
    }

    @Override
    public void sortMessageQueues() {
    }
}
