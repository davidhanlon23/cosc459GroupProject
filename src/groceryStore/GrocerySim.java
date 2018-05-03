/*=========================================================
 Grocery Store Queue Simulation
=========================================================
 Project authors: David M. Hanlon, Rojaleen Chhetry,
 Melvin Gaye, and Montrell Jubilee
 COSC 459 - 101
 Started: 04/15/2018
--------------------------------------------------------*/
package groceryStore;

import java.util.*;

public class GrocerySim
{
	
	private PriorityQueue<SimEvent> FEL;
	private Queue<Customer> checkLine; 

	private ArrayList<Customer> customers;
	private int numCustomers;
	private Customer currCust;
	private CheckOut cashier;
	private Random r;
	
	// As in most object-oriented programs, the main here is very simple
	public static void main (String [] args) throws ClassNotFoundException
	{
		int customers = 14;
		
		GrocerySim sim = new GrocerySim(customers);
		sim.runSimulation();
		sim.showResults();
		System.out.println("");
		System.out.println("Grocery Store diagram:");
		System.out.println("Where Customers are denoted by X, Server by S, and the system by the box.");
		System.out.println("\t\t\t----------------------------------------------------");
		System.out.println("Arriving Customer \t| Customers Waiting to be served \t Server \t   | Departing Customer");
		System.out.println("\t X \t\t|\t X X X X \t\t    \t   S \t   |");
		System.out.println("\t\t\t| \t\t\t\t\t\t   |");
		System.out.println("\t\t\t| \t\t\t\t\t\t   |");
		System.out.println("\t\t\t----------------------------------------------------");
	}
	
	public GrocerySim(int n)
	{
		FEL = new PriorityQueue<SimEvent>();

		checkLine = new LinkedList<Customer>();  // The customers waiting to be
		                   // checked out are in a simple queue (FIFO).
		numCustomers = n;
		customers = new ArrayList<Customer>();
		              
		cashier = new CheckOut();
		r = new Random((new Date()).getTime());  // Seed the random number
		                   // generator so output differs 	from run to run.
	}

	public void runSimulation() throws ClassNotFoundException
	{
		int custNum = 0;
		int clock = 0;
		FEL.add(new ArrivalEvent(0));  
		while (!FEL.isEmpty())
		{
			SimEvent e = FEL.remove();
			clock = e.get_e_time();
			if (Class.forName("groceryStore.ArrivalEvent").isInstance(e))
			{
				custNum++;
				currCust = new Customer(custNum, e.get_e_time());
				currCust.serviceT = serviceTime();
				if (!cashier.isBusy())
				{
					currCust.startServiceT = clock;
					cashier.addCust(currCust);
					FEL.add(new CompletionEvent(clock + currCust.serviceT));
				}
				else
				{
					checkLine.add(currCust);
				}
				if (custNum < numCustomers)
					FEL.add(new ArrivalEvent(clock + deltaCustomer()));
			}
			else // Event is a CompletionEvent
			{
				currCust = cashier.removeCust();
				currCust.endServiceT = clock;
				currCust.waitT = clock - currCust.arrivalT - currCust.serviceT;
				currCust.inSystemT = clock - currCust.arrivalT;
				customers.add(currCust);
				if (!checkLine.isEmpty())
				{
					currCust = checkLine.remove();
					currCust.startServiceT = clock;
					cashier.addCust(currCust);
					FEL.add(new CompletionEvent(clock + currCust.serviceT));
				}
			}
		}
	}

	private class CheckOut
	{
		private boolean busy;
		private Customer currentCust;

		public CheckOut()
		{
			busy = false;
			currentCust = null;
		}

		public boolean isBusy()
		{
			return busy;
		}

		public void addCust(Customer c)
		{
			currentCust = c;
			busy = true;
		}

		public Customer removeCust()
		{
			Customer t = currentCust;
			currentCust = null;
			busy = false;
			return t;
		}
	}

	private class Customer
	{
		private int id;
		public int arrivalT, serviceT, startServiceT, waitT, endServiceT, inSystemT;

		public Customer(int newid, int arr)
		{
			id = newid;
			arrivalT = arr;
		}
	}
		
	public void showResults()
	{
		System.out.println("Customer  Time Since  Arrival  Service  Time Service  Time Customer  Time Service   Time Spent  Idle Server");
		System.out.println("   Id        Last      Time     Time       Begins         Waits         Ends          in Sys       Time");
		System.out.println("--------  ----------  -------  -------  ------------  -------------  ------------   ----------  -----------");
		int oldA = 0;
		int oldT = 0;
		int arrTot = 0;
		int servTot = 0;
		int waitTot = 0;
		int sysTot = 0;
		int idleTot = 0;
		int numWait = 0;
		int totalTime = 0;
		float arrivalRate,serviceRate;
		for (int i = 0; i < customers.size(); i++)
		{
			currCust = customers.get(i);
			System.out.print("   " + currCust.id + "\t");
			if (i == 0)
				System.out.print("      " + 0 + "\t\t");
			else
			{
				int deltaA = currCust.arrivalT - oldA;
				System.out.print("      " + deltaA + "\t\t");
				arrTot += deltaA;
			}
			System.out.print(currCust.arrivalT + "\t");
			System.out.print("  " + currCust.serviceT + "\t");
			servTot += currCust.serviceT;
			System.out.print("    " + currCust.startServiceT + "\t\t");
			int currWait = currCust.startServiceT - currCust.arrivalT;
			System.out.print("   " + currWait + "\t");
			waitTot += currWait;
			if (currWait > 0) numWait++;
			System.out.print("\t " + currCust.endServiceT + "\t");
			System.out.print("\t" + (currCust.endServiceT - currCust.arrivalT) + "\t");
			sysTot += currCust.endServiceT - currCust.arrivalT;
			if (i == 0)
				System.out.print("     " + 0 + "\t");
			else
			{
				int idleT = currCust.startServiceT - oldT;
				System.out.print("     " + idleT + " ");
				idleTot += idleT;
			}
			oldT = currCust.endServiceT;
			oldA = currCust.arrivalT;
			if (i == (customers.size()-1))
				totalTime = currCust.endServiceT;
			System.out.println();
		}
		
		arrivalRate = 1/(arrTot/((float) (numCustomers - 1)));
		serviceRate = 1/(servTot/((float) numCustomers));
		
		System.out.println();
		System.out.printf("Arrival rate: %.2f %n", arrivalRate);
		System.out.printf("Service rate: %.2f %n", serviceRate);
		System.out.printf("(P)Server utilization: %.2f %n", (numWait/((float) numCustomers)));
		System.out.printf("(An)Avg. interarrival time: %.2f %n", (arrTot/((float) (numCustomers - 1))));
		System.out.printf("(Sn)Avg. service time: %.2f %n",  (servTot/((float) numCustomers)));
		System.out.println("L(t)Customers in system: " + numCustomers);
		System.out.println("LQ(t)Customers in queue: " + numWait);
		System.out.printf("(L)Avg. num of customers in system: %.2f %n", (sysTot/((float) numCustomers)));
		System.out.printf("(LQ) Avg. num of customers in queue: %.2f %n", (numCustomers/((float) numWait)));
		System.out.printf("(W)Avg. wait in system: %.2f %n", (waitTot/((float) numCustomers)));
		System.out.printf("(WQ)Avg. wait in queue: %.2f %n", (waitTot/((float) numWait)));
	}

	public int deltaCustomer()
	{
		return (r.nextInt(8) + 1);
	}

	public int serviceTime()
	{
		double zeroOne = r.nextDouble();
		if (zeroOne <= 0.1) return 1;
		else if (zeroOne <= 0.30) return 2;
		else if (zeroOne <= 0.60) return 3;
		else if (zeroOne <= 0.85) return 4;
		else if (zeroOne <= 0.95) return 5;
		else return 6;
	}
}