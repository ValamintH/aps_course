package com.example.aps.logic;

import java.util.concurrent.Flow;

public class InputDispatcher implements Flow.Subscriber<Request> {

    private Flow.Subscription subscription;
    private Application application;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Request request) {
        application.getBuffer().saveRequest(request);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Stream completed");
    }

    private static InputDispatcher inputDispatcher;

    private InputDispatcher(Application application) {
        this.application = application;
    }

    public static synchronized InputDispatcher getInstance(Application application) {
        if (inputDispatcher == null) {
            inputDispatcher = new InputDispatcher(application);
        }
        return inputDispatcher;
    }
}
