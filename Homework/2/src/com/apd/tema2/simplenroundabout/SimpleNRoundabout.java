package com.apd.tema2.simplenroundabout;

import com.apd.tema2.car.Car;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class SimpleNRoundabout
{
    private Scanner scanner;
    public static Semaphore semaphore;

    public SimpleNRoundabout(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        for (int i = 0; i < nr_cars; ++i)
        {
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt());
            System.out.println("Car " + cars[i].getId() + " has reached the roundabout, now waiting...");
        }
        int car_limit = scanner.nextInt();
        int wait_time = scanner.nextInt() / 1000;

        Thread[] threads = new Thread[nr_cars];
        for (int i = 0; i < nr_cars; ++i)
        {
            threads[i] = new Thread(new TaskSimpleNRoundabout(cars[i], wait_time));
        }
        semaphore = new Semaphore(car_limit);
        for (int i = 0; i < nr_cars; ++i)
        {
            threads[i].start();
        }
        for (int i = 0; i < nr_cars; ++i)
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
