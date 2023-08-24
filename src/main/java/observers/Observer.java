package observers;

import engine.GameObject;
import observers.events.Event;

public interface Observer {
    void onNotify(GameObject gameObject, Event event);
}
