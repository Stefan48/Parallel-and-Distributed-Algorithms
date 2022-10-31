package com.apd.tema2.simplemaxxcarroundabout;

import com.apd.tema2.car.Car;

public class TaskSimpleMaxXCarRoundabout implements Runnable
{
    private Car[] cars;
    private int nr_cars;
    private int wait_time;
    private int lane;

    public TaskSimpleMaxXCarRoundabout(Car[] cars, int nr_cars, int wait_time, int lane)
    {
        this.cars = cars;
        this.nr_cars = nr_cars;
        this.wait_time = wait_time;
        this.lane = lane;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < nr_cars; ++i)
        {
            try
            {
                SimpleMaxXCarRoundabout.semaphores[lane].acquire();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Car " + cars[i].getId() + " has entered the roundabout from lane " + cars[i].getLane());
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            System.out.println("Car " + cars[i].getId() + " has exited the roundabout after " + wait_time + " seconds");
            SimpleMaxXCarRoundabout.semaphores[lane].release();
        }
    }
}
