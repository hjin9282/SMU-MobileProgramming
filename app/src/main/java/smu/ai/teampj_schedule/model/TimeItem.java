package smu.ai.teampj_schedule.model;

public class TimeItem {
    public String time;
    public String dest;
    public boolean express;

    public TimeItem(String time, String dest, boolean express) {
        this.time = time;
        this.dest = dest;
        this.express = express;
    }
}

