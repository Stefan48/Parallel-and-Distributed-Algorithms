package com.apd.tema2.simplemaxxcarroundabout;

import com.apd.tema2.car.Car;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class SimpleMaxXCarRoundabout
{
    private Scanner scanner;
    private static final int max_lanes = 100;
    public static Semaphore[] semaphores;

    public SimpleMaxXCarRoundabout(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        Car[][] cars_by_lane = new Car[max_lanes][nr_cars];
        int[] nr_cars_by_lane = new int[max_lanes];
        for (int i = 0; i < nr_cars; ++i)
        {
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
            System.out.println("Car " + cars[i].getId() + " has reached the roundabout from lane " + cars[i].getLane());
            int lane = cars[i].getLane();
            cars_by_lane[lane][nr_cars_by_lane[lane]] = new Car(cars[i]);
            nr_cars_by_lane[lane]++;
        }
        int nr_lanes = scanner.nextInt();
        int wait_time = scanner.nextInt() / 1000;
        int cars_max_at_once_per_lane = scanner.nextInt();

        Thread[] threads = new Thread[nr_lanes];
        semaphores = new Semaphore[nr_lanes];

        for (int i = 0; i < nr_lanes; ++i)
        {
            threads[i] = new Thread(new TaskSimpleMaxXCarRoundabout(cars_by_lane[i], nr_cars_by_lane[i], wait_time, i));
            semaphores[i] = new Semaphore(cars_max_at_once_per_lane);
        }
        for (int i = 0; i < nr_lanes; ++i)
        {
            threads[i].start();
        }
        for (int i = 0; i < nr_lanes; ++i)
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
