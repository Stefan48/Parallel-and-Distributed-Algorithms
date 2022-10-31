package com.apd.tema2.crosswalk;

import com.apd.tema2.car.Car;

public class TaskCrosswalk implements Runnable
{
    private Car car;
    private boolean last_color;
    private Pedestrians pedestrians;

    public TaskCrosswalk(Car car, Pedestrians pedestrians)
    {
        this.car = car;
        // initialize last color with red
        this.last_color = false;
        this.pedestrians = pedestrians;
    }

    public void run()
    {
        while (!pedestrians.isFinished() || last_color == false)
        {
            if (pedestrians.isPass() && last_color == true)
            {
                System.out.println("Car " + car.getId() + " has now red light");
                last_color = false;
            }
            else if (!pedestrians.isPass() && last_color == false)
            {
                System.out.println("Car " + car.getId() + " has now green light");
                last_color = true;
            }
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
