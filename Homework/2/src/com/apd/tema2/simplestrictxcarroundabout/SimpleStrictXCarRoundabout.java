package com.apd.tema2.simplestrictxcarroundabout;

import com.apd.tema2.car.Car;

import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class SimpleStrictXCarRoundabout
{
    private Scanner scanner;
    private static final int max_lanes = 100;
    public static CyclicBarrier barrier;

    public SimpleStrictXCarRoundabout(Scanner scanner)
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
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt());
            System.out.println("Car " + cars[i].getId() + " has reached the roundabout, now waiting...");
            int lane = cars[i].getLane();
            cars_by_lane[lane][nr_cars_by_lane[lane]] = new Car(cars[i]);
            nr_cars_by_lane[lane]++;
        }
        int nr_lanes = scanner.nextInt();
        int wait_time = scanner.nextInt() / 1000;
        int cars_at_once_per_lane = scanner.nextInt();

        Thread[] threads = new Thread[nr_lanes];
        int cars_per_lane = nr_cars_by_lane[0];
        if (cars_per_lane % cars_at_once_per_lane != 0) return;
        boolean ok = true;
        for (int i = 0; i < nr_lanes; ++i)
        {
            if (nr_cars_by_lane[i] != cars_per_lane)
            {
                ok = false;
                break;
            }
            threads[i] = new Thread(new TaskSimpleStrictXCarRoundabout(cars_by_lane[i], cars_per_lane, wait_time, cars_at_once_per_lane));
        }
        if (!ok) return;
        barrier = new CyclicBarrier(nr_lanes);
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
