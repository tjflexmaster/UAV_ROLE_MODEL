/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Role.Person;
import java.util.Date;

/**
 *
 * @author kent
 */
public class Message <T>
{
    private Person.Job tx;
    private T message;
    private Vocab.Detail detail;
    private Date creation_time;
    
    public Message(Person.Job tx, T message, Vocab.Detail data)
    {
        this.tx = tx;
        this.message = message;
        this.detail = data;
        creation_time = new Date();
    }

    /**
     * @return the tx
     */
    public Person.Job getTx() {
        return tx;
    }

    /**
     * @return the message
     */
    public T getMessage() {
        return message;
    }
    
    public Date getCreationTime()
    {
        return this.creation_time;
    }
    
    public Vocab.Detail getDetail()
    {
        return detail;
    }
    
    public void inheritDate(Date date)
    {
        this.creation_time = date;
    }
}
