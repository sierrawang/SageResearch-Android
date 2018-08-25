package org.sagebionetworks.research.presentation.recorder.reactive;

import org.junit.Test;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;

public class ReactiveFileResultRecorderTest {
    Subscription ssubscription;

    @Test
    public void t() throws InterruptedException {
        CompositeDisposable cd = new CompositeDisposable();
        ConnectableFlowable<Integer> cf = Flowable.fromIterable(Arrays.asList(1, 2))
                .doFinally(() -> {
                    cd.dispose();
                })
                .publish();

        AtomicInteger i = new AtomicInteger();
        Flowable.fromCallable(() -> {
            return i.incrementAndGet();
        });

        Disposable d1 = cf
                .doOnCancel(this::onReactiveDataCancel)
                .doFinally(this::doReactiveDataFinally)
                .doOnSubscribe(this::onReactiveDataSubscribe)
                .subscribe(this::onReactiveDataNext, this::onReactiveDataError,
                        this::onReactiveDataComplete);

        AtomicReference<Disposable> disp = new AtomicReference<>();
        cf.connect(d -> {
            disp.getAndSet(d);
        });

        Thread.sleep(10);

        cd.dispose();
        disp.get().dispose();
        disp.get().dispose();

        disp.get().dispose();

        Thread.sleep(5000);

    }

    private void doReactiveDataFinally() {
        System.out.println("Finally");

    }

    private void onReactiveDataCancel() {
        System.out.println("Cancel");
    }

    private void onReactiveDataComplete() {
        System.out.println("Complete");

    }

    private void onReactiveDataError(Throwable t) {

    }

    private void onReactiveDataNext(Integer i) {
        System.out.println("Next");
    }

    private void onReactiveDataSubscribe(Subscription s) {
        System.out.println("Subscribe");
        ssubscription = s;

    }
}