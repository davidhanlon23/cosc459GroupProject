/*=========================================================
 Grocery Store Queue Simulation
=========================================================
 Project authors: David M. Hanlon, Rojaleen Chhetry,
 Melvin Gaye, and Montrell Jubilee
 COSC 459 - 101
 Started: 04/15/2018
--------------------------------------------------------*/
package groceryStore;
public abstract class SimEvent implements Comparable<SimEvent>
{
	protected int e_time;

	public int get_e_time()
	{
		return e_time;
	}

	public int compareTo(SimEvent rhs)
	{
		if (e_time < rhs.e_time)
			return -1;
		else if (e_time == rhs.e_time)
			return 0;
		else
			return 1;
	}
}
