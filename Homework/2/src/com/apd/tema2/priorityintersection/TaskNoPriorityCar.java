package com.apd.tema2.priorityintersection;

import com.apd.tema2.car.Car;

public class TaskNoPriorityCar implements Runnable
{
    private Car car;

    public TaskNoPriorityCar(Car car)
    {
        this.car = car;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(car.getWaitTime());
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Car " + car.getId() + " with low priority is trying to enter the intersection...");
        PriorityIntersection.car_queue.add(car);
        // check car queue
        while (PriorityIntersection.nr_passing > 0 || car.getId() != PriorityIntersection.car_queue.peek().getId())
        {
            // busy waiting
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        PriorityIntersection.car_queue.poll();
        System.out.println("Car " + car.getId() + " with low priority has entered the intersection");
    }
}
