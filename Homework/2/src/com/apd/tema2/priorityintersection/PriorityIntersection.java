package com.apd.tema2.priorityintersection;

import com.apd.tema2.car.Car;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class PriorityIntersection
{
    private Scanner scanner;
    public static Integer nr_passing;
    public static Queue<Car> car_queue;

    public PriorityIntersection(Scanner scanner)
    {
        this.scanner = scanner;
    }

    public void Solve()
    {
        int nr_cars = scanner.nextInt();
        Car[] cars = new Car[nr_cars];
        for (int i = 0; i < nr_cars; ++i)
        {
            int id = scanner.nextInt(); scanner.nextInt(); scanner.nextInt();
            int wait_time = scanner.nextInt();
            int lane = scanner.nextInt();
            cars[i] = new Car(id, lane, wait_time);
        }

        Thread[] threads = new Thread[nr_cars];
        for (int i = 0; i < nr_cars; ++i)
        {
            if (cars[i].getLane() > 1)
            {
                threads[i] = new Thread(new TaskPriorityCar(cars[i]));
            }
            else
            {
                threads[i] = new Thread(new TaskNoPriorityCar(cars[i]));
            }
        }
        nr_passing = 0;
        car_queue = new LinkedBlockingQueue<>();

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
