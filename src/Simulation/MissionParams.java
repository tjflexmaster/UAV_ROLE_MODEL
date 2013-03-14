/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Vocab.Event;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author kent
 */
public class MissionParams extends LinkedList<Vocab.Event>
{
    private int seed;
    private Random rand;
    
    private int    MM;  // the weight, or how often a MM_* Event will occur
    private int    MM_adjust_flight; // 0
    private int    MM_adjust_search; // 1 
    private int    P;  // the weight, or how often a P_* Event will occur
    private int    P_adjust_flight;  // 2
    private int    P_problem;  // 3
    private int    P_subtle; // 4
    private int    UAV;  // the weight, or how often a UAV_* Event will occur
    private int    UAV_adjust_flight_needed; // 5
    private int    UAV_video_null;  // 6
    private int    UAV_video_possible_hit; // 7
    private int    UAV_video_hit; // 8
    private int    UAV_video_low_quality; // 9
    private int    UAV_problem; // 10
    private int    PS;  // the weight, or how often a PS_* Event will occur
    private int    PS_search_info_update; // 11
    private int    PS_need_uav_update; // 12
    
    public static MissionParams test1Params()
    {
        MissionParams p = new MissionParams();
        p.add(Event.PS_search_info_update);
        
        
        // PS observation 1
        // MM search_info 1
        // P search_info 1
        // VA search_info 1
        
        return p;
    }

    public static MissionParams test2Params()
    {
        MissionParams p = new MissionParams();
        p.add(Event.PS_search_info_update);
        p.add(Event.PS_search_info_update);
        p.add(Event.MM_adjust_flight);
        
        // PS observation 2
        // MM search_info 2
        // MM observation 1
        // P search_info 2
        // P modify_flight 2
        // VA search_info 2
        // PGUI take_off 1
        // PGUI modify_flight_path 1
        // UAV take_off 1
        // UAV modify_flight_path 1
        
        return p;
    }
    
    public static MissionParams test3Params()
    {
        MissionParams p = new MissionParams();
        p.add(Event.PS_search_info_update);
        p.add(Event.PS_search_info_update);
        p.add(Event.MM_adjust_flight);
        p.add(Event.MM_adjust_search);
        p.add(Event.PS_need_uav_update);
        p.add(Event.P_adjust_flight);
        p.add(Event.P_problem);
        p.add(Event.P_subtle);
        p.add(Event.UAV_adjust_flight_needed);
        p.add(Event.UAV_problem);
        p.add(Event.UAV_video_hit);
        p.add(Event.UAV_video_low_quality);
        p.add(Event.UAV_video_null);
        p.add(Event.UAV_video_possible_hit);
        
        
        // PS observation 3 
        // PS sitrep 2
        // PS priority_update 1 
        // MM search_info 3
        // MM observation 2
        // MM req_sitrep 1
        // MM annotation 2
        // MM sitrep 4
        // P search_info 4
        // P modify_flight 6
        // P observation 3
        // P req_sitrep 1
        // P status_details 1
        // VA search_info 4
        // VA req_sitrep 1
        // PGUI take_off 1
        // PGUI modify_flight_path 8
        // PGUI req_status_details 1
        // VGUI video 4
        // VGUI annotate_object 2
        // UAV take_off 1
        // UAV observation 5
        // UAV modify_flight_path 8
        
        return p;
    }
    
    public static MissionParams alpha()
    {
        MissionParams p = new MissionParams();
        
        p.seed = 33;
        
        p.MM = 5;
        p.MM_adjust_flight = 1;
        p.MM_adjust_search = 1;
        
        p.P = p.MM;
        p.P_adjust_flight = 20;
        p.P_problem = 1;
        p.P_subtle = 5;
        
        p.UAV = 40;
        p.UAV_adjust_flight_needed = 5;
        p.UAV_video_null = 80;
        p.UAV_video_possible_hit = 5;
        p.UAV_video_hit = 3;
        p.UAV_video_low_quality = 10;
        p.UAV_problem = 1;
        
        p.PS = 4;
        p.PS_search_info_update = 1;
        p.PS_need_uav_update = 3;
         
        p.rand = new Random(p.seed);
        
        p.add(Event.PS_search_info_update);
        p.staggerEventWeights();
        
        p.generate(1000);
        
        return p;
    }
    
    /*
     * Creates a number range for the categories and for each
     * category's events. This way, a number nextInt(last_category)
     * can be used with a simple chain of if (num < category)
     * to index into the range, where each category has their given
     * relative weight, or probability of being selected.
     */
    private void staggerEventWeights()
    {
        MM = MM;
        MM_adjust_flight = MM_adjust_flight;
        MM_adjust_search += MM_adjust_flight;
        
        P += MM;
        P_adjust_flight = P_adjust_flight;
        P_problem += P_adjust_flight;
        P_subtle += P_problem;
        
        UAV += P;
        UAV_adjust_flight_needed = UAV_adjust_flight_needed;
        UAV_video_null += UAV_adjust_flight_needed;
        UAV_video_possible_hit += UAV_video_null;
        UAV_video_hit += UAV_video_possible_hit;
        UAV_video_low_quality += UAV_video_hit;
        UAV_problem += UAV_video_low_quality;
        
        PS += UAV;
        PS_search_info_update = PS_search_info_update;
        PS_need_uav_update += PS_search_info_update;
    }
    
    public Vocab.Event getNextEvent()
    {
        int category = rand.nextInt(PS);
        
        if (category < MM) {
            return next_MM_Event();
        }
        else if (category < P) {
            return next_P_Event();
        }
        else if (category < UAV) {
            return next_UAV_Event();
        }
        else {
            return next_PS_Event();
        }
    }



    private void generate(int i) 
    {
        while(i-- > 0)
        {
            this.add(this.getNextEvent());
        }
    }

    private Event next_MM_Event()
    {
        int event = rand.nextInt(MM_adjust_search);
        
        if (event < MM_adjust_flight) {
            return Vocab.Event.MM_adjust_flight;
        }
        else {
            return Vocab.Event.MM_adjust_search;
        }
    }

    private Event next_P_Event()
    {
        int event = rand.nextInt(P_subtle);
        
        if (event < P_adjust_flight) {
            return Vocab.Event.P_adjust_flight;
        }
        else if (event < P_problem) {
            return Vocab.Event.P_problem;
        }
        else {
            return Vocab.Event.P_subtle;
        }
    }

    private Event next_UAV_Event()
    {
        int event = rand.nextInt(UAV_problem);
        
        if (event < UAV_adjust_flight_needed) {
            return Vocab.Event.UAV_adjust_flight_needed;
        }
        else if (event < UAV_video_null) {
            return Vocab.Event.UAV_video_null;
        }
        else if (event < UAV_video_possible_hit) {
            return Vocab.Event.UAV_video_possible_hit;
        }
        else if (event < UAV_video_hit) {
            return Vocab.Event.UAV_video_hit;
        }
        else if (event < UAV_video_low_quality) {
            return Vocab.Event.UAV_video_low_quality;
        }
        else {
            return Vocab.Event.UAV_problem;
        }
    }

    private Event next_PS_Event() 
    {
        int event = rand.nextInt(PS_need_uav_update);
        
        if (event < PS_search_info_update) {
            return Vocab.Event.MM_adjust_flight;
        }
        else {
            return Vocab.Event.PS_need_uav_update;
        }
    }
}
