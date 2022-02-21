package nl.thedutchruben.discordeventsync.events;


import nl.thedutchruben.discordeventsync.framework.Event;

import java.util.LinkedList;
import java.util.List;

public class DiscordEventCreateEvent extends DiscordEventSyncEvent{
    private Event event;

    public DiscordEventCreateEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
