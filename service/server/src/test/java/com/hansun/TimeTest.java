package com.hansun;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuanl2 on 2017/4/13.
 */
public class TimeTest {


    public static void main(String[] args) {
        Random random1 = new Random();

        List<DeviceEntity> deviceIDsByUserA = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            int areaCount = random1.nextInt(6);
            for (int i = 1; i < areaCount; i++) {
                deviceIDsByUserA.add(new DeviceEntity("Device-2017010" + i + "0" + j, "Area-A-" + j, "user1"));
            }
        }

        List<DeviceEntity> deviceIDsByUserB = new ArrayList<>();
        for (int j = 0; j < 6; j++) {
            int areaCount = random1.nextInt(5);
            for (int i = 1; i < areaCount; i++) {
                deviceIDsByUserB.add(new DeviceEntity("Device-2017020" + i + "0" + j, "Area-B-" + j, "user2"));
            }
        }
        List<DeviceEntity> deviceIDsByUserC = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            int areaCount = random1.nextInt(8);
            for (int i = 1; i < areaCount; i++) {
                deviceIDsByUserB.add(new DeviceEntity("Device-2017030" + i + "0" + j, "Area-C-" + j, "user3"));
            }
        }
        List<DeviceEntity> deviceIDsByUserD = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            int areaCount = random1.nextInt(10);
            for (int i = 1; i < areaCount; i++) {
                deviceIDsByUserD.add(new DeviceEntity("Device-2017040" + i + "0" + j, "Area-D-" + j, "user4"));
            }
        }
        List<DeviceEntity> deviceIDsByUserE = new ArrayList<>();
        for (int j = 0; j < 6; j++) {
            int areaCount = random1.nextInt(15);
            for (int i = 1; i < areaCount; i++) {
                deviceIDsByUserE.add(new DeviceEntity("Device-2017050" + i + "0" + j, "Area-E-" + j, "user5"));
            }
        }
        List<String> conmusers = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            conmusers.add("payAccount00" + i);
        }

        List<Consume> consumeTypes = new ArrayList<>();
        consumeTypes.add(new Consume("2-Minute", 1, 2));
        consumeTypes.add(new Consume("5-Minute", 2, 5));
        consumeTypes.add(new Consume("8-inute", 3, 8));
        consumeTypes.add(new Consume("15-Minute", 5, 15));

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("consume_data.txt"));


            for (DeviceEntity s : deviceIDsByUserA) {
                String str;
                Random random = new Random();
                Instant instant = Instant.now();
                for (int i = 1; i < 100; i++) {
                    int count = random.nextInt(12);
                    for (int j = 1; j < count; j++) {
                        int hour = random.nextInt(23);
                        if(hour < 8) {
                            hour = hour + 12;
                        }
                        Instant in = instant.minus(Period.ofDays(i)).plus(hour, ChronoUnit.HOURS);
                        String time = in.getEpochSecond() + "" + in.getNano();
                        Consume con = consumeTypes.get(random.nextInt(4));
                        str = "consume_data,deviceid=" + s.getDeviceID()
                                + ",consume=" + conmusers.get(random.nextInt(1000))
                                + ",type=" + con.getType()
                                + ",hour=" + hour
                                + ",area=" + s.getArea()
                                + ",userid=" + s.getUserid()
                                + " count=1,price=" + con.getPrices()
                                + ",duration=" + con.getDuration() + " " + time;
                        out.write(str + "\n");  //Replace with the string
                    }
                }

            }

            for (DeviceEntity s : deviceIDsByUserB) {
                String str;
                Random random = new Random();
                Instant instant = Instant.now();
                for (int i = 1; i < 100; i++) {
                    int count = random.nextInt(12);
                    for (int j = 1; j < count; j++) {
                        int hour = random.nextInt(23);
                        if(hour < 8) {
                            hour = hour + 12;
                        }
                        Instant in = instant.minus(Period.ofDays(i)).plus(hour, ChronoUnit.HOURS);
                        String time = in.getEpochSecond() + "" + in.getNano();
                        Consume con = consumeTypes.get(random.nextInt(4));
                        str = "consume_data,deviceid=" + s.getDeviceID()
                                + ",consume=" + conmusers.get(random.nextInt(1000))
                                + ",type=" + con.getType()
                                + ",hour=" + hour
                                + ",area=" + s.getArea()
                                + ",userid=" + s.getUserid()
                                + " count=1,price=" + con.getPrices()
                                + ",duration=" + con.getDuration() + " " + time;
                        out.write(str + "\n");  //Replace with the string
                    }
                }

            }

            for (DeviceEntity s : deviceIDsByUserC) {
                String str;
                Random random = new Random();
                Instant instant = Instant.now();
                for (int i = 1; i < 100; i++) {
                    int count = random.nextInt(12);
                    for (int j = 1; j < count; j++) {
                        int hour = random.nextInt(23);
                        if(hour < 8) {
                            hour = hour + 12;
                        }
                        Instant in = instant.minus(Period.ofDays(i)).plus(hour, ChronoUnit.HOURS);
                        String time = in.getEpochSecond() + "" + in.getNano();
                        Consume con = consumeTypes.get(random.nextInt(4));
                        str = "consume_data,deviceid=" + s.getDeviceID()
                                + ",consume=" + conmusers.get(random.nextInt(1000))
                                + ",type=" + con.getType()
                                + ",hour=" + hour
                                + ",area=" + s.getArea()
                                + ",userid=" + s.getUserid()
                                + " count=1,price=" + con.getPrices()
                                + ",duration=" + con.getDuration() + " " + time;
                        out.write(str + "\n");  //Replace with the string
                    }
                }

            }

            for (DeviceEntity s : deviceIDsByUserD) {
                String str;
                Random random = new Random();
                Instant instant = Instant.now();
                for (int i = 1; i < 100; i++) {
                    int count = random.nextInt(12);

                    for (int j = 1; j < count; j++) {
                        int hour = random.nextInt(23);
                        if(hour < 8) {
                            hour = hour + 12;
                        }
                        Instant in = instant.minus(Period.ofDays(i)).plus(hour, ChronoUnit.HOURS);
                        String time = in.getEpochSecond() + "" + in.getNano();
                        Consume con = consumeTypes.get(random.nextInt(4));
                        str = "consume_data,deviceid=" + s.getDeviceID()
                                + ",consume=" + conmusers.get(random.nextInt(1000))
                                + ",type=" + con.getType()
                                + ",hour=" + hour
                                + ",area=" + s.getArea()
                                + ",userid=" + s.getUserid()
                                + " count=1,price=" + con.getPrices()
                                + ",duration=" + con.getDuration() + " " + time;
                        out.write(str + "\n");  //Replace with the string
                    }
                }

            }

            for (DeviceEntity s : deviceIDsByUserE) {
                String str;
                Random random = new Random();
                Instant instant = Instant.now();
                for (int i = 1; i < 100; i++) {
                    int count = random.nextInt(12);
                    for (int j = 1; j < count; j++) {
                        int hour = random.nextInt(23);
                        if(hour < 8) {
                            hour = hour + 14;
                        }
                        Instant in = instant.minus(Period.ofDays(i)).plus(hour, ChronoUnit.HOURS);
                        String time = in.getEpochSecond() + "" + in.getNano();
                        Consume con = consumeTypes.get(random.nextInt(4));
                        str = "consume_data,deviceid=" + s.getDeviceID()
                                + ",consume=" + conmusers.get(random.nextInt(1000))
                                + ",type=" + con.getType()
                                + ",hour=" + hour
                                + ",area=" + s.getArea()
                                + ",userid=" + s.getUserid()
                                + " count=1,price=" + con.getPrices()
                                + ",duration=" + con.getDuration() + " " + time;
                        out.write(str + "\n");  //Replace with the string
                    }
                }

            }

            //you are trying to write
            out.close();

        } catch (IOException e) {
            System.out.println("Exception ");

        }
    }


}

class DeviceEntity {
    private String deviceID;
    private String area;
    private String userid;

    public DeviceEntity(String deviceID, String area, String userid) {
        this.userid = userid;
        this.deviceID = deviceID;
        this.area = area;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

class Consume {
    private String type;
    private int prices;
    private int duration;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrices() {
        return prices;
    }

    public void setPrices(int prices) {
        this.prices = prices;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Consume(String type, int prices, int duration) {
        this.type = type;
        this.prices = prices;
        this.duration = duration;
    }
}