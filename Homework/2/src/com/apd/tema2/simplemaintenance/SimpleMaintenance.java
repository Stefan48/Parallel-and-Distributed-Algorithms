package com.apd.tema2.simplemaintenance;

import com.apd.tema2.car.Car;

import java.util.ArrayList;
import java.util.Scanner;

public class SimpleMaintenance
{
    private Scanner scanner;
    public static Object lock;

    public SimpleMaintenance(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        ArrayList<Car> cars_lane0 = new ArrayList<>();
        ArrayList<Car> cars_lane1 = new ArrayList<>();
        for (int i = 0; i < nr_cars; ++i)
        {
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt());
            if (cars[i].getLane() == 0)
            {
                cars_lane0.add(cars[i]);
            }
            else
            {
                cars_lane1.add(cars[i]);
            }
            System.out.println("Car " + cars[i].getId() + " from side number " + cars[i].getLane() + " has reached the bottleneck");
        }
        int cars_at_once = scanner.nextInt();

        Thread[] threads = new Thread[2];
        threads[0] = new Thread(new TaskSimpleMaintenance(cars_lane0, cars_at_once));
        threads[1] = new Thread(new TaskSimpleMaintenance(cars_lane1, cars_at_once));
        lock = new Object();

        for (int i = 0; i < 2; ++i)
        {
            threads[i].start();
        }
        for (int i = 0; i < 2; ++i)
        {
            try
            {
                threads[i].join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
