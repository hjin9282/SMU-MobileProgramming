package smu.ai.transittime;

public class BusInfo {

    // 1. 멤버 변수 (private)
    private static String number;
    private String time;
    private String location;

    // 2. 생성자 (Constructor)
    public BusInfo(String number, String time, String location) {
        this.number = number;
        this.time = time;
        this.location = location;
    }

    // 3. Getter 메소드 (데이터를 가져오기 위해 public)
    public static String getNumber() {
        return number;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    // 지금 당장 데이터 변경이 필요 없으므로 Setter는 생략
}