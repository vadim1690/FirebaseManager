package com.dev.mobile.vadim.firebasemanager.firebase_manager;


@FunctionalInterface
public interface TwoParamFunction<T, U, R> {
    R apply(T t, U u);
}