/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.rr1;

/**
 *
 * @author Jessuse
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// ssss
class PCB {
    int pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnAroundTime;
    int responseTime;
    boolean executed;

    PCB(int _pid, int _arrival_time, int _burst_time) {
        pid = _pid;
        arrivalTime = _arrival_time;
        burstTime = _burst_time;
        remainingTime = burstTime;
        executed = false;
    }
}


class Info {
    int pid;
    int start_time;
    int end_time;

    Info(int _pid, int _start_time, int _end_time) {
        pid = _pid;
        start_time = _start_time;
        end_time = _end_time;
    }
}

public class RR1 {

    static void roundRobin(PCB process[], int quantum, int contextSwitch, int num, List<Info> ganttChart) {
        int[] remainingTime = new int[num];
        for (int i = 0; i < num; i++) {
            remainingTime[i] = process[i].burstTime;
        }

        int currentTime = 0;
        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (int i = 0; i < num; i++) {
                if (remainingTime[i] > 0) {
                    allDone = false;

                    if (remainingTime[i] > quantum) {
                        ganttChart.add(new Info(i + 1, currentTime, currentTime + quantum));
                        currentTime += quantum;
                        remainingTime[i] -= quantum;

                        currentTime += contextSwitch; // Adding context switch time
                    } else {
                        ganttChart.add(new Info(i + 1, currentTime, currentTime + remainingTime[i]));
                        currentTime += remainingTime[i];
                        process[i].finishTime = currentTime;
                        remainingTime[i] = 0;

                        currentTime += contextSwitch; // Adding context switch time
                    }
                }
            }
        }

        // Calculate turnaround time and waiting time
        for (int i = 0; i < num; i++) {
            process[i].turnAroundTime = process[i].finishTime - process[i].arrivalTime;
            process[i].waitingTime = process[i].turnAroundTime - process[i].burstTime;
        }
    }


    public static void main(String[] args) {
        List<Integer> arrivalTimes = new ArrayList<>();
        List<Integer> burstTimes = new ArrayList<>();
        int contextSwitch = 0;
        int quantum = 0;
        int n = 0;
        String filename = "C:/data/RR.txt"; // Specify the file name
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                arrivalTimes.add(Integer.parseInt(values[0]));
                burstTimes.add(Integer.parseInt(values[1]));
                contextSwitch = Integer.parseInt(values[2]);
                quantum = Integer.parseInt(values[3]);
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("the arrivale time :"+arrivalTimes);
        System.out.println("the burst time :"+burstTimes);
        System.out.println("the quantum :"+quantum);
        System.out.println("the context switch :"+contextSwitch);
        

        List<PCB> processes = new ArrayList<>();

        for (int i = 0; i < arrivalTimes.size(); i++) {
            processes.add(new PCB(i + 1, arrivalTimes.get(i), burstTimes.get(i)));
        }

        final int numProcesses = n;
        PCB[] processArray = processes.toArray(new PCB[0]);
        List<Info> ganttChart = new ArrayList<>();

        roundRobin(processArray, quantum, contextSwitch, numProcesses, ganttChart);

        // Calculate metrics
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        double totalBurstTime = 0;
        int currentTime = 0;
        for (int i = 0; i < numProcesses; i++) {
            totalWaitingTime += processArray[i].waitingTime;
            totalTurnaroundTime += processArray[i].turnAroundTime;
            totalBurstTime += processArray[i].burstTime;
            currentTime = Math.max(currentTime, processArray[i].finishTime);
        }

        // Print Gantt Chart
        printGanttChart(ganttChart);

        // Print results
        System.out.println("\nProcess\tArrivale Time\tBurst Time\tTurnaround Time\tWaiting Time");
        for (int i = 0; i < numProcesses; ++i) {
            System.out.println(("P"+(i + 1)) +"\t\t" + processArray[i].arrivalTime+"\t\t" + processArray[i].burstTime+ "\t\t" + processArray[i].turnAroundTime + "\t\t" + processArray[i].waitingTime);
        }

        // Calculate metrics
        double avgWaitingTime = totalWaitingTime / numProcesses;
        double avgTurnaroundTime = totalTurnaroundTime / numProcesses;
        double cpuUtilization = (totalBurstTime / currentTime) * 100;

        // Print metrics
        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("CPU Utilization Rate: " + cpuUtilization + "%");
    }

    
    
    public static void printGanttChart(List<Info> ganttChart) {
     int prevEndTime = 0;
    System.out.print("|");
    for (Info entry : ganttChart) {
        //System.out.print(" c  |");
        System.out.printf("    P" + entry.pid + "     |"); // Adjusted formatting for better alignment
        System.out.print(" c  |");
        prevEndTime = entry.end_time;
    }
    System.out.println();
    }
}
