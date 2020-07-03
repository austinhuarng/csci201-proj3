package austinhu_CSCI201L_Assignment3;

import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable {
    // Keep track of tasks
    private ArrayList<Task> taskList;

    public Schedule() {
        taskList = new ArrayList<>();
    }

    public void addTask(int time, String restaurant, String food) {
        taskList.add(new Task(time, restaurant, food));
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    // Inner class to store task object
    public static class Task  implements Serializable{
        private int time;
        private String restaurant;
        private String food;

        public Task(int time, String restaurant, String food) {
            this.time = time;
            this.restaurant = restaurant;
            this.food = food;
        }

        public int getTime() {
            return time;
        }

        public String getRestaurant() {
            return restaurant;
        }

        public String getFood() {
            return food;
        }
    }
}

