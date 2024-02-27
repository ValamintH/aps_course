package com.example.aps;

import com.example.aps.logic.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Mode mode = Mode.AUTO;
        Application application = new Application(10, 3, 5);
        application.setMode(mode);
        Generator generator = new Generator(application, 10, 0.5);
        generator.run();
        application.getStatistics().printStatistics();
    }
}
