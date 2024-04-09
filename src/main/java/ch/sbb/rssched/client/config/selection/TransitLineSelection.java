package ch.sbb.rssched.client.config.selection;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a selection of transit lines and associated routes.
 * <p>
 * Allows for the addition and retrieval of lines and routes by their identifiers. The transit lines can optionally be
 * associated to a group.
 *
 * @author munterfi
 */
@Log4j2
@NoArgsConstructor
public class TransitLineSelection implements Iterable<TransitLineSelection.Entry> {
    public static final String NO_GROUP = "N/A";
    private final Map<Id<TransitLine>, String> lines = new HashMap<>();
    private final Map<Id<TransitLine>, Set<Id<TransitRoute>>> routes = new HashMap<>();

    public void add(String group, Id<TransitLine> lineId, Id<TransitRoute> routeId) {
        checkAndAddLine(group, lineId);
        routes.computeIfAbsent(lineId, ignored -> new HashSet<>()).add(routeId);
        log.debug("Added route {} ({} : {}) to selection", routeId, lineId, group);
    }

    public void add(String group, Id<TransitLine> lineId, Set<Id<TransitRoute>> routeIds) {
        checkAndAddLine(group, lineId);
        routes.computeIfAbsent(lineId, ignored -> new HashSet<>()).addAll(routeIds);
        log.debug("Added routes {} to line ID {} with group {}", routeIds, lineId, group);
    }

    private void checkAndAddLine(String group, Id<TransitLine> lineId) {
        if (lines.containsKey(lineId) && !lines.get(lineId).equals(group)) {
            throw new IllegalArgumentException("Line ID " + lineId + " is already associated with a different group.");
        }
        lines.put(lineId, group);
    }

    public Set<Id<TransitLine>> getLineIds() {
        return new HashSet<>(lines.keySet());
    }

    @Override
    @Nonnull
    public Iterator<Entry> iterator() {
        return new TransitLineSelectionIterator();
    }

    public record Entry(String group, Id<TransitLine> lineId, Set<Id<TransitRoute>> routeIds) {
    }

    private class TransitLineSelectionIterator implements Iterator<Entry> {
        private final Iterator<Map.Entry<Id<TransitLine>, String>> lineIterator;

        public TransitLineSelectionIterator() {
            this.lineIterator = lines.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return lineIterator.hasNext();
        }

        @Override
        public Entry next() {
            if (!lineIterator.hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<Id<TransitLine>, String> lineEntry = lineIterator.next();
            Set<Id<TransitRoute>> routeSet = routes.getOrDefault(lineEntry.getKey(), Collections.emptySet());
            return new Entry(lineEntry.getValue(), lineEntry.getKey(), routeSet);
        }
    }

}
