package com.apd.tema2.simplemaintenance;

import com.apd.tema2.car.Car;
import java.util.ArrayList;

public class TaskSimpleMaintenance implements Runnable
{
    private ArrayList<Car> cars;
    private int cars_at_once;

    public TaskSimpleMaintenance(ArrayList<Car> cars, int cars_at_once)
    {
        this.cars = cars;
        this.cars_at_once = cars_at_once;
    }

    public void run()
    {
        synchronized (SimpleMaintenance.lock)
        {
            for (int i = 0; i < cars.size(); i += cars_at_once)
            {
                for (int j = 0; j < cars_at_once; ++j)
                {
                    System.out.println("Car " + cars.get(i+j).getId() + " from side number " + cars.get(i+j).getLane() + " has passed the bottleneck");
                }
                SimpleMaintenance.lock.notifyAll();
                if (i + cars_at_once == cars.size())
                    break;
                try
                {
                    SimpleMaintenance.lock.wait();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
