package main.webapp.java.com.customparams;

import javax.ws.rs.ext.ParamConverter;
import java.util.Calendar;

public class MyDate implements ParamConverter {

    private int date;
    private int month;
    private int year;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public Object fromString(String s) {
        Calendar c = Calendar.getInstance();
        if (s.equals("today")) {
            this.date = c.get(Calendar.DATE);
            this.month = c.get(Calendar.MONTH);
            this.year = c.get(Calendar.YEAR);
        }
        return this;
    }

    @Override
    public String toString(Object o) {
        if (o == null) {
            return null;
        } else {
            return o.toString();
        }
    }

    @Override
    public String toString() {
        return "MyDate{" +
                "date=" + date +
                ", month=" + month +
                ", year=" + year +
                '}';
    }

}
