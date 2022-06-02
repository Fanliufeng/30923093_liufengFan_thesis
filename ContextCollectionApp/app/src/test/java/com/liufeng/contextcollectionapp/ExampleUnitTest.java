package com.liufeng.contextcollectionapp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.getTime());
        Date date = new Date(timestamp.getTime());

        long time1 = 1651293741224L;
        long time2 = 1651296581408L;
        Date date1 = new Date(time1);
        Date date2 = new Date(time2);

        long time3 = Long.parseLong("1651293741224");

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");




        System.out.println((time2-time1)/(1000*60) > 30);
        System.out.println(formatter.format(time1));
        System.out.println(formatter.format(time3));
        System.out.println(formatter.format(time2));
        //Date date = new Date();
        //System.out.println(formatter.format(date));
    }
    @Test
    public void test2(){
        List<Double> speed = new ArrayList<>();

        speed.add(165.4);
        speed.add(635.4);
        speed.add(625.4);
        speed.add(615.4);
        speed.add(22.4);
        speed.add(62.4);

        double max = Collections.max(speed);
        System.out.println(String.valueOf(max));



    }
}