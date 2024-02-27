package com.example.aps.logic;

import java.util.ArrayList;
import java.util.List;

public class Generator implements Runnable {

    Application application;
    private Integer numberOfSources;
    private List<Source> sources;
    private Double lambda;
    private Double probability;


    public Double countPoisonProbability() {
        probability *= lambda / application.getStep().get();
        return probability;
    }

    public Generator(Application application, Integer numberOfSources, Double probability) {
        this.application = application;
        this.numberOfSources = numberOfSources;
        this.probability = probability;
        this.lambda = this.numberOfSources * this.probability;
        this.probability = 1/Math.exp(lambda);
        sources = new ArrayList<>();
        for (int sourceId = 0; sourceId < numberOfSources; sourceId++) {
            Source source = new Source(sourceId, this, application);
            sources.add(source);
        }
        application.getStatistics().setGenerator(this);
    }

    public void startGeneration() {
        application.getChoiceDispatcher().startChoiceDispatcher();
        Integer step = application.getStep().get();

        while (step <= this.numberOfSources) {

            application.incrementStep();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Double poisonProbability = this.countPoisonProbability();
            for (Source source: this.getSources()) {
                InputDispatcher inputDispatcher = InputDispatcher.getInstance(application);
                source.subscribe(inputDispatcher);
                source.generateRequest(poisonProbability);
            }
            step = application.getStep().get();
        }

        while (!application.getBuffer().isAllNull() || !application.getDeviceStorage().isAllDevicesFree()) {
            application.incrementStep();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Source> getSources() {
        return sources;
    }

    @Override
    public void run() {
        startGeneration();
    }
}
