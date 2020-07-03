package austinhu_CSCI201L_Assignment3;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {
	private String name;
	private Double latitude;
	private Double longitude;
    private double distance;

	public Restaurant(String name, Double latitude, Double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = -1; // Default to -1
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public void setDistance(double latitude, double longitude) {
        this.distance = 3963.0 * Math.acos((Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(this.latitude)))
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(this.latitude))
                * Math.cos(Math.toRadians(longitude) - Math.toRadians(this.longitude)));
    }
}
