package com.example.aps.logic;

import java.util.ArrayList;
import java.util.List;

public class DeviceStorage {

    private List<Device> devices;
    private Integer numberOfDevices;
    private Application application;


    public DeviceStorage(Integer numberOfDevices, Integer processingTime, Application application) {
        this.numberOfDevices = numberOfDevices;
        devices = new ArrayList<>();
        this.application = application;
        for (int deviceId = 0; deviceId < numberOfDevices; deviceId++) {
            Device device = new Device(deviceId, this, processingTime, this.application);
            devices.add(device);
        }
    }

    public List<Device> getDevices() {
        return devices;
    }

    public Boolean isAllDevicesFree() {
        for (Device d: devices) {
            if (d.getDeviceState() == DeviceState.BUSY) {
                return false;
            }
        }
        return true;
    }
}
