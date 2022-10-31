package com.apd.tema2.priorityintersection;

import com.apd.tema2.car.Car;

public class TaskPriorityCar implements Runnable
{
    private Car car;

    public TaskPriorityCar(Car car)
    {
        this.car = car;
    }

    public void run()
    {
        try
        {
            Thread.sleep(car.getWaitTime());
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        synchronized (PriorityIntersection.nr_passing)
        {
            PriorityIntersection.nr_passing++;
        }
        System.out.println("Car " + car.getId() + " with high priority has entered the intersection");
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Car " + car.getId() + " with high priority has exited the intersection");
        synchronized (PriorityIntersection.nr_passing)
        {
            PriorityIntersection.nr_passing--;
        }
    }
}
