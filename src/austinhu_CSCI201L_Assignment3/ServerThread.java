package austinhu_CSCI201L_Assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import austinhu_CSCI201L_Assignment3.Schedule.Task;
import util.TimestampUtil;

public class ServerThread extends Thread{

	private PrintWriter pw;
	private BufferedReader br;
	private ClientHandler ch;
	public boolean isfirst;
	public static int currDrivers;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
	public Socket socket;
	boolean free;
	public static boolean programover;
	public ServerThread(Socket s, ClientHandler ch) {
		try {
			this.ch = ch;
			isfirst = false;
			this.socket = s;
			this.free = true;
			programover = false;

			ois = new ObjectInputStream(s.getInputStream());
			oos  = new ObjectOutputStream(s.getOutputStream());
			//
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}

	public void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendObject(Message<?> message){
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean readinput(){
		try {
			free = ois.readBoolean();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(free == true)
			return true;
		else return false;
	}
	
	//........
	public Task getNextClosestTask(Vector<Task> pending, double currlat, double currlong){
		double min = 999999;
		Task t = null;
		for(Task task : pending){
			for(Restaurant restaurant : ClientHandler.yelpvec){
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
	
	public void logic(Vector<Task>readyorders) throws InterruptedException{
		this.free = false;
		double currlat = ClientHandler.userLatitude;
		double currlong = ClientHandler.userLongitude;
		if(!readyorders.isEmpty()){
			for(Task task: readyorders){
				sendMessage("[" + TimestampUtil.getTimestamp() + "] Starting delivery of " + task.getFood() + " from " + task.getRestaurant() + ".");
			}
			while(!readyorders.isEmpty()){
				Task nextclosest = getNextClosestTask(readyorders, currlat, currlong);
				double updist = 0.000;
				for(Restaurant res : ClientHandler.yelpvec){
					if(res.getName().equals(nextclosest.getRestaurant())){
						updist = res.getDistance();
						currlat = res.getLatitude();
						currlong = res.getLongitude();
						System.err.println(updist + "   updist for " + res.getName());
	
					}
				}
				sendMessage("wait for " + updist*1000 + " time");
				System.out.println(updist + "      updist   ");
				ServerThread.sleep((long)(updist * 1000.0));
				sendMessage("[" + TimestampUtil.getTimestamp() + "] Finished delivery of " + nextclosest.getFood() + " from " + nextclosest.getRestaurant() + ".");
				readyorders.remove(nextclosest);
				Vector<Task> temp = new Vector<Task>();
				for(Task t:readyorders){
					if(t.getRestaurant().equals(nextclosest.getRestaurant())){
						sendMessage("[" + TimestampUtil.getTimestamp() + "] Finished delivery of " + t.getFood() + " from " + t.getRestaurant() + ".");
						//readyorders.remove(t);
					}
					else temp.add(t);
				}
				readyorders.clear();
				readyorders.addAll(temp);
				if(!readyorders.isEmpty()){
					for(Task continuedeliv : readyorders){
						sendMessage("[" + TimestampUtil.getTimestamp() + "] Continuing delivery to " + continuedeliv.getRestaurant() + ".");
						continue;
					}
				}
				else if(readyorders.isEmpty()){
					sendMessage("[" + TimestampUtil.getTimestamp() + "] Finished all deliveries, returning to HQ.");
					double gohome = calculateDistance(currlat, currlong, ClientHandler.userLatitude, ClientHandler.userLongitude);
					ServerThread.sleep((long)(gohome * 1000.0));
					sendMessage("[" + TimestampUtil.getTimestamp() + "] Returned to HQ.");
					this.free = true;
					break;
				}
				System.err.println("got to second break");
			}
		}
	}
	
	public double calculateDistance(double latitude, double longitude, double userLatitude, double userLongitude){
		double distance = 3963.0 * Math.acos((Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(userLatitude)))
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(userLatitude))
                * Math.cos(Math.toRadians(longitude) - Math.toRadians(userLongitude)));
		return distance;
	}
	//....

	public void run() {
		String line;
		currDrivers = ClientHandler.numDrivers;
//		
//		int totaldriv = ClientHandler.totalDrivers;
//		if(totaldriv == currDrivers){
//			isfirst = true;
//		}
//			while(true){
//				Message<Double> lat = new Message<Double>("latitude", ClientHandler.userLatitude);
//				sendObject(lat);
//				//send longitude to client
//				Message<Double> lng = new Message<Double>("longitude", ClientHandler.userLongitude);
//				sendObject(lng);
//				//send # drivers
//				Message<Integer>nd = new Message<Integer>("numDrivers", ClientHandler.totalDrivers);
//				sendObject(nd);
//				//send yelp data
//				Message<Vector<Restaurant>> yp = new Message<Vector<Restaurant>>("yelpvec", ClientHandler.yelpvec);
//				sendObject(yp);
//				String str;
//				if(ClientHandler.numDrivers >0) str = ClientHandler.numDrivers + " more driver(s) is needed before the service can begin. Waiting...";
//				else str = "All drivers have arrived! Starting service.";
//				ch.broadcastDrivers(str, this);
//			}
		

		if(currDrivers > 0)
			line = currDrivers + " more driver(s) is needed before the service can begin. Waiting...";
		else
			line = "All drivers have arrived! Starting service.";
		ch.broadcastDrivers(line, this);
		if(currDrivers == 0){
			//ch.broadcastData(ClientHandler.userLatitude, ClientHandler.userLongitude, ClientHandler.yelpvec);
			//ch.logic();
			//ch.logic(this);
			System.err.println("lelelle");
			//ch.logic();
			//ch.sendVector(this);
		}
		while (currDrivers != 0){
			currDrivers = ClientHandler.numDrivers;
		}
		if(line.equals("All drivers have arrived! Starting service.")){
			while(true){
				if(programover == true){
					ch.broadcastDrivers("ohno", this);
					System.out.println("All orders completed!");
				}
			}
		}
//		if(isfirst){
//			ch.runLogic();
//		}
	}
	


}

