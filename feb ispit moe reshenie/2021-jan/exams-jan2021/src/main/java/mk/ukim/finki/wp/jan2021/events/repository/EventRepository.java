package mk.ukim.finki.wp.jan2021.events.repository;

import mk.ukim.finki.wp.jan2021.events.model.Event;
import mk.ukim.finki.wp.jan2021.events.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findAllByPriceLessThanAndTypeEquals(Double price, EventType type);
    List<Event> findAllByPriceLessThan(Double price);
    List<Event> findAllByTypeEquals(EventType type);
}
