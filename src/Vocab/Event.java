/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vocab;

/**
 * Events from the environment that drive things forward.
 * @author kent
 */
public enum Event {
    MM_adjust_flight, // 0
    MM_adjust_search, // 1 
    P_adjust_flight,  // 2
    P_problem,  // 3
    P_subtle, // 4
    UAV_adjust_flight_needed, // 5
    UAV_video_null,  // 6
    UAV_video_possible_hit, // 7
    UAV_video_hit, // 8
    UAV_video_low_quality, // 9
    UAV_problem, // 10
    PS_search_info_update, // 11
    PS_need_uav_update // 12
}
