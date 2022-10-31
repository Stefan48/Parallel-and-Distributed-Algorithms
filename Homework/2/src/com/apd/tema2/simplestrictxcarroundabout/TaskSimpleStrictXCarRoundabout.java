package com.apd.tema2.simplestrictxcarroundabout;

import com.apd.tema2.car.Car;

import java.util.concurrent.BrokenBarrierException;

public class TaskSimpleStrictXCarRoundabout implements Runnable
{
    private Car[] cars;
    private int cars_per_lane;
    private int wait_time;
    private int cars_at_once_per_lane;

    public TaskSimpleStrictXCarRoundabout(Car[] cars, int cars_per_lane, int wait_time, int cars_at_once_per_lane)
    {
        this.cars = cars;
        this.cars_per_lane = cars_per_lane;
        this.wait_time = wait_time;
        this.cars_at_once_per_lane = cars_at_once_per_lane;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < cars_per_lane; i += cars_at_once_per_lane)
        {
            // wait all threads
            try
            {
                SimpleStrictXCarRoundabout.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int j = 0; j < cars_at_once_per_lane; ++j)
            {
                System.out.println("Car " + cars[i+j].getId() + " was selected to enter the roundabout from lane " + cars[i+j].getLane());
            }
            // wait all threads
            try
            {
                SimpleStrictXCarRoundabout.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int j = 0; j < cars_at_once_per_lane; ++j)
            {
                System.out.println("Car " + cars[i+j].getId() + " has entered the roundabout from lane " + cars[i+j].getLane());
            }
            try
            {
                Thread.sleep(100);
                // wait all threads
                SimpleStrictXCarRoundabout.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int j = 0; j < cars_at_once_per_lane; ++j)
            {
                System.out.println("Car " + cars[i+j].getId() + " has exited the roundabout after " + wait_time + " seconds");
            }
        }
    }
}
