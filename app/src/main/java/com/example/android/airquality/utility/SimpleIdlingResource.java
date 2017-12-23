package com.example.android.airquality.utility;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleIdlingResource  {

    private static AtomicBoolean isIdle;
    private static SimpleIdlingResource instance;

    private SimpleIdlingResource(){}

    public static SimpleIdlingResource getInstance(){
        if (instance == null){
            instance = new SimpleIdlingResource();
            isIdle = new AtomicBoolean();
        }
        return instance;
    }

    public static void setIsIdle(boolean value){
        isIdle.set(value);
    }

    public static boolean isIdle(){
        return isIdle.get();
    }
}
