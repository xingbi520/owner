package com.shendun.architecture.net.schedulers;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchedulerProvider implements BaseSchedulerProvider {

    private SchedulerProvider() {
    }

    private static class SingleTonHoler {
        private static SchedulerProvider INSTANCE = new SchedulerProvider();
    }

    public static SchedulerProvider getInstance() {
        return SingleTonHoler.INSTANCE;
    }

    @Override
    @NonNull
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    @NonNull
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    @NonNull
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(io()).observeOn(ui());
    }
}