package nl.thedutchruben.discordeventsync.events;


import nl.thedutchruben.discordeventsync.framework.Event;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DiscordEventsUpdateEvent extends DiscordEventSyncEvent{
    private LinkedList<Event> events;


    public DiscordEventsUpdateEvent(LinkedList<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
