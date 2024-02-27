package com.example.aps.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    private Map<Integer, List<Request>> allProducedRequests = new HashMap<>();
    private Map<Integer, List<Request>> allDeniedRequests = new HashMap<>();
    private Map<Integer, List<Request>> requestsOnDevices = new HashMap<>();
    private Application application;
    private Generator generator;

    public Statistics(Application application) {
        this.application = application;
    }

    public void addProducedRequest(Request request) {
        if (allProducedRequests.containsKey(request.getSourceId())) {
            List<Request> requests = allProducedRequests.get(request.getSourceId());
            requests.add(request);
        } else {
            List<Request> requests = new ArrayList<>();
            requests.add(request);
            allProducedRequests.put(request.getSourceId(), requests);
        }
    }

    public void addDeniedRequest(Request request) {
        if (allDeniedRequests.containsKey(request.getSourceId())) {
            List<Request> requests = allDeniedRequests.get(request.getSourceId());
            requests.add(request);
        } else {
            List<Request> requests = new ArrayList<>();
            requests.add(request);
            allDeniedRequests.put(request.getSourceId(), requests);
        }
    }

    public void addRequestOnDevice(Request request, Device device) {
        if (requestsOnDevices.containsKey(device.getDeviceId())) {
            List<Request> requests = requestsOnDevices.get(device.getDeviceId());
            requests.add(request);
        } else {
            List<Request> requests = new ArrayList<>();
            requests.add(request);
            requestsOnDevices.put(device.getDeviceId(), requests);
        }
    }

    public void printStatistics() {
        System.out.println("\n \n -------------------------- clock"
                + application.getStep() + "--------------------------\n");

        List<Device> devices = new ArrayList<>();
        Map<Integer, List<Request>> processedRequests = application.getStatistics().getRequestsOnDevices();
        for (Device device: application.getDeviceStorage().getDevices()) {
            if (!processedRequests.containsKey(device.getDeviceId())) {
                List<Request> requests = new ArrayList<>();
                processedRequests.put(device.getDeviceId(), requests);
            }
            device.setWaitingTime(System.currentTimeMillis());
            devices.add(device);
        }


        System.out.println("\n....................................................");


        Map<Integer, List<Request>> producedRequests = application.getStatistics().getAllProducedRequests();
        Map<Integer, List<Request>> deniedRequests = application.getStatistics().getAllDeniedRequests();

        for (Source source : generator.getSources()) {
            int sourceId = source.getSourceId();
            producedRequests.putIfAbsent(sourceId, new ArrayList<>());
            deniedRequests.putIfAbsent(sourceId, new ArrayList<>());
        }

        System.out.println("Source\tProduced Reqs\t\tDenied Reqs\t\tDenied Percent, %\t\tTime Buffer" +
                "\t\tTime System\t\tSystem Dispersion\t\tBuffer Dispersion");

        int totalNumberProducedRequests = 0;
        int totalNumberProcessedRequests = 0;
        for (int sourceId : producedRequests.keySet()) {
            List<Request> producedList = producedRequests.get(sourceId);
            List<Request> deniedList = deniedRequests.get(sourceId);

            int numOfProducedRequests = producedList.size();
            int numOfDeniedRequests = deniedList != null ? deniedList.size() : 0;

            double deniedPercent = numOfProducedRequests > 0 ? ((double)numOfDeniedRequests / numOfProducedRequests) * 100 : 0;
            deniedPercent = Math.round(deniedPercent * 10.0) / 10.0;

            long totalSumOfTime = 0;
            long sumOfTimeInBuffer = 0;

            for (Request request : producedList) {
                totalSumOfTime += request.getTimeInSystem();
                sumOfTimeInBuffer += request.getTimeInBuffer();
            }

            long averageTimeInSystem = producedList.isEmpty() ? 0 : totalSumOfTime / producedList.size();
            long averageTimeInBuffer = producedList.isEmpty() ? 0 : sumOfTimeInBuffer / producedList.size();

            long timeInSystemDispersion = 0;
            long timeInBufferDispersion = 0;

            for (Request request : producedList) {
                timeInSystemDispersion += Math.pow(request.getTimeInSystem() - averageTimeInSystem, 2);
                timeInBufferDispersion += Math.pow(request.getTimeInBuffer() - averageTimeInBuffer, 2);
            }

            totalNumberProducedRequests += numOfProducedRequests;
            totalNumberProcessedRequests += numOfProducedRequests - numOfDeniedRequests;
            timeInSystemDispersion = producedList.isEmpty() ? 0 : timeInSystemDispersion / producedList.size();
            timeInBufferDispersion = producedList.isEmpty() ? 0 : timeInBufferDispersion / producedList.size();

            System.out.format("%-6d  %-18d  %-14d  %-23.1f %-15d  %-14d %-24d %-34d\n",
                    sourceId, numOfProducedRequests, numOfDeniedRequests, deniedPercent,
                    averageTimeInBuffer, averageTimeInSystem, timeInSystemDispersion, timeInBufferDispersion);
        }
        System.out.println("Total produced requests " + totalNumberProducedRequests);
        System.out.println("Total processed requests " + totalNumberProcessedRequests);
        System.out.println("Processed percent " + (double) 100 * totalNumberProcessedRequests/totalNumberProducedRequests + "%");
        System.out.println("\n....................................................");

        if (application.getMode() == Mode.STEP) {
            System.out.println("Device\t\tProcessing req\t\tProcessed reqs\t\tWorking time\t\tWaiting time\t\tEffectivity, %");
            for (Map.Entry<Integer, List<Request>> entry : processedRequests.entrySet()) {
                int key = entry.getKey();
                List<Request> requests = entry.getValue();
                int numOfRequests = requests.size();
                long workingTime = devices.get(key).getWorkingTime();
                long waitingTime = devices.get(key).getWaitingTime();
                long effectivityPercent = 100 * workingTime / (waitingTime + workingTime);
                Request processingRequest = devices.get(key).getProcessingRequest();

                System.out.format("%-11d %-19s %-18d  %-18d  %-19d %-15d\n",
                        key, processingRequest, numOfRequests, workingTime, waitingTime, effectivityPercent);
            }
            System.out.println("\n....................................................");
        }

        System.out.println("Device\t\tProcessed reqs\t\tWorking time\t\tWaiting time\t\tEffectivity, %");

        long totalEffectivity = 0;
        for (Map.Entry<Integer, List<Request>> entry : processedRequests.entrySet()) {
            int key = entry.getKey();
            List<Request> requests = entry.getValue();
            int numOfRequests = requests.size();
            long workingTime = devices.get(key).getWorkingTime();
            long waitingTime = devices.get(key).getWaitingTime();
            long effectivityPercent = 100 * workingTime/(waitingTime + workingTime);

            totalEffectivity += effectivityPercent;
            System.out.format("%-10d  %-18d  %-18d  %-19d %-15d\n",
                    key, numOfRequests, workingTime, waitingTime, effectivityPercent);
        }
        totalEffectivity = totalEffectivity / application.getDeviceStorage().getDevices().size();
        System.out.println("Effectivity " + totalEffectivity + "%");
    }


    public Map<Integer, List<Request>> getAllProducedRequests() {
        return allProducedRequests;
    }

    public Map<Integer, List<Request>> getAllDeniedRequests() {
        return allDeniedRequests;
    }

    public Map<Integer, List<Request>> getRequestsOnDevices() {
        return requestsOnDevices;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }
}
