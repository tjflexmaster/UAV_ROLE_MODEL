/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Util.Message;
import Util.Timing;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kent
 */
public class Environment extends Person<Vocab.Detail>
{

    Queue<Vocab.Event> envQueue; 
    
    public Environment(Queue<Vocab.Event> envQueue)
    {
        super(Job.ENV);
        this.envQueue = envQueue;
    }

    @Override
    public void run()
    {
        
        while(!envQueue.isEmpty())
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.sendMessage(envQueue.peek());
            
            envQueue.remove();
        }
        
        this.passTerminationToken();
    }

    private void sendMessage(Vocab.Event next)
    {
        switch(next)
        {
            case MM_adjust_flight:
                this.getTeam().getMissionManager().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.MM.observation, 
                        Vocab.Detail.adjust_flight));
                break;
            case MM_adjust_search:
                this.getTeam().getMissionManager().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.MM.observation, 
                        Vocab.Detail.adjust_search));
                break;
            case P_adjust_flight:
                this.getTeam().getPilot().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.P.observation, 
                        Vocab.Detail.adjust_flight));
                break;
            case P_problem:
                this.getTeam().getPilot().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.P.observation, 
                        Vocab.Detail.problem));
                break;
            case P_subtle:
                this.getTeam().getPilot().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.P.observation, 
                        Vocab.Detail.subtle));
                break;
            case UAV_adjust_flight_needed:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.adjust_flight));
                break;
            case UAV_video_null:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.video_null));
                break;
            case UAV_video_possible_hit:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.video_possible_hit));
                break;
            case UAV_video_hit:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.video_hit));
                break;
            case UAV_video_low_quality:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.video_low_quality));
                break;
            case UAV_problem:
                this.getTeam().getUav().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.UAV.observation, 
                        Vocab.Detail.problem));
                break;
            case PS_search_info_update:
                this.getTeam().getParentSearch().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.PS.observation, 
                        Vocab.Detail.adjust_search));
                break;
            case PS_need_uav_update:
                this.getTeam().getParentSearch().rxMessage(this, 
                        new Message<>(
                        Job.ENV, 
                        Vocab.PS.observation, 
                        Vocab.Detail.update_status));
                break;
            default:
                throw new AssertionError(next.name());
        }
    }

    @Override
    public void sortMessageQueues() {
    }

    @Override
    protected void doTimeRelatedTasksMaybe() {
    }
    
}
