package com.apd.tema2.simplesemaphore;

import com.apd.tema2.car.TaskSortCars;
import com.apd.tema2.car.Car;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class SimpleSemaphore
{
    private Scanner scanner;
    public static CyclicBarrier barrier;
    public static boolean sorted;

    public SimpleSemaphore(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        for (int i = 0; i < nr_cars; ++i)
        {
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
            System.out.println("Car " + cars[i].getId() + " has reached the semaphore, now waiting...");
        }
        int P = Runtime.getRuntime().availableProcessors();
        if (nr_cars <= P)
        {
            Arrays.sort(cars, Comparator.comparingInt(Car::getWaitTime));
        }
        else
        {
            Thread[] threads = new Thread[P];
            for (int i = 0; i < P; ++i)
            {
                threads[i] = new Thread(new TaskSortCars(i, P, cars, (x, y) -> x.getWaitTime() <= y.getWaitTime()));
            }
            barrier = new CyclicBarrier(P);
            sorted = false;
            for (int i = 0; i < P; ++i)
            {
                threads[i].start();
            }
            for (int i = 0; i < P; ++i)
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
        for (int i = 0; i < nr_cars; ++i)
        {
            System.out.println("Car " + cars[i].getId() + " has waited enough, now driving...");
        }
    }
}
