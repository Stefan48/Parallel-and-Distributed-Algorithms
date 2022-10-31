package com.apd.tema2.simplenroundabout;

import com.apd.tema2.car.Car;

public class TaskSimpleNRoundabout implements Runnable
{
    private Car car;
    private int wait_time;

    public TaskSimpleNRoundabout(Car car, int wait_time)
    {
        this.car = car;
        this.wait_time = wait_time;
    }

    @Override
    public void run()
    {
        try
        {
            SimpleNRoundabout.semaphore.acquire();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Car " + car.getId() + " has entered the roundabout");
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Car " + car.getId() + " has exited the roundabout after " + wait_time + " seconds");
        SimpleNRoundabout.semaphore.release();
    }
}
