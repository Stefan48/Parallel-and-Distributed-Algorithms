package com.apd.tema2.crosswalk;
import com.apd.tema2.car.Car;

import java.util.Scanner;

public class Crosswalk
{
    private Scanner scanner;

    public Crosswalk(Scanner scanner)
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
        }
        int execute_time = scanner.nextInt();
        int max_pedestrians = scanner.nextInt();

        Thread[] threads = new Thread[nr_cars + 1];
        Pedestrians pedestrians = new Pedestrians(execute_time, max_pedestrians);
        for (int i = 0; i < nr_cars; ++i)
        {
            threads[i] = new Thread(new TaskCrosswalk(cars[i], pedestrians));
        }
        threads[nr_cars] = new Thread(pedestrians);

        for (int i = 0; i <= nr_cars; ++i)
        {
            threads[i].start();
        }
        for (int i = 0; i <= nr_cars; ++i)
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
