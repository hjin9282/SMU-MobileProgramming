package smu.ai.teampj_schedule.model;

import java.util.List;

public class TimeTableResponse {
    public TimeTableService SearchSTNTimeTableByIDService;

    public static class TimeTableService {
        public int list_total_count;
        public Result RESULT;
        public List<TimeTableRow> row;
    }

    public static class Result {
        public String CODE;
        public String MESSAGE;
    }
}