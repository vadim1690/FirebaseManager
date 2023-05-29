package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

class FirebaseWriter<T> {


    private final Function<T, String> keyMapper;
    private final DatabaseReference reference;


    public FirebaseWriter(DatabaseReference reference, Function<T, String> keyMapper) {
        this.keyMapper = keyMapper;
        this.reference = reference;
    }

    // generic util function for firebase write operations
    public void writeMap(List<T> list) {
        Map<String, Object> updateMap = FirebaseMapper.listToMapMapper(list, keyMapper);
        reference.updateChildren(updateMap);
    }

    public void writeObject(T obj) {
        reference.child(keyMapper.apply(obj)).setValue(obj);
    }

    public void writeObject(T obj, DatabaseReference.CompletionListener callback) {
        reference.child(keyMapper.apply(obj)).setValue(obj,callback);
    }

}
