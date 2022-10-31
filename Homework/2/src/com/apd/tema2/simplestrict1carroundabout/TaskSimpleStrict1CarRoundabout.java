package com.apd.tema2.simplestrict1carroundabout;

import com.apd.tema2.car.Car;

import java.util.concurrent.BrokenBarrierException;

public class TaskSimpleStrict1CarRoundabout implements Runnable
{
    private Car[] cars;
    private int cars_per_lane;
    private int wait_time;

    public TaskSimpleStrict1CarRoundabout(Car[] cars, int cars_per_lane, int wait_time)
    {
        this.cars = cars;
        this.cars_per_lane = cars_per_lane;
        this.wait_time = wait_time;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < cars_per_lane; ++i)
        {
            // wait all threads
            try
            {
                SimpleStrict1CarRoundabout.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Car " + cars[i].getId() + " has entered the roundabout from lane " + cars[i].getLane());
            try
            {
                Thread.sleep(100);
                // wait all threads
                SimpleStrict1CarRoundabout.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Car " + cars[i].getId() + " has exited the roundabout after " + wait_time + " seconds");
        }
    }
}
