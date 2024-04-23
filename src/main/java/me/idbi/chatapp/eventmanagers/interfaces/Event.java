package me.idbi.chatapp.eventmanagers.interfaces;

import lombok.Getter;
import me.idbi.chatapp.Main;

@Getter
public abstract class Event {

     private String name;
     private final boolean async;

     public Event(boolean async) {
          this.async = async;
     }

     public Event() {
          this(false);
     }

     public String getEventName() {
          if (name == null) {
               name = getClass().getSimpleName();
          }
          return name;
     }

     public boolean callEvent() {
          Main.getEventManager().callEvent(this);
          if(this instanceof Cancellable cancel) {
               return !cancel.isCancelled();
          } else {
               return true;
          }
     }

     public final boolean isAsynchronous() {
          return async;
     }

     //protected abstract void execute();
}
