package com.apd.tema2.car;


import com.apd.tema2.simplesemaphore.SimpleSemaphore;

import java.util.concurrent.BrokenBarrierException;

public class TaskSortCars implements Runnable
{
    private int id;
    private int P;
    private Car[] cars;
    private CarComparator comparator;

    public TaskSortCars(int id, int P, Car[] cars, CarComparator comparator)
    {
        this.id = id;
        this.P = P;
        this.cars = cars;
        this.comparator = comparator;
    }

    @Override
    public void run()
    {

        Car aux;
        int range = (int)Math.ceil((double)cars.length / P); // range >= 2
        int start, stop;
        while(true)
        {
            // Odd transposition sort
            start = id * range;
            if (start % 2 == 0)
                start++;
            stop = Math.min(start + range - 1, cars.length - 1);
            if(stop % 2 != 0)
                stop--;

            SimpleSemaphore.sorted = true;
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int i = start; i < stop; i += 2)
            {
                if (!comparator.compare(cars[i], cars[i+1]))
                {
                    //swap(cars[i], cars[i+1]);
                    aux = new Car(cars[i]);
                    cars[i] = new Car(cars[i + 1]);
                    cars[i + 1] = new Car(aux);
                    SimpleSemaphore.sorted = false;
                }
            }
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            if (SimpleSemaphore.sorted)
                break;
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }

            // Even transposition sort
            start = id * range;
            if (start % 2 != 0)
                start--;
            stop = Math.min(start + range - 1, cars.length - 1);
            if(stop % 2 == 0)
                stop--;
            SimpleSemaphore.sorted = true;
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int i = start; i < stop; i += 2)
            {
                if (!comparator.compare(cars[i], cars[i + 1]))
                {
                    //swap(cars[i], cars[i+1]);
                    aux = new Car(cars[i]);
                    cars[i] = new Car(cars[i + 1]);
                    cars[i + 1] = new Car(aux);
                    SimpleSemaphore.sorted = false;
                }
            }
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
            if (SimpleSemaphore.sorted)
                break;
            // wait all threads
            try
            {
                SimpleSemaphore.barrier.await();
            } catch (BrokenBarrierException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
