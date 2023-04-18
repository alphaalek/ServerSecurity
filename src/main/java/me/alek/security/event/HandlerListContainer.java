package me.alek.security.event;

import lombok.Getter;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HandlerListContainer {

    private static HandlerListContainer instance;
    private static boolean isAssigned = false;

    public static synchronized HandlerListContainer singletonInstance(int handlerListSize) {
        if (instance == null) {
            instance = new HandlerListContainer(handlerListSize);
            isAssigned = true;
        }
        return instance;
    }

    @Getter private final int handlerListSize;
    @Getter private final List<Long> currentIds = new ArrayList<>();
    @Getter private final HashMap<Long, List<RegisteredListener>> handlerIdMap = new HashMap<>();

    public HandlerListContainer(int handlerListSize) {
        this.handlerListSize = handlerListSize;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public long assignId(RegisteredListener listener) {
        for (long id : currentIds) {
            List<RegisteredListener> listenersAtId = handlerIdMap.get(id);
            if (listenersAtId == null || listenersAtId.isEmpty()) {
                clearId(id);
                continue;
            }
            if (listenersAtId.contains(listener)) continue;

            listenersAtId.add(listener);
            if (listenersAtId.size() == handlerListSize) {
                currentIds.remove(id);
            }
            return id;
        }
        long id = generateId();
        currentIds.add(id);
        handlerIdMap.put(id, new ArrayList<>());
        handlerIdMap.get(id).add(listener);
        return id;
    }

    private long generateId() {
        return new Random().nextLong();
    }

    public void removeFromMap(long id, RegisteredListener listener) {
        List<RegisteredListener> getListenersAtId = handlerIdMap.get(id);
        if (getListenersAtId == null) return;
        if (!getListenersAtId.contains(listener)) return;
        getListenersAtId.remove(listener);
        if (getListenersAtId.size() == 0) {
            clearId(id);
        }
    }

    public List<RegisteredListener> getListeners(long id) {
        return handlerIdMap.get(id);
    }

    public void clearId(long id) {
        handlerIdMap.remove(id);
        currentIds.remove(id);
    }
}
