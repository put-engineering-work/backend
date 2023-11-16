package work.util.mapstruct;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import work.domain.Event;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event fromCreateDto(EventCreateDto eventCreateDto);

    @Mapping(target = "latitude", expression = "java(event.getLocation().getX())")
    @Mapping(target = "longitude", expression = "java(event.getLocation().getY())")
    EventsInRadiusDto eventToEventsInRadiusDto(Event event);

    @AfterMapping
    default void setEventLocation(@MappingTarget Event event, EventCreateDto eventCreateDto) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point location = geometryFactory.createPoint(new Coordinate(eventCreateDto.longitude(), eventCreateDto.latitude()));
        event.setLocation(location);
    }
}
