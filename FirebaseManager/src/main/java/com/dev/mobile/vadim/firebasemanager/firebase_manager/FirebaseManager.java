package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class FirebaseManager<T> {


    private final FirebaseReader<T> firebaseReader;
    private final FirebaseWriter<T> firebaseWriter;
    private final FirebaseRemover<T> firebaseRemover;


    public FirebaseManager(String tableName, Class<T> valueType, Function<T, String> keyMapper) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(tableName);
        firebaseReader = new FirebaseReader<>(reference, valueType, keyMapper);
        firebaseWriter = new FirebaseWriter<>(reference, keyMapper);
        firebaseRemover = new FirebaseRemover<>(reference, keyMapper);
    }


    public LiveData<List<T>> getListLiveData() {
        return firebaseReader.readListLiveData();
    }

    public LiveData<List<T>> getListLiveData(Comparator<T> comparator) {
        return firebaseReader.readListLiveData(comparator);
    }

    public LiveData<List<T>> getListLiveData(Predicate<T> predicate) {
        return firebaseReader.readListLiveData(predicate);
    }

    public LiveData<List<T>> getListLiveData(Predicate<T> predicate, Comparator<T> comparator) {
        return firebaseReader.readListLiveData(predicate, comparator);

    }


    public LiveData<List<T>> getListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables) {
        return firebaseReader.readListLiveData(keyMappers, resultMappers, tables);
    }


    public LiveData<List<T>> getListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Comparator<T> comparator) {
        return firebaseReader.readListLiveData(keyMappers, resultMappers, tables, comparator);
    }

    public LiveData<List<T>> getListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Predicate<T> predicate) {
        return firebaseReader.readListLiveData(keyMappers, resultMappers, tables, predicate);
    }

    public LiveData<List<T>> getListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables,  Predicate<T> predicate,Comparator<T> comparator) {
        return firebaseReader.readListLiveData(keyMappers, resultMappers, tables, predicate, comparator);
    }


    public void singleItemRead(T item, ItemSingleReadCallback<T> callback) {
        if(item == null){
            callback.onItemRead(null);
            return;
        }
        firebaseReader.singleItemReadWithCallback(item, callback);
    }

    public void singleItemReadById(String id, ItemSingleReadCallback<T> callback) {
        if(id == null){
            callback.onItemRead(null);
            return;
        }
        firebaseReader.readSingleItemByIdWithCallback(id, callback);
    }

    public void singleListRead(ListSingleReadCallback<T> callback) {
        firebaseReader.singleListCallbackRead(callback);
    }

    public LiveData<T> getItemLiveData(T item) {
        return firebaseReader.readItemLiveData(item);
    }

    public LiveData<T> getItemByIdLiveData(String id) {
        return firebaseReader.readItemLiveDataById(id);
    }

    public void writeList(List<T> list) {
        firebaseWriter.writeMap(list);
    }


    public void writeItem(T item) {
        firebaseWriter.writeObject(item);
    }

    public void writeItem(T item, DatabaseReference.CompletionListener callback) {
        firebaseWriter.writeObject(item,callback);
    }

    public void remove(T item) {
        firebaseRemover.remove(item);
    }
}