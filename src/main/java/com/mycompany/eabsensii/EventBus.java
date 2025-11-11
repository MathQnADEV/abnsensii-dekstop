/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaqy
 */
public class EventBus {

    private static final List<DataChangeListener> listeners = new ArrayList<>();

    public interface DataChangeListener {

        void onDataChanged();
    }

    public static void subscribe(DataChangeListener listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(DataChangeListener listener) {
        listeners.remove(listener);
    }

    public static void notifyDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged();
        }
    }
}
