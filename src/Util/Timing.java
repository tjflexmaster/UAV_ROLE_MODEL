/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kent
 */
public class Timing 
{
    private static boolean withTiming;
    private static Random random;
    
    public static void setTiming(boolean value)
    {
        withTiming = value;
        if (value)
        {
            random = new Random();
        }
    }
    
    /**
     * Delays if setTiming(true) somewhere in the
     * given range of seconds.
     * 
     * @param minMS
     * @param maxMS 
     */
    public static void delay(double minSec, double maxSec)
    {
        if(withTiming)
        {
            try
            {
                Thread.sleep(
                        (int) (random.nextInt((int)((maxSec - minSec) * 1000)) 
                                + minSec * 1000));
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Timing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
