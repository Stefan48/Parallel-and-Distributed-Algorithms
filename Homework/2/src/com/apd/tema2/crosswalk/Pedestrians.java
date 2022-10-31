package com.apd.tema2.crosswalk;

public class Pedestrians implements Runnable
{
    private static final int PEDESTRIAN_COUNTER_TIME = 500;
    private static final int PEDESTRIAN_PASSING_TIME = 2000;


    private int pedestriansNo = 0;
    private int maxPedestriansNo;
    private boolean pass = false;
    private boolean finished = false;
    private int executeTime;
    private long startTime;

    public Pedestrians(int executeTime, int maxPedestriansNo)
    {
        this.startTime = System.currentTimeMillis();
        this.executeTime = executeTime;
        this.maxPedestriansNo = maxPedestriansNo;
    }

    @Override
    public void run()
    {
        //this.startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < executeTime)
        {
            try
            {
                pedestriansNo++;
                Thread.sleep(PEDESTRIAN_COUNTER_TIME);

                if(pedestriansNo == maxPedestriansNo)
                {
                    pedestriansNo = 0;
                    pass = true;
                    Thread.sleep(PEDESTRIAN_PASSING_TIME);
                    pass = false;
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        finished = true;
    }

    public boolean isPass()
    {
        return pass;
    }

    public boolean isFinished()
    {
        return finished;
    }
}
