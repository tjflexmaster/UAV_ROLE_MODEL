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
public class VideoAnalyst extends Person<Vocab.VA>
{
    public class State
    {
        
    }
    
    private State state;
    
    private boolean ready;
    
    public VideoAnalyst()
    {
        super(Job.VA);
        ready = false;
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
        Vocab.Detail vid = this.peekAtVideo();
        
        if (vid != null)
        {
            Timing.delay(1, 5);
            
            switch(vid)
            {
                case video_null:
                    break;
                case video_possible_hit:
                case video_hit:
                    getTeam().getVideoGUI().rxMessage(this, new Message<>(Job.VA, Vocab.VGUI.annotate_object, vid));
                    break;
                case video_low_quality:
                    getTeam().getPilot().rxMessage(this, new Message<>(Job.VA, Vocab.P.modify_flight, null));
                    break;
                default:
                    throw new AssertionError(vid.name());
                
            }
            
            this.popTopVideo();
        }
    }
   
    @Override
    protected void MM_Message_Handler(Message<Vocab.VA> msg) 
    {
        switch(msg.getMessage())
        {
            case search_info:
                Timing.delay(1, 5);               
                ready = true;
                break;
            case modify_search:
                Timing.delay(1, 5);
                break;
            case req_sitrep:
                Timing.delay(1, 3);
                getTeam().getMissionManager().rxMessage(this, new Message<>(Job.VA, Vocab.MM.sitrep, Vocab.Detail.none));
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
            
        }
    }

    @Override
    public void sortMessageQueues() {
    }
}
