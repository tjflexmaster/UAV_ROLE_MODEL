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
public enum P implements Verifiable
{
    search_info, modify_flight, req_sitrep,
    observation, status_details, land;

    @Override
    public void preAssert(Person p, Job tx, Job rx, Date messageCreation, Detail detail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void postAssert(Person p, Job tx, Job rx, Date messageCreation, Detail detail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
