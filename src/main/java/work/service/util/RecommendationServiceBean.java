package work.service.util;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.domain.Event;
import work.repository.EventRepository;
import work.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationServiceBean implements RecommendationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RecommendationServiceBean(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void makeRecommendations() {
        var users = userRepository.findAll();
        for (var user : users) {
            List<Event> recommendedEvents = new ArrayList<>();
            var events = eventRepository.findLastNEventsForUser(user.getId(), 10);
            List<String> categories = new ArrayList<>();
            var locations = new ArrayList<>();
            for (var event : events) {
                event.getCategories().forEach(c -> {
                    locations.add(event.getLocation());
                    if (!categories.contains(c.getName()))
                        categories.add(c.getName());
                });
            }
            recommendedEvents = eventRepository.findRecommendedEvents(categories, user.getId());
            List<Event> eventsWithin10Km = new ArrayList<>();

            for (var recommendedEvent : recommendedEvents) {
                Point recommendedEventPoint = recommendedEvent.getLocation();

                double recommendedEventLat = recommendedEventPoint.getX();
                double recommendedEventLon = recommendedEventPoint.getY();

                for (var event : events) {
                    Point eventPoint = event.getLocation();

                    double eventLat = eventPoint.getX();
                    double eventLon = eventPoint.getY();

                    double distance = calculateDistance(recommendedEventLat, recommendedEventLon, eventLat, eventLon);

                    if (distance <= 10) {
                        eventsWithin10Km.add(event);
                    }

                }
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}
