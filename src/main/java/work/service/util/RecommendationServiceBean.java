package work.service.util;

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
            for (var event : events) {
                event.getCategories().forEach(c -> {
                    if (!categories.contains(c.getName()))
                        categories.add(c.getName());
                });
            }
            recommendedEvents = eventRepository.findRecommendedEvents(categories, user.getId());
        }
    }
}
