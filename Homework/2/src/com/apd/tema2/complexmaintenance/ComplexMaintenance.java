package com.apd.tema2.complexmaintenance;

import com.apd.tema2.car.Car;

import java.util.Scanner;

public class ComplexMaintenance
{
    private Scanner scanner;
    private final int max_lanes = 10;
    public static Object[] locks;
    public static int[] lanes_cnt;

    public ComplexMaintenance(Scanner scanner)
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
            int lane = cars[i].getLane();
            cars_by_lane[lane][nr_cars_by_lane[lane]] = cars[i];
            nr_cars_by_lane[lane]++;
            System.out.println("Car " + cars[i].getId() + " has come from the lane number " + lane);
        }
        int new_lanes = scanner.nextInt();
        int old_lanes = scanner.nextInt();
        int cars_at_once = scanner.nextInt();

        Thread[] threads = new Thread[old_lanes];
        // lanes_cnt = how many old lanes have switched to and are currently using this new lane
        lanes_cnt = new int[new_lanes];
        for(int i = 0; i < old_lanes; ++i)
        {
            int lane_dest = Math.min(i / (old_lanes / new_lanes), new_lanes - 1);
            threads[i] = new Thread(new TaskComplexMaintenance(cars_by_lane[i], nr_cars_by_lane[i], lane_dest, cars_at_once));
            lanes_cnt[lane_dest]++;
        }
        locks = new Object[new_lanes];
        for(int i = 0; i < new_lanes; ++i)
        {
            locks[i] = new Object();
        }

        for (int i = 0; i < old_lanes; ++i)
        {
            threads[i].start();
        }
        for (int i = 0; i < old_lanes; ++i)
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
