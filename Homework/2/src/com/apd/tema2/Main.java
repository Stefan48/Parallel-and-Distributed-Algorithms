package com.apd.tema2;

import com.apd.tema2.complexmaintenance.ComplexMaintenance;
import com.apd.tema2.crosswalk.Crosswalk;
import com.apd.tema2.priorityintersection.PriorityIntersection;
import com.apd.tema2.railroad.Railroad;
import com.apd.tema2.simplemaintenance.SimpleMaintenance;
import com.apd.tema2.simplemaxxcarroundabout.SimpleMaxXCarRoundabout;
import com.apd.tema2.simplenroundabout.SimpleNRoundabout;
import com.apd.tema2.simplesemaphore.SimpleSemaphore;
import com.apd.tema2.simplestrict1carroundabout.SimpleStrict1CarRoundabout;
import com.apd.tema2.simplestrictxcarroundabout.SimpleStrictXCarRoundabout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(args[0]));
        String intersection_type = scanner.next();
        switch (intersection_type)
        {
            case "simple_semaphore":
                new SimpleSemaphore(scanner).Solve();
                break;
            case "simple_n_roundabout":
                new SimpleNRoundabout(scanner).Solve();
                break;
            case "simple_strict_1_car_roundabout":
                new SimpleStrict1CarRoundabout(scanner).Solve();
                break;
            case "simple_strict_x_car_roundabout":
                new SimpleStrictXCarRoundabout(scanner).Solve();
                break;
            case "simple_max_x_car_roundabout":
                new SimpleMaxXCarRoundabout(scanner).Solve();
                break;
            case "priority_intersection":
                new PriorityIntersection(scanner).Solve();
                break;
            case "crosswalk":
                new Crosswalk(scanner).Solve();
                break;
            case "simple_maintenance":
                new SimpleMaintenance(scanner).Solve();
                break;
            case "complex_maintenance":
                new ComplexMaintenance(scanner).Solve();
                break;
            case "railroad":
                new Railroad(scanner).Solve();
                break;
            default:
                break;
        }
    }
}
