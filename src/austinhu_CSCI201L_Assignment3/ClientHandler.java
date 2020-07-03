package austinhu_CSCI201L_Assignment3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.border.EmptyBorder;

import austinhu_CSCI201L_Assignment3.Schedule.Task;
import parser.ScheduleParser;
import parser.YelpAPI;
import util.ScheduleFormatException;
import util.TimestampUtil;

public class ClientHandler implements Serializable{
	private static Scanner scan;
	public static Schedule schedule;
	public static double userLatitude = 34.02116;
	public static double userLongitude = -118.287132;
	public static int numDrivers;
	public static int totalDrivers = -1;
	public static Vector<Restaurant> yelpvec;
	public ObjectInputStream ois;
	public static Vector <Task> todo;// = new Vector<Task>();
//	public static long startTime = System.currentTimeMillis();
//	public static long elapsedTime = 0;
	//public Message<Vector<Task>> messagevec;

	//Client Handler
	private Vector<ServerThread> serverThreads;
	public ClientHandler(int port) {
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Listening on port " + port);
			serverThreads = new Vector<ServerThread>();
			System.out.println("Waiting for drivers...");
			Socket s = null;
			while(true) {
				//if(numDrivers > 0){
				s = ss.accept(); // blocking
				System.out.println("Connection from: " + s.getInetAddress());
				numDrivers--;
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
				if(numDrivers == 0){
					//send a vector of pending orders to drivers
					long startTime = System.currentTimeMillis();
					long elapsedTime = 0;
					int iteration = 0;
					int tempsize = 0;
					int nextTaskIndex = 0;
					Vector <Task> todo = null;//= new Vector<Task>();
					ServerThread temp = null;
					while(true){
						System.out.println("size of serverthreass:    " + serverThreads.size());
						for(ServerThread runthread : serverThreads){
							System.out.println("value of Thread bool:    " + Boolean.toString(runthread.free));
						}
						for(ServerThread runthread : serverThreads){
							System.out.println("Thread: " + Boolean.toString(runthread.free));
							if(runthread.free == true){
								runthread.free = false;
								temp = runthread;
								break;
							}
						}
						if(iteration == 0){
							todo = new Vector<Task>();
						}
						for(int i=0; i<schedule.getTaskList().size(); i++){
							if((elapsedTime / 1000) >= schedule.getTaskList().get(i).getTime()){
								System.err.println("somethign in todo");
								todo.add(schedule.getTaskList().get(i));
								//schedule.getTaskList().remove(i);
								tempsize++;
							}
						}
						elapsedTime = System.currentTimeMillis() - startTime;
						for(Task task : todo){
							schedule.getTaskList().remove(task);
						}
						if(temp != null && !todo.isEmpty()){
							temp.logic(todo);
							//logic(todo, temp);
							todo.clear();
							iteration = 0;
							temp.free = true;
							temp = null;
							continue;
						}
						else iteration++;
//						if(todo.isEmpty() && iteration > 0){
//							temp.free = true;
//						}
						if(tempsize == schedule.getTaskList().size()){
							System.out.println("finished all orders");
							break;
						}
						if(schedule.getTaskList().isEmpty()){
							ServerThread.programover = true;
							break;
						}
						//elapsedTime = System.currentTimeMillis() - startTime;
					}
					if(ServerThread.programover == true){
						System.exit(0);
					}
				}
			}
		} catch (IOException | InterruptedException ioe) {
			ioe.printStackTrace();
			//System.out.println("ioe in ChatRoom constructor: " + ioe.getMessage());
		}
	}
	
	public void broadcastDrivers(String message, ServerThread st) {
		if (message != null) {
			if(numDrivers > 0)
				System.out.println("Waiting for " + numDrivers + " more driver(s)...");
			for(ServerThread threads : serverThreads) {
				threads.sendMessage(message);
			}
		}
	}
	public void logic() throws IOException, InterruptedException{
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		int iteration = 0;
		int tempsize = 0;
		int nextTaskIndex = 0;
		Vector <Task> todo = null;//= new Vector<Task>();
		ServerThread temp = null;
		while(true){
			System.out.println("size of serverthreass:    " + serverThreads.size());
			for(ServerThread runthread : serverThreads){
				System.out.println("value of Thread bool:    " + Boolean.toString(runthread.free));
			}
			for(ServerThread runthread : serverThreads){
				System.out.println("Thread: " + Boolean.toString(runthread.free));
				if(runthread.free == true){
					temp = runthread;
					break;
				}
			}
			if(iteration == 0){
				todo = new Vector<Task>();
			}
			for(int i=0; i<schedule.getTaskList().size(); i++){
				if((elapsedTime / 1000) >= schedule.getTaskList().get(i).getTime()){
					System.err.println("somethign in todo");
					todo.add(schedule.getTaskList().get(i));
					//schedule.getTaskList().remove(i);
					tempsize++;
				}
			}
			for(Task task : todo){
				schedule.getTaskList().remove(task);
			}
			if(temp != null && !todo.isEmpty()){
				temp.logic(todo);
				//logic(todo, temp);
				todo.clear();
				iteration = 0;
				continue;
			}
			else iteration++;
			if(tempsize == schedule.getTaskList().size()){
				System.out.println("finished all orders");
				break;
			}
			elapsedTime = System.currentTimeMillis() - startTime;
		}
	}
	public void sendVector(ServerThread st) throws IOException{
		Message<Vector<Task>> messagevec = new Message<Vector<Task>>("todo", todo);
		//Message<ConcurrentLinkedQueue<Task>> messagevec = new Message<ConcurrentLinkedQueue<Task>>("todo", todoqueue);
		st.sendObject(messagevec);
		todo.clear();
	}
	
	public void broadcastData(double userLat, double userLong, Vector<Restaurant> yelpvec) throws IOException{
		for(ServerThread s : serverThreads){
			//send latitude to client
			Message<Double> lat = new Message<Double>("latitude", userLatitude);
			s.sendObject(lat);
			//send longitude to client
			Message<Double> lng = new Message<Double>("longitude", userLongitude);
			s.sendObject(lng);
			//send # drivers
			Message<Integer>nd = new Message<Integer>("numDrivers", totalDrivers);
			s.sendObject(nd);
			//send yelp data
			Message<Vector<Restaurant>> yp = new Message<Vector<Restaurant>>("yelpvec", yelpvec);
			s.sendObject(yp);
		}
	}
	//end client handler

	public static void main(String[] args) throws InterruptedException, IOException {
		scan = new Scanner(System.in);
		loadScheduleFile();
		storeYelpData();
		//getUserLocation();
		getDriverCount();
//		for(Restaurant restaurant : yelpvec){
//			System.err.println(restaurant.getName() + " ::: " + restaurant.getLatitude() + " ::: " + restaurant.getLongitude());
//		}
		ClientHandler ch = new ClientHandler(3456);
		scan.close();
	}

	private static void loadScheduleFile() {
		while(true){
			System.out.println("What is the name of the schedule file?");
			String fileName = scan.nextLine();
			System.out.println(); // Print blank line for spacing
			try {
				schedule = ScheduleParser.loadSchedule(fileName);
				System.out.println("The schedule file has been properly read.");
				break;
			} catch (ScheduleFormatException e) {
				System.out.println("That file is not properly formatted.");
			} catch (IOException e) {
				System.out.println("This file does not exist!\n");
			}
		}
	}

	private static void getUserLocation() {
		userLatitude = getLatitude("What is your latitude?");
		userLongitude = getLongitude("What is your longitude?");
	}

	private static double getLatitude(String query) {
		double latitude = 0.0;
		while (true) {
			System.out.println(query);
			String latitudeString = scan.nextLine();
			System.out.println(); // Blank line for spacing
			try {
				latitude = Double.parseDouble(latitudeString);
				if (latitude < -90.0 || latitude > 90.0) {
					throw new NumberFormatException();
				}
				return latitude;
			} catch (NumberFormatException ignore) { }
		}
	}

	private static double getLongitude(String query) {
		double longitude = 0.0;
		while (true) {
			System.out.println(query);
			String longitudeString = scan.nextLine();
			System.out.println(); // Blank line for spacing
			try {
				longitude = Double.parseDouble(longitudeString);
				if (longitude < -180.0 || longitude > 180.0) {
					throw new NumberFormatException();
				}
				return longitude;
			} catch (NumberFormatException ignore) { }
		}
	}

	private static void getDriverCount() {
		numDrivers= getDrivers("How many drivers will be in service today?");
	}

	private static int getDrivers(String query) {
		while (true) {
			System.out.println(query);
			String driverString = scan.nextLine();
			System.out.println(); // Blank line for spacing
			try {
				numDrivers = Integer.parseInt(driverString);
				if (numDrivers <= 0) {
					throw new NumberFormatException();
				}
				if(totalDrivers == -1)
					totalDrivers = numDrivers;
				return numDrivers;
			} catch (NumberFormatException ignore) {
				System.out.println("Invalid driver count.");
			}
		}
	}

	private static void storeYelpData() throws IOException{
		yelpvec = new Vector<Restaurant>();
		boolean done;
		for(Task t : schedule.getTaskList()){
			done = false;
			if(yelpvec.size() == 0){
				yelpvec.add(YelpAPI.call(t.getRestaurant(), userLatitude, userLongitude));
				continue;
			}
			for(Restaurant r : yelpvec){
				String restoname = t.getRestaurant();
				if(restoname.equals(r.getName())){
					done = true;
					break;
				}
			}
			if(done == false){
				yelpvec.add(YelpAPI.call(t.getRestaurant(), userLatitude, userLongitude));
			}
		}

	}


}