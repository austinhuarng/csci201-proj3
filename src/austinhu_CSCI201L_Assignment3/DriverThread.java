package austinhu_CSCI201L_Assignment3;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import austinhu_CSCI201L_Assignment3.Schedule.Task;
import util.TimestampUtil;
import java.lang.*;

public class DriverThread extends Thread{
	public static double userLatitude;
	public static double userLongitude;
	public static int numDrivers;
	private static Schedule schedule;
	private static Scanner sc;
	String hostname;
	static int port;
	public static Vector<Task> readyorders = new Vector<Task>();
	public static Vector<Restaurant> yelpvector = new Vector<Restaurant>(); 
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public static boolean isfree;

	Socket s;
	public DriverThread() {
		System.out.println("Welcome to SalEats v2.0!");
		System.out.print("Enter the server hostname: ");
		sc = new Scanner(System.in);
		isfree = true;
		hostname = "localhost";//sc.nextLine();
		System.out.print("Enter the server port: ");
		port = 3456;//Integer.parseInt(sc.nextLine());
		try {
			s = new Socket(hostname, port);
			//
			//br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			//pw = new PrintWriter(s.getOutputStream());
			//
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient constructor: " + ioe.getMessage());
		}
	}
	
	//still need to get the yelp data vector from client handler
	public Task getNextClosestTask(Vector<Task> pending, double currlat, double currlong){
		double min = 999999;
		Task t = null;
		for(Task task : pending){
			for(Restaurant restaurant : yelpvector){
				if(task.getRestaurant().equals(restaurant.getName())){
					restaurant.setDistance(currlat, currlong);
					if(restaurant.getDistance() < min){
						min = restaurant.getDistance();
						t = task;
					}
				}
			}
		}
		return t;
	}

	public void run() {
			try{
				while(true){
					String line = (String) ois.readObject();
					System.out.println(line);
					if((int)line.charAt(0) == 65){
						break;
					}
				}
				System.err.println("gotten past all arrived");
//				while(true){
//					System.out.println("entered while true 2");
//					//STILL NEED TO SEND IN YELP DATA VECTOR
//					Message<?> message = (Message<?>) ois.readObject();
//					System.out.println("ehehehhe" + message.type);
//					//save all static data
//					if(message.type.equals("latitude"))
//						userLatitude = (Double)message.T;
//					else if(message.type.equals("longitude"))
//						userLongitude = (Double)message.T;
//					else if(message.type.equals("numDrivers"))
//						numDrivers = (Integer)message.T;
//					else if(message.type.equals("yelpvec")){
//						yelpvector = (Vector<Restaurant>)message.T;
//					}
//					System.err.println("successfully send lat " + userLatitude);
//					System.err.println("successfully send lng " + userLongitude);
//					System.err.println("successfully send ND " + numDrivers);
//					if(!yelpvector.isEmpty() && yelpvector!= null){
//						break;
//					}
////					if(!yelpvector.isEmpty()){
////						for(int i=0; i<yelpvector.size(); i++){
////							System.err.println(yelpvector.get(i).getName());
////						}
////					}
//				}
				while(true){
					String line = (String) ois.readObject();
					System.out.println(line);
					if(line.equals("ohno")){
						System.out.println("[" + TimestampUtil.getTimestamp() + "]" + "All orders completed!");
						System.exit(0);
					}
				}
					
					
//					double currlat = userLatitude;
//					double currlong = userLongitude;
//					//Boolean isfree;
//					if(isfree == true){
//						System.err.println("trying to writeobject twice");
//						oos.writeObject("takeorder");
//						oos.flush();
//						System.err.println("wrote to     writeobject twice");
//						
//						Message <?> message = (Message<?>)ois.readObject();
//						readyorders = (Vector<Task>)message.T;
//						isfree = false;
//					}
//					//else continue;
////					for(Restaurant restaurant : yelpvector){
////						restaurant.setDistance(userLatitude, userLongitude);
////					}
////					if(!readyorders.isEmpty()){
////						for(Task task: readyorders){
////							System.out.println("[" + TimestampUtil.getTimestamp() + "] Starting delivery of " + task.getFood() + " from " + task.getRestaurant() + ".");
////						}
////					}
//					while(!readyorders.isEmpty()){
//						for(Task task: readyorders){
//							System.out.println("[" + TimestampUtil.getTimestamp() + "] Starting delivery of " + task.getFood() + " from " + task.getRestaurant() + ".");
//						}
//						Task nextclosest = getNextClosestTask(readyorders, currlat, currlong);
//						double updist = 0;
//						for(Restaurant res : yelpvector){
//							if(res.getName().equals(nextclosest.getRestaurant())){
//								updist = res.getDistance();
//								currlat = res.getLatitude();
//								currlong = res.getLongitude();
//							}
//						}
//						System.out.println(updist + "      updist   ");
//						Thread.sleep((long)updist);
//						System.out.println("[" + TimestampUtil.getTimestamp() + "] Finished delivery of " + nextclosest.getFood() + " from " + nextclosest.getRestaurant() + ".");
//						readyorders.remove(nextclosest);
//						Vector<Task> temp = new Vector<Task>();
//						for(Task t:readyorders){
//							if(t.getRestaurant().equals(nextclosest.getRestaurant())){
//								System.out.println("[" + TimestampUtil.getTimestamp() + "] Finished delivery of " + t.getFood() + " from " + t.getRestaurant() + ".");
//								//readyorders.remove(t);
//							}
//							else temp.add(t);
//						}
//						readyorders.clear();
//						readyorders.addAll(temp);
//						if(!readyorders.isEmpty()){
//							for(Task continuedeliv : readyorders){
//								System.out.println("[" + TimestampUtil.getTimestamp() + "] Continuing delivery to " + continuedeliv.getRestaurant() + ".");
//								continue;
//							}
//						}
//						else if(readyorders.isEmpty()){
//							System.out.println("[" + TimestampUtil.getTimestamp() + "] Finished all deliveries, returning to HQ.");
//							double gohome = calculateDistance(currlat, currlong, userLatitude, userLongitude);
//							Thread.sleep((long)gohome);
//							System.out.println("[" + TimestampUtil.getTimestamp() + "] Returned to HQ.");
//							isfree = true;
//							break;
//						}
//						System.err.println("got to second break");
//					}
//				}
//					isfree = true;
//					oos.writeBoolean(isfree);
//					oos.flush();
			}
			catch (IOException | ClassNotFoundException ioe) {
				ioe.printStackTrace();
				System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
			}
	}
	
	public double calculateDistance(double latitude, double longitude, double userLatitude, double userLongitude){
		double distance = 3963.0 * Math.acos((Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(userLatitude)))
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(userLatitude))
                * Math.cos(Math.toRadians(longitude) - Math.toRadians(userLongitude)));
		return distance;
	}
	
	public static void main(String [] args) {
		DriverThread dt = new DriverThread();
	}
}