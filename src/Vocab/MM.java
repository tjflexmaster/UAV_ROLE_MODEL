/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vocab;

import Role.Person;
import Role.Person.Job;
import java.util.Date;

/**
 *
 * @author kent
 */
public enum MM implements Verifiable {
    //from multiple
    ready_to_rock, priority_update,
    // from PS
    search_info, kill_search, req_sitrep,
    // from P
    sitrep, /* priority_update, ready_to_rock */
    // from PGUI
    status_details,
    // from video analyst
     req_video_feed, /* priority_update, ready_to_rock */
    // from video gui
    video, annotation,
    // from the environment
    observation;

    @Override
    public void preAssert(Person p, Job tx, Job rx, Date messageCreation, Detail detail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void postAssert(Person p, Job tx, Job rx, Date messageCreation, Detail detail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
