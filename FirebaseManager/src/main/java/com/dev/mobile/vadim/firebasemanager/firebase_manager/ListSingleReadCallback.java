package com.dev.mobile.vadim.firebasemanager.firebase_manager;

import java.util.List;

public interface ListSingleReadCallback<T> {
    void onListRead(List<T> item);
}
