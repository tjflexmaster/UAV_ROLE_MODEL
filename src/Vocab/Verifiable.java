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
public interface Verifiable
{
    public void preAssert(Person p, Job tx, Job rx, Date messageCreation, Vocab.Detail detail);
    public void postAssert(Person p, Job tx, Job rx, Date messageCreation, Vocab.Detail detail);
}
