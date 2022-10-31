package com.apd.tema2.railroad;

import com.apd.tema2.car.Car;

import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Railroad
{
    private Scanner scanner;
    private final int max_lanes = 10;
    public static CyclicBarrier barrier;

    public Railroad(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        Car[][] cars_by_lane = new Car[max_lanes][nr_cars];
        int[] nr_cars_by_lane = new int[max_lanes];
        int nr_lanes = 0;
        for (int i = 0; i < nr_cars; ++i)
        {
            cars[i] = new Car(scanner.nextInt(), scanner.nextInt());
            int lane = cars[i].getLane();
            if (lane + 1 > nr_lanes)
            {
                nr_lanes = lane + 1;
            }
            cars_by_lane[lane][nr_cars_by_lane[lane]] = cars[i];
            nr_cars_by_lane[lane]++;
        }

        Thread[] threads = new Thread[nr_lanes];
        for(int i = 0; i < nr_lanes; ++i)
        {
            threads[i] = new Thread(new TaskRailroad(cars_by_lane[i], nr_cars_by_lane[i]));
        }
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
