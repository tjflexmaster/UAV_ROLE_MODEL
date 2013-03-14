/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

/**
 *
 * @author kent
 */
public class Test 
{
    public static void Assert(boolean e)
    {
        if (!e)
        {
            throw new AssertionError();
        }
    }
    
    
}
