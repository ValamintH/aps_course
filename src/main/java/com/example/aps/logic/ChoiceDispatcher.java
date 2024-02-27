package com.example.aps.logic;

import java.util.ArrayList;
import java.util.List;

public class ChoiceDispatcher {
    private Application application;
    private Buffer buffer;
    private DeviceStorage deviceStorage;

    public ChoiceDispatcher(Application application, Buffer buffer, DeviceStorage deviceStorage) {
        this.application = application;
        this.buffer = buffer;
        this.deviceStorage = deviceStorage;
    }


    public Device chooseDevice() {
        for (Device device: deviceStorage.getDevices()) {
            if (device.getDeviceState() == DeviceState.FREE) {
                return device;
            }
        }
        return null;
    }


    public Request chooseRequest() {
        List<Request> requests = buffer.getRequests();
        if (!buffer.isAllNull()) {
            Request bestRequest = null;
            for (Request r : requests) {
                if (r != null) {
                    if (bestRequest == null || r.getSourceId() < bestRequest.getSourceId()) {
                        bestRequest = r;
                    }
                }
            }
            return bestRequest;
        }
        return null;
    }

    public void startChoiceDispatcher() {
        new Thread(() -> {
            while (true) {
                synchronized (application) {
                    if (!buffer.isAllNull()) {
                        ChoiceDispatcher choiceDispatcher = application.getChoiceDispatcher();
                        Device device = choiceDispatcher.chooseDevice();
                        if (device != null) {
                            Request chosenRequest = choiceDispatcher.chooseRequest();
                            if (chosenRequest != null) {
                                List<Request> requests = buffer.getRequests();
                                List<Request> chosenRequests = new ArrayList<>();
                                for (Request r: requests) {
                                    if (r != null) {
                                        if (r.getSourceId().equals(chosenRequest.getSourceId())) {
                                            chosenRequests.add(r);
                                            application.getBuffer().deleteRequest(r);
                                        }
                                    }
                                }
                                device.setProcessingRequests(chosenRequests);
                                device.processRequest();
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
