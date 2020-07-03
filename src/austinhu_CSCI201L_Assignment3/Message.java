package austinhu_CSCI201L_Assignment3;

import java.io.Serializable;
import java.util.Vector;

public class Message<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String type;
	public T T;
	
	public Message(String type, T T) {
		this.type = type;
		this.T = T;
	}
	
}
