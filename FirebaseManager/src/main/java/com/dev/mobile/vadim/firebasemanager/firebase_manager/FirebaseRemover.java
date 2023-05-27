package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import com.google.firebase.database.DatabaseReference;

import java.util.function.Function;

class FirebaseRemover<T> {
    private final Function<T, String> keyMapper;
    private final DatabaseReference reference;

    public FirebaseRemover(DatabaseReference reference, Function<T, String> keyMapper) {
        this.keyMapper = keyMapper;
        this.reference = reference;
    }

    public void remove(T item) {
        reference.child(keyMapper.apply(item)).removeValue();
    }


}
