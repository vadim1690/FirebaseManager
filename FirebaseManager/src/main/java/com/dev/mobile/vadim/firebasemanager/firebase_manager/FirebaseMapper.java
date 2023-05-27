package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class FirebaseMapper {

    public static <T> List<T> dataSnapshotToListMapper(DataSnapshot dataSnapshot, Class<T> valueType) {
        List<T> list = new ArrayList<>();
        dataSnapshot.getChildren().forEach(data -> list.add(data.getValue(valueType)));
        return list;
    }

    public static <T> List<T> applyPredicate(List<T> data, Predicate<T> predicate) {
        return data.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> applySort(List<T> data, Comparator<T> comparator) {
        return data.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static <T> T dataSnapshotToItemMapper(DataSnapshot dataSnapshot, Class<T> valueType) {
        return dataSnapshot.getValue(valueType);
    }

    public static <T> Map<String, Object> listToMapMapper(List<T> list, Function<T, String> keyMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }

}
