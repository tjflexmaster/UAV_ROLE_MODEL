/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Role;

import Model.Team;
import Util.Message;
import Util.Test;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kent
 */
public abstract class Person<T> extends Thread
{

    /**
     * @return the myJob
     */
    public Job getMyJob() {
        return myJob;
    }
    public enum Job
    {
        MM, P, PGUI, PS, UAV, VA, VGUI, ENV
    }
    
    private Queue<Message<T>> observationQueue;
    private java.util.concurrent.ConcurrentLinkedQueue<Vocab.Detail> videoQueue;
    private java.util.concurrent.ConcurrentLinkedQueue<Message<T>> queue;
    private Team team;
    private Map<T, Integer> debugLog;
    private Job myJob;
    private AtomicInteger myNetMessageEffect;
    private Integer passedNetMessageEffectOrNull;
    
    
    public Person(Job myJob)
    {
        this.myJob = myJob;
        
        queue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        observationQueue = new LinkedList<>();
        videoQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        debugLog = new java.util.HashMap<>();
        
        myNetMessageEffect = new AtomicInteger();
        myNetMessageEffect.set(0);
        passedNetMessageEffectOrNull = null;
    }
    
    @Override
    public void run()
    { 
        missionLoop();
        
    }
    
    private void missionLoop()
    {
        Message<T> msg;
        

        
        while(getTeam().isMissionActive())
        {
            if (passedNetMessageEffectOrNull != null &&
                    !this.hasMessages())
            {
                this.passTerminationToken();
            }
            

            this.sortMessageQueues();
            
            this.doTimeRelatedTasksMaybe();
            
            if (queue.isEmpty())
            {
                continue;
            }
            
            msg = this.queue.peek();
            
            if(Team.DEBUG)
            {
                if (!debugLog.containsKey(msg.getMessage()))
                {
                    debugLog.put(msg.getMessage(), 0);
                }
                
                debugLog.put(msg.getMessage(), debugLog.get(msg.getMessage()) + 1);
            }
            

            //Verifiable check = (Verifiable) msg.getMessage();
            
            //check.preAssert(this, msg.getTx(), this.getMyJob(), msg.getCreationTime(), msg.getDetail());
            
            switch(msg.getTx())
            {
                case MM:
                    MM_Message_Handler(msg);
                    break;
                case P:
                    P_Message_Handler(msg);
                    break;
                case PGUI:
                    PGUI_Message_Handler(msg);
                    break;
                case PS:
                    PS_Message_Handler(msg);
                    break;
                case UAV:
                    UAV_Message_Handler(msg);
                    break;
                case VA:
                    VA_Message_Handler(msg);
                    break;
                case VGUI:
                    VGUI_Message_Handler(msg);
                    break;
                case ENV:
                    recordTx();
                    observationQueue.add(msg);
                    break;
                default:
                    throw new AssertionError(msg.getTx().name());
            }
            
            this.queue.remove();
            recordRx();
            
            //check.postAssert(this, msg.getTx(), this.getMyJob(), msg.getCreationTime(), msg.getDetail());
            
        }
    }
    
    public abstract void sortMessageQueues();

    protected abstract void doTimeRelatedTasksMaybe();
    
    public void rxMessage(Person tx, Message<T> msg)
    {
        tx.recordTx();
        Test.Assert(queue.add(msg));
    }
     
    protected Message<T> peekAtObservations()
    {
        return observationQueue.peek();
    }
    
    protected void popTopObservation()
    {
        observationQueue.remove();
        recordRx();
    }
    
    protected final Vocab.Detail peekAtVideo()
    {
            return videoQueue.peek();
    }
    
    protected final void popTopVideo()
    {
        videoQueue.remove();
         recordRx();
    }
    
    public void rxVideo(Person tx, Vocab.Detail vid)
    {
        tx.recordTx();
        
        switch(vid)
        {
            case video_null:
            case video_possible_hit:
            case video_hit:
            case video_low_quality:
                videoQueue.add(vid);
                break;
            default:
                throw new AssertionError(vid.name());
            
        }
    }

    public void recordTx()
    {
        this.myNetMessageEffect.incrementAndGet();
    }
    
    public void recordRx()
    {
        this.myNetMessageEffect.decrementAndGet();
    }
    
    public void acceptTerminationToken(int netMessageCountThusFar)
    {
        this.passedNetMessageEffectOrNull = netMessageCountThusFar;
        
        if (myJob.equals(Job.ENV))
        {
            if (netMessageCountThusFar == 0)
            {
                getTeam().setIsMissionActive(false);
            }
            else
            {
                this.passTerminationToken();
            }
        } 
    }
    
    public void passTerminationToken()
    {   
        
        if (this.myJob.equals(Job.ENV)) {
            passedNetMessageEffectOrNull = 0;
        }
        
        passedNetMessageEffectOrNull += myNetMessageEffect.intValue();
        Person rx = null;
        
        switch(this.myJob)
        {
            case MM:
                rx = getTeam().getPilot();
                break;
            case P:
                rx = getTeam().getPilotGUI();
                break;
            case PGUI:
                rx = getTeam().getParentSearch();
                break;
            case PS:
                rx = getTeam().getUav();
                break;
            case UAV:
                rx = getTeam().getVideoAnalyst();
                break;
            case VA:
                rx = getTeam().getVideoGUI();
                break;
            case VGUI:
                rx = getTeam().getEnvironment();
                break;
            case ENV:
                rx = getTeam().getMissionManager();
                break;
            default:
                throw new AssertionError(this.myJob.name());
        }
        
        rx.acceptTerminationToken(passedNetMessageEffectOrNull);
        
        passedNetMessageEffectOrNull = null;
    }
    
    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public java.util.Map<T, Integer> getDebugInfo()
    {
        return Collections.unmodifiableMap(debugLog);
    }
    
    public boolean hasMessages()
    {
        return !this.observationQueue.isEmpty() ||
                !this.queue.isEmpty() ||
                !this.videoQueue.isEmpty();
    }
    

    protected void MM_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void P_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void PGUI_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void PS_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void UAV_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void VA_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

    protected void VGUI_Message_Handler(Message<T> msg) {
        throw new UnsupportedOperationException("Cannot RX from this source!");
    }

}
