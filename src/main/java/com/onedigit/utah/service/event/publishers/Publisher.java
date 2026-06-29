package com.onedigit.utah.service.event.publishers;

import reactor.core.publisher.Sinks;

public abstract class Publisher<T> {
    protected final Sinks.Many<T> sink = Sinks.many().multicast().directBestEffort();
    protected void emit(T data) {
        sink.emitNext(data, (t, r) -> r == Sinks.EmitResult.FAIL_NON_SERIALIZED);
    }
    public void publish(T data) { emit(data); }
}
