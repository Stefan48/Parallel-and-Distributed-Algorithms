package com.apd.tema2.railroad;

import com.apd.tema2.car.Car;

import java.util.concurrent.BrokenBarrierException;

public class TaskRailroad implements Runnable
{
    private Car[] cars;
    private int nr_cars;
    private int lane;

    public TaskRailroad(Car[] cars, int nr_cars)
    {
        this.cars = cars;
        this.nr_cars = nr_cars;
        this.lane = cars[0].getLane();
    }

    public void run()
    {
        for (int i = 0; i < nr_cars; ++i)
        {
            System.out.println("Car " + cars[i].getId() + " from side number " + lane + " has stopped by the railroad");
        }
        try
        {
            Railroad.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e)
        {
            e.printStackTrace();
        }
        if (lane == 0)
        {
            System.out.println("The train has passed, cars can now proceed");
        }
        try
        {
            Railroad.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < nr_cars; ++i)
        {
            System.out.println("Car " + cars[i].getId() + " from side number " + lane + " has started driving");
        }
    }
}
