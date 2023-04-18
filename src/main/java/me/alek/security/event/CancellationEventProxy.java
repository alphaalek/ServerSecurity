package me.alek.security.event;

import lombok.Getter;
import me.alek.security.event.wrappers.WrappedEventController;
import me.alek.security.event.wrappers.WrappedUniqueRegisteredListener;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CancellationEventProxy<EVENT extends Event> {
    interface CancelListener<EVENT extends Event> {
        void onCancelled(RegisteredListener registeredListener, EVENT event);
    }

    @Getter private static HandlerListContainer handlerListContainer;
    private final Class<EVENT> clazz;
    private final List<CancelListener<EVENT>> listeners = new ArrayList<>();
    private final boolean controlMultipleCancellers;
    private EventController<EVENT> eventController;
    private EnumMap<EventPriority, ArrayList<RegisteredListener>> backup;

    public CancellationEventProxy(Class<EVENT> clazz, boolean controlMultipleCancellers) {
        this.clazz = clazz;
        this.controlMultipleCancellers = controlMultipleCancellers;
        if (controlMultipleCancellers) {
            this.eventController = new EventController<>();
        }
        injectProxy();
    }

    public void addListener(CancelListener<EVENT> listener) {
        listeners.add(listener);
    }

    public void removeListener(CancelListener<Event> listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    private EnumMap<EventPriority, ArrayList<RegisteredListener>> getSlots(HandlerList list) throws Exception {
        return (EnumMap<EventPriority, ArrayList<RegisteredListener>>) getSlotsField(list).get(list);
    }

    private Field getSlotsField(HandlerList list) throws Exception {
        Field slotField = list.getClass().getDeclaredField("handlerslots");
        slotField.setAccessible(true);
        return slotField;
    }

    private void injectProxy() {
        HandlerList list;
        Map<EventPriority, ArrayList<RegisteredListener>> slots;
        try {
            list = getHandlerList(clazz);
            slots = getSlots(list);
        } catch (Exception e) {
            return;
        }
        backup = slots.clone();

        synchronized (list) {
            for (EventPriority p : slots.keySet().toArray(new EventPriority[0])) {
                final EventPriority priority = p;
                final ArrayList<RegisteredListener> proxyList = new ArrayList<RegisteredListener>() {
                    @Override
                    public boolean add(RegisteredListener e) {
                        super.add(injectRegisteredListener(e));
                        return backup.get(priority).add(e);
                    }

                    @Override
                    public boolean remove(Object listener) {
                        // Remove this listener
                        for (Iterator<RegisteredListener> it = iterator(); it.hasNext(); ) {
                            WrappedUniqueRegisteredListener delegated = (WrappedUniqueRegisteredListener) it.next();
                            if (delegated.delegate == listener) {
                                it.remove();
                                break;
                            }
                        }
                        return backup.get(priority).remove(listener);
                    }
                };
                for (RegisteredListener listener : backup.get(priority)) {
                    proxyList.add(listener);
                }
                slots.put(priority, proxyList);
            }
        }
    }

    private RegisteredListener injectRegisteredListener(final RegisteredListener listener) {

        return new WrappedUniqueRegisteredListener(listener) {
            @Override
            public void callEvent(Event event) throws EventException {

                WrappedEventController controllerEvent = null;
                if (handlerListContainer == null || !handlerListContainer.isAssigned()) {
                    handlerListContainer = HandlerListContainer.singletonInstance(event.getHandlers().getRegisteredListeners().length);
                }

                long id = handlerListContainer.assignId(listener);
                setId(id);
                final List<RegisteredListener> listeners = handlerListContainer.getListeners(this.getId());
                if (listeners.size() == 1) {
                    controllerEvent = new WrappedEventController(listener, this.getId(), ControllerType.THREAD_START);
                } else if (listeners.size() == handlerListContainer.getHandlerListSize()) {
                    controllerEvent = new WrappedEventController(listener, this.getId(), ControllerType.CALLBACK);
                }

                if (!(event instanceof Cancellable)) {
                    listener.callEvent(event);
                    return;
                }

                final boolean prior = getCancelState(event);
                listener.callEvent(event);

                if (!prior && getCancelState(event)) {
                    invokeCancelled(this, (EVENT) event);

                    if (controlMultipleCancellers) {
                        ((Cancellable) event).setCancelled(false);
                        eventController.addCancelOnCallback((EVENT) event);
                    }
                }
                if (controllerEvent != null) {
                    Bukkit.getPluginManager().callEvent(controllerEvent);
                }
            }
        };
    }

    private void invokeCancelled(RegisteredListener registeredListener, EVENT event) {
        for (CancelListener<EVENT> listener : listeners) {
            listener.onCancelled(registeredListener, event);
        }
    }

    private boolean getCancelState(Event event) {
        return ((Cancellable) event).isCancelled();
    }

    public void close() {
        if (backup != null) {
            try {
                HandlerList list = getHandlerList(clazz);
                getSlotsField(list).set(list, backup);

                Field handlers = list.getClass().getDeclaredField("handlers");
                handlers.setAccessible(true);
                handlers.set(list, null);

            } catch (Exception e) {
            }
            backup = null;
        }
    }

    private static HandlerList getHandlerList(Class<? extends Event> clazz) throws Exception {
        while (clazz.getSuperclass() != null && Event.class.isAssignableFrom(clazz.getSuperclass())) {
            try {
                Method method = clazz.getDeclaredMethod("getHandlerList");
                method.setAccessible(true);
                return (HandlerList) method.invoke(null);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass().asSubclass(Event.class);
            }
        }
        throw new Exception();
    }
}
