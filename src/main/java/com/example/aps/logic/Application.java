package com.example.aps.logic;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {

    private AtomicInteger step = new AtomicInteger(0);
    private Integer bufferCapacity;
    private Buffer buffer;
    private Statistics statistics = new Statistics(this);
    private Mode mode;
    private Integer numberOfDevices;
    private Integer processingTime;
    private DeviceStorage deviceStorage;
    private ChoiceDispatcher choiceDispatcher;

    public Application(Integer bufferCapacity, Integer numberOfDevices, Integer processingTime) {
        this.bufferCapacity = bufferCapacity;
        this.numberOfDevices = numberOfDevices;
        this.processingTime = processingTime;
        this.buffer = new Buffer(this.bufferCapacity, this);
        this.deviceStorage = new DeviceStorage(numberOfDevices, processingTime, this);
        this.choiceDispatcher = new ChoiceDispatcher(this, buffer, deviceStorage);
    }

    public synchronized void incrementStep() {
        Scanner scanner = new Scanner(System.in);
        if (mode == Mode.STEP) {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            statistics.printStatistics();
            System.out.println(buffer.getRequests());
            System.out.println("Pointer in buffer: " + buffer.getPointer());
            step.incrementAndGet();
        } else {
            step.incrementAndGet();;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized AtomicInteger getStep() {
        return step;
    }
    public Buffer getBuffer() {
        return buffer;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public ChoiceDispatcher getChoiceDispatcher() {
        return choiceDispatcher;
    }

    public DeviceStorage getDeviceStorage() {
        return deviceStorage;
    }

    public Mode getMode() { return mode; }
}
