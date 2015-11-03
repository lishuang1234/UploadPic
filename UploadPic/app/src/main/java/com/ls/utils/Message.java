package com.ls.utils;

/**
 * Created by ls on 15-11-3.
 * <p/>
 * [
 * {
 * "contribution": "0.310054",
 * "from": "08:00:20:0A:8C:6D+0",
 * "name": "et000.jpg",
 * "value": "1498.98"
 * },
 * {
 * "contribution": "0.221014",
 * "from": "08:00:20:0A:8C:6D+1",
 * "name": "et001.jpg",
 * "value": "1068.51"
 * }
 * ]
 */
public class Message {


    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return
                "contribution=" + contribution +
                "  value=" + value
                ;
    }

    public Message() {

    }

    private String contribution;
    private String from;
    private String name;
    private String value;


}
