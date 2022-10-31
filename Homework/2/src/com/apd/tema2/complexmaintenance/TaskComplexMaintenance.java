package com.apd.tema2.complexmaintenance;

import com.apd.tema2.car.Car;

public class TaskComplexMaintenance implements Runnable
{
    private Car[] cars;
    private int nr_cars;
    private int lane;
    private int lane_dest;
    private int cars_at_once;

    public TaskComplexMaintenance(Car[] cars, int nr_cars, int lane_dest, int cars_at_once)
    {
        this.cars = cars;
        this.nr_cars = nr_cars;
        this.lane = cars[0].getLane();
        this.lane_dest = lane_dest;
        this.cars_at_once = cars_at_once;
    }

    public void run()
    {
        boolean done = false;
        synchronized (ComplexMaintenance.locks[lane_dest])
        {
            for (int i = 0; i < nr_cars; i += cars_at_once)
            {
                for (int j = 0; j < cars_at_once; ++j)
                {
                    if (i + j >= nr_cars - 1)
                    {
                        done = true;
                        if (i + j >= nr_cars)
                            break;
                    }
                    System.out.println("Car " + cars[i+j].getId() + " from the lane " + lane + " has entered lane number " + lane_dest);
                }
                if (!done)
                {
                    System.out.println("The initial lane " + lane + " has no permits and is moved to the back of the new lane queue");
                    ComplexMaintenance.locks[lane_dest].notifyAll();
                }

                else // if (done)
                {
                    System.out.println("The initial lane " + lane + " has been emptied and removed from the new lane queue");
                    ComplexMaintenance.lanes_cnt[lane_dest]--;
                    ComplexMaintenance.locks[lane_dest].notifyAll();
                    break;
                }
                //check if it's the only old lane using new lane 'lane_dest'
                if (ComplexMaintenance.lanes_cnt[lane_dest] > 1)
                {
                    try
                    {
                        ComplexMaintenance.locks[lane_dest].wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
