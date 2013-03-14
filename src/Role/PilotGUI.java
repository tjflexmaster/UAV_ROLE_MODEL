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
public class PilotGUI  extends Person<Vocab.PGUI>
{

    public PilotGUI()
    {
        super(Job.PGUI);
    }
    
    
    @Override
    protected void P_Message_Handler(Message<Vocab.PGUI> msg) 
    {
        switch(msg.getMessage())
        {
            case take_off:
                getTeam().getUav().rxMessage(this, new Message<>(Job.PGUI, Vocab.UAV.take_off, null));
                break;
            case land:
                getTeam().getUav().rxMessage(this, new Message<>(Job.PGUI, Vocab.UAV.land, null));
                break;
            case kill:
                getTeam().getUav().rxMessage(this, new Message<>(Job.PGUI, Vocab.UAV.kill, null));
                break;
            case modify_flight_path:
                getTeam().getUav().rxMessage(this, new Message<>(Job.PGUI, Vocab.UAV.modify_flight_path, null));
                break;
            case req_status_details:
                if (getTeam().getUav().hasProblem() != null)
                {
                    getTeam().getPilot().rxMessage(this, new Message<>(Job.PGUI, Vocab.P.status_details, Vocab.Detail.problem));
                }
                else if (getTeam().getUav().getNeedsAdjustment() != null)
                {
                    getTeam().getPilot().rxMessage(this, new Message<>(Job.PGUI, Vocab.P.status_details, Vocab.Detail.adjust));
                }
                else
                {
                    getTeam().getPilot().rxMessage(this, new Message<>(Job.PGUI, Vocab.P.status_details, Vocab.Detail.none));
                }
                break;
            case toggle_gimbal:
                // TODO ?
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
