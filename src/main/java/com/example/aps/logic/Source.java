package com.example.aps.logic;

import java.util.Random;
import java.util.concurrent.Flow;


public class Source implements Flow.Publisher<Request> {

    private Flow.Subscriber<? super Request> subscriber;
    private Integer sourceId;
    private Generator generator;
    private Application application;


    public Source(Integer id, Generator generator, Application application) {
        this.sourceId = id;
        this.generator = generator;
        this.application = application;
    }

    public void generateRequest(Double poisonProbability) {
        Random random = new Random();
        Double randomProbability = random.nextDouble(0, 1);
        if (randomProbability <= poisonProbability) {
            Request request = new Request(sourceId, application.getStep().get());
            notifySubscriber(request);
            generator.application.getStatistics().addProducedRequest(request);
        }
    }

    public Integer getSourceId() {
        return sourceId;
    }


    private void notifySubscriber(Request request) {
        subscriber.onNext(request);
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Request> subscriber) {
        this.subscriber = subscriber;
    }
}
