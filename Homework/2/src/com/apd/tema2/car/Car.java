package com.apd.tema2.car;

public class Car
{
    private int id;
    private int lane;
    private int wait_time;

    public Car(int id, int lane, int wait_time)
    {
        this.id = id;
        this.lane = lane;
        this.wait_time = wait_time;
    }
    public Car(int id, int lane)
    {
        this(id, lane, 0);
    }
    public Car(Car other)
    {
        this.id = other.id;
        this.lane = other.lane;
        this.wait_time = other.wait_time;
    }

    public int getId()
    {
        return id;
    }
    public int getLane()
    {
        return lane;
    }
    public int getWaitTime()
    {
        return wait_time;
    }
}
