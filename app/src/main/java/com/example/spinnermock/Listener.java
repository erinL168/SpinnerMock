package com.example.spinnermock;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import javax.annotation.Nullable;

// Callback Interface
public interface Listener {
    void onSuccess(String data, @Nullable List<Object> objectModel);
    void onFailure(String data);
    void onComplete(String data);
}
