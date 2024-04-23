package me.idbi.chatapp.eventmanagers;

import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.eventmanagers.interfaces.EventHandler;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class EventManager {

    private final List<Listener> listeners;

    public EventManager() {
        listeners = new ArrayList<Listener>();
    }

    public void registerEvents(Listener... events) {
        listeners.addAll(Arrays.stream(events).toList());
    }

    public void registerEvent(Listener event) {
        listeners.add(event);
    }

    public void callEvent(Event event) {
        for (Listener listener : listeners) {
            for (Method method : listener.getClass().getMethods()) {
                try {
                    if(!method.isAnnotationPresent(EventHandler.class)) continue;
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if(parameterTypes.length == 1) {
                        Class<?> parameter = parameterTypes[0];

                        if(parameter.getCanonicalName().equalsIgnoreCase(event.getClass().getCanonicalName())) {
                            method.setAccessible(true);
                            method.invoke(listener, new Object[]{event});
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
