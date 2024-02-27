package com.example.aps.logic;

import java.util.ArrayList;
import java.util.List;

public class Buffer {
    private List<Request> requests;
    private Integer capacity;
    private Application application;
    private Integer pointer = 0;

    public Buffer(Integer capacity, Application application) {
        this.capacity = capacity;
        this.application = application;
        this.requests = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            requests.add(null);
        }
    }

    public void saveRequest(Request request) {
        synchronized (application) {
            Integer oldPointer = pointer;
            if (pointer == (capacity - 1)) {
                pointer = 0;
            } else {
                pointer++;
            }
            while (!pointer.equals(oldPointer)) {
                if (requests.get(pointer) == null) {
                    request.setStartTimeInBuffer(System.currentTimeMillis());
                    requests.set(pointer, request);
                    System.out.println("Buffer saved request from source: " + request.getSourceId());
                    return;
                }
                if (pointer == (capacity - 1)) {
                    pointer = 0;
                } else {
                    pointer++;
                }
            }
            if (requests.get(pointer) == null) {
                request.setStartTimeInBuffer(System.currentTimeMillis());
                requests.set(pointer, request);
                System.out.println("Buffer saved request from source: " + request.getSourceId());
                return;
            }
            Request worstRequest = getWorstRequest();
                if (worstRequest != null) {
                    pointer = requests.indexOf(worstRequest);
                    deleteRequest(worstRequest);
                    System.out.println("Denied request from source: " + worstRequest.getSourceId());
                    worstRequest.countTimeInSystem(System.currentTimeMillis());
                    application.getStatistics().addDeniedRequest(worstRequest);
                    request.setStartTimeInBuffer(System.currentTimeMillis());
                    requests.set(pointer, request);
                    System.out.println("Buffer saved request from source: " + request.getSourceId());
                }
        }
    }

    private Request getWorstRequest() {
        Request worstRequest = null;
        for (Request r : requests) {
            if (worstRequest == null || r.getSourceId() > worstRequest.getSourceId()) {
                worstRequest = r;
            }
        }
        return worstRequest;
    }

    public void deleteRequest(Request request) {
        request.countTimeInBuffer(System.currentTimeMillis());
        requests.set(requests.indexOf(request), null);
        /*System.out.println(requests);*/
    }

    public boolean isAllNull() {
        for (Request r : requests) {
            if (r != null) {
                return false;
            }
        }
        return true;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public Integer getPointer() {
        return pointer;
    }
}
