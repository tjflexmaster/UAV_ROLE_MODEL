/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Util.Message;

/**
 *
 * @author kent
 */
public class VideoGUI extends Person<Vocab.VGUI>
{
    public class State
    {
        
    }
    
    private State state;
    
    
    public VideoGUI()
    {
        super(Job.VGUI);
    }
    
     
    @Override
    protected void UAV_Message_Handler(Message<Vocab.VGUI> msg) 
    {
        switch(msg.getMessage())
        {
            case video:
                getTeam().getMissionManager().rxVideo(this, msg.getDetail());
                getTeam().getVideoAnalyst().rxVideo(this, msg.getDetail());
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }
    
    @Override
    protected void VA_Message_Handler(Message<Vocab.VGUI> msg)
    {
        switch(msg.getMessage())
        {
            case video_command:
                // does nothing in the FSM model...
                break;
            case annotate_object:
                getTeam().getMissionManager().rxMessage(this, 
                        new Message<>(Job.VGUI, Vocab.MM.annotation, msg.getDetail()));
                break;
            default:
                throw new AssertionError(msg.getMessage().name());
            
        }
    }

    @Override
    public void sortMessageQueues() {
    }

    @Override
    protected void doTimeRelatedTasksMaybe() {
    }
}
