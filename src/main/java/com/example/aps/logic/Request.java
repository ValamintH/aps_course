package com.example.aps.logic;

public class Request {

    private final Integer sourceId;
    private final Integer step;
    private final Long startTime;
    private Long startTimeInBuffer = 0L;
    private Long timeInSystem = 0L;
    private Long timeInBuffer = 0L;

    public Request(Integer sourceId, Integer step) {
        this.sourceId = sourceId;
        this.step = step;
        this.startTime = System.currentTimeMillis();
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getStep() {
        return step;
    }

    @Override
    public String toString() {
        return "s" + sourceId.toString();
    }

    public void countTimeInSystem(Long currentTime) {
        timeInSystem = currentTime - startTime;
    }

    public void countTimeInBuffer(Long currentTime) {
        timeInBuffer = currentTime - startTimeInBuffer;
    }

    public Long getTimeInSystem() {
        return timeInSystem;
    }

    public Long getTimeInBuffer() {
        return timeInBuffer;
    }

    public void setStartTimeInBuffer(Long startTimeInBuffer) {
        this.startTimeInBuffer = startTimeInBuffer;
    }
}
