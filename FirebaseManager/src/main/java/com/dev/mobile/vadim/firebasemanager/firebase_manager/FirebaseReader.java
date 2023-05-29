package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;

class FirebaseReader<T> {

    private final int NUM_OF_THREAD = 4;

    private final DatabaseReference reference;

    private final Function<T, String> keyMapper;
    private final Class<T> valueType;

    private final MediatorLiveData<List<T>> listLiveData;
    private final MediatorLiveData<List<T>> queryListLiveData;
    private final MediatorLiveData<List<T>> sortedlistLiveData;
    private final MediatorLiveData<List<T>> sortQueryListLiveData;
    private final MediatorLiveData<List<T>> chainedListLiveData;
    private final MediatorLiveData<List<T>> chainedQueryListLiveData;
    private final MediatorLiveData<List<T>> chainedSortedlistLiveData;
    private final MediatorLiveData<List<T>> chainedSortQueryListLiveData;
    private final MediatorLiveData<T> singleItemLiveData;


    public FirebaseReader(DatabaseReference reference, Class<T> valueType, Function<T, String> keyMapper) {
        this.keyMapper = keyMapper;
        this.valueType = valueType;
        this.reference = reference;

        listLiveData = new MediatorLiveData<>();
        queryListLiveData = new MediatorLiveData<>();
        sortedlistLiveData = new MediatorLiveData<>();
        sortQueryListLiveData = new MediatorLiveData<>();
        chainedListLiveData = new MediatorLiveData<>();
        chainedQueryListLiveData = new MediatorLiveData<>();
        chainedSortedlistLiveData = new MediatorLiveData<>();
        chainedSortQueryListLiveData = new MediatorLiveData<>();
        singleItemLiveData = new MediatorLiveData<>();
    }

    public LiveData<T> readItemLiveDataById(String id) {
        return queryItemRead(id);
    }

    public LiveData<T> readItemLiveData(T item) {
        return queryItemRead(keyMapper.apply(item));
    }

    public void singleListCallbackRead(ListSingleReadCallback<T> callback) {
        singleReadList(callback);
    }

    public void readSingleItemByIdWithCallback(String id, ItemSingleReadCallback<T> callback) {
        singleReadItem(id, callback);
    }

    public void singleItemReadWithCallback(T item, ItemSingleReadCallback<T> callback) {
        singleReadItem(keyMapper.apply(item), callback);
    }

    public LiveData<List<T>> readListLiveData() {
        return queryRead(listLiveData, null, null);
    }

    public LiveData<List<T>> readListLiveData(Comparator<T> comparator) {
        return queryRead(sortedlistLiveData, null, comparator);
    }

    public LiveData<List<T>> readListLiveData(Predicate<T> predicate) {
        return queryRead(queryListLiveData, predicate, null);
    }


    public LiveData<List<T>> readListLiveData(Predicate<T> predicate, Comparator<T> comparator) {
        return queryRead(sortQueryListLiveData, predicate, comparator);
    }


    public LiveData<List<T>> readListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables) {
        return chainedQueryRead(chainedListLiveData, keyMappers, resultMappers, tables, null, null);
    }

    public LiveData<List<T>> readListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Comparator<T> comparator) {
        return chainedQueryRead(chainedSortedlistLiveData, keyMappers, resultMappers, tables, null, comparator);
    }


    public LiveData<List<T>> readListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Predicate<T> predicate) {
        return chainedQueryRead(chainedQueryListLiveData, keyMappers, resultMappers, tables, predicate, null);
    }


    public LiveData<List<T>> readListLiveData(List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Predicate<T> predicate, Comparator<T> comparator) {
        return chainedQueryRead(chainedSortQueryListLiveData, keyMappers, resultMappers, tables, predicate, comparator);
    }


    private void singleReadItem(String id, ItemSingleReadCallback<T> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            reference.child(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onItemRead(FirebaseMapper.dataSnapshotToItemMapper(task.getResult(), valueType)));
                }
            });
        });

    }

    private void singleReadList(ListSingleReadCallback<T> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onListRead(FirebaseMapper.dataSnapshotToListMapper(task.getResult(), valueType)));
                }
            });
        });
    }


    private LiveData<T> queryItemRead(String id) {
        singleItemLiveData.addSource(new FirebaseQueryLiveData(reference.child(id)), dataSnapshot -> {
            if (dataSnapshot != null) {
                Executors.newFixedThreadPool(NUM_OF_THREAD).execute(() -> {
                    T data = FirebaseMapper.dataSnapshotToItemMapper(dataSnapshot, valueType);
                    singleItemLiveData.postValue(data);
                });
            }
        });
        return singleItemLiveData;
    }


    private LiveData<List<T>> queryRead(MediatorLiveData<List<T>> liveData, Predicate<T> predicate, Comparator<T> comparator) {
        liveData.addSource(new FirebaseQueryLiveData(reference), dataSnapshot -> {
            if (dataSnapshot != null) {
                Executors.newFixedThreadPool(NUM_OF_THREAD).execute(() -> {
                    List<T> data = FirebaseMapper.dataSnapshotToListMapper(dataSnapshot, valueType);

                    if (predicate != null)
                        data = FirebaseMapper.applyPredicate(data, predicate);
                    if (comparator != null)
                        data = FirebaseMapper.applySort(data, comparator);

                    liveData.postValue(data);
                });
            }
        });
        return liveData;
    }

    private LiveData<List<T>> chainedQueryRead(MediatorLiveData<List<T>> liveData, List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables, Predicate<T> predicate, Comparator<T> comparator) {
        liveData.addSource(new FirebaseQueryLiveData(reference), dataSnapshot -> {
            if (dataSnapshot != null) {
                Executors.newFixedThreadPool(NUM_OF_THREAD).execute(() -> {
                    List<T> data = FirebaseMapper.dataSnapshotToListMapper(dataSnapshot, valueType);
                    data = getChainedReadCalls(data, keyMappers, resultMappers, tables);
                    if (predicate != null)
                        data = FirebaseMapper.applyPredicate(data, predicate);
                    if (comparator != null)
                        data = FirebaseMapper.applySort(data, comparator);
                    liveData.postValue(data);
                });
            }
        });
        return liveData;
    }


    private List<T> getChainedReadCalls(List<T> data, List<Function<T, String>> keyMappers, List<TwoParamFunction<DataSnapshot, T, T>> resultMappers, List<String> tables) {
        CountDownLatch latch = new CountDownLatch(keyMappers.size() * data.size());

        for (int i = 0; i < keyMappers.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                String id;
                try {
                    id = keyMappers.get(i).apply(data.get(j));
                } catch (Exception e) {
                    continue;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(tables.get(i)).child(id);
                int finalI = i;
                int finalJ = j;
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            resultMappers.get(finalI).apply(snapshot, data.get(finalJ));
                        } catch (Exception e) {
                            latch.countDown();
                            return;
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        latch.countDown();
                    }
                });
            }
        }

        try {
            latch.await(); // Wait for all database calls to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

}
