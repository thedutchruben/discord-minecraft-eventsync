package nl.thedutchruben.discordeventsync.events;

import nl.thedutchruben.discordeventsync.framework.Event;

public class DiscordRoundUpdateEvent extends DiscordEventSyncEvent{
    private Event event;

    public DiscordRoundUpdateEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
