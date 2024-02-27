package com.example.aps.logic;


import java.util.ArrayList;
import java.util.List;

public class Device {

    private Integer deviceId;
    private DeviceStorage deviceStorage;
    private Integer processingTime;
    private Application application;
    private DeviceState deviceState = DeviceState.FREE;
    private Request processingRequest;
    private long startWorkingTime;
    private long startWaitingTime;
    private long waitingTime = 0;
    private long workingTime = 0;

    private List<Request> requestQueue = new ArrayList<>();


    public Device(Integer deviceId, DeviceStorage deviceStorage, Integer processingTime, Application application) {
        this.deviceId = deviceId;
        this.deviceStorage = deviceStorage;
        this.processingTime = processingTime;
        this.application = application;
        this.startWaitingTime = System.currentTimeMillis();
    }

    public void processRequest() {
        new Thread(() -> {
            for (Request r: requestQueue) {
                Integer startStep;
                Integer actualStep;
                processingRequest = r;

                synchronized (application) {
                    startStep = application.getStep().get();
                    actualStep = application.getStep().get();
                    startWorkingTime = System.currentTimeMillis();
                }

                while (actualStep <= startStep + processingTime) {
                    actualStep = application.getStep().get();
                }

                synchronized (application) {
                    workingTime += System.currentTimeMillis() - startWorkingTime;
                    processingRequest.countTimeInSystem(System.currentTimeMillis());
                    application.getStatistics().addRequestOnDevice(processingRequest, this);
                }
            }
            deviceState = DeviceState.FREE;
            processingRequest = null;
            requestQueue.clear();
        }).start();
    }


    public DeviceState getDeviceState() {
        return deviceState;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setProcessingRequests(List<Request> processingRequests) {
        deviceState = DeviceState.BUSY;
        this.requestQueue = new ArrayList<>(processingRequests);
    }

    public long getWorkingTime() {
        return workingTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long currentTime) {
        this.waitingTime = currentTime - startWaitingTime - workingTime;
    }

    public Request getProcessingRequest() {
        return processingRequest;
    }

    public List<Request> getRequestQueue() {
        return requestQueue;
    }
}
