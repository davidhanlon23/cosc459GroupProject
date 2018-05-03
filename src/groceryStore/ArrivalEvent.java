/*=========================================================
 Grocery Store Queue Simulation
=========================================================
 Project authors: David M. Hanlon, Rojaleen Chhetry,
 Melvin Gaye, and Montrell Jubilee
 COSC 459 - 101
 Started: 04/15/2018
--------------------------------------------------------*/
package groceryStore;
public class ArrivalEvent extends SimEvent
{
	public ArrivalEvent() {}
	
	public ArrivalEvent(int new_time)
	{
		e_time = new_time;
	}
}