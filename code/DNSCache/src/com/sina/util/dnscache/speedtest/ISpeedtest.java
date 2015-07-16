package com.sina.util.dnscache.speedtest;

/**
 * Created by fenglei on 15/4/22.
 */
public interface ISpeedtest {

    public float speedTest(String ip, String host);


   public float speedFormula(long startTime , long stopTime,  int size);
}
