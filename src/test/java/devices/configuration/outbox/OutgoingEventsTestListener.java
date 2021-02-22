package devices.configuration.outbox;

import devices.configuration.JsonAssert;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.awaitility.Awaitility;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class OutgoingEventsTestListener {

    public volatile Queue<String> events = new ConcurrentLinkedDeque<>();

    @KafkaListener(groupId = "outbox-tests", topics = {
            "station-configuration",
            "station-configuration-station-snapshot-v1"
    })
    void listen(@Payload String payloadAsJson) {
        System.out.println("RECEIVED: " + payloadAsJson);
        events.add(payloadAsJson);
    }

    public static EventAssert event() {
        return new EventAssert();
    }

    private ArrayList<String> await(Consumer<List<String>> check) {
        var current = new ArrayList<String>();
        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> check.accept(accumulate(current)));
        return current;
    }

    @NotNull
    private ArrayList<String> accumulate(ArrayList<String> current) {
        var next = events.poll();
        if (next != null) current.add(next);
        return current;
    }

    public void clear() {
        events = new ConcurrentLinkedDeque<>();
    }

    public void hasExactly(int times, EventAssert expected) {
        await(actual -> Assertions.assertThat(actual)
                .areExactly(times, new HamcrestCondition<>(Matchers.allOf(expected.matchers)))
        );
    }

    public Optional<String> waitOn(EventAssert expected) {
        Predicate<String> predicate = expected.toPredicate();
        ArrayList<String> list = await(actual -> Assertions.assertThat(actual).anyMatch(predicate));
        return list.stream().filter(predicate).findFirst();
    }

    public void last(String expected) {
        await(actual -> {
            Assertions.assertThat(actual).hasSizeGreaterThan(0);
            String last = actual.get(actual.size() - 1);
            JsonAssert.assertThat(last).hasFieldsLike(expected);
        });
    }

    public static class EventAssert {
        List<Matcher<? super String>> matchers = new ArrayList<>();

        public EventAssert ofTypeStationLocationUpdated() {
            return ofType("StationLocationUpdated");
        }

        public EventAssert ofType(String expected) {
            matchers.add(Matchers.containsString("\"type\":\"" + expected + "\""));
            return this;
        }

        public EventAssert withStationName(String expected) {
            matchers.add(Matchers.containsString("\"stationName\":\"" + expected + "\""));
            return this;
        }

        public EventAssert withVersion(String expected) {
            matchers.add(Matchers.containsString("\"version\":\"" + expected + "\""));
            return this;
        }

        public Predicate<String> toPredicate() {
            return e -> Matchers.allOf(matchers).matches(e);
        }
    }
}
