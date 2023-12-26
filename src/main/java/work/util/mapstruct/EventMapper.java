package work.util.mapstruct;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.*;
import work.domain.Event;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.certainevent.CertainEventDto;


@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "location", source = "eventCreateDto", qualifiedByName = "toPoint")
    Event fromCreateDto(EventCreateDto eventCreateDto);

    @Mapping(target = "longitude", expression = "java(event.getLocation().getX())")
    @Mapping(target = "latitude", expression = "java(event.getLocation().getY())")
    EventsInRadiusDto eventToEventsInRadiusDto(Event event);

    @Named("toPoint")
    default Point toPoint(EventCreateDto eventCreateDto) {
        if (eventCreateDto == null || eventCreateDto.latitude() == null || eventCreateDto.longitude() == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(eventCreateDto.longitude(), eventCreateDto.latitude()));
    }

//    @AfterMapping
//    default void compressAndLinkImages(@MappingTarget Event event, EventCreateDto eventCreateDto, UtilService utilService) {
//        if (eventCreateDto.startDate() != null) {
//            event.setStartDate(ZonedDateTime.parse(eventCreateDto.startDate()));
//        }
//        if (eventCreateDto.endDate() != null) {
//            event.setEndDate(ZonedDateTime.parse(eventCreateDto.endDate()));
//        }
//        if (eventCreateDto.photos() != null) {
//            Set<EventImage> eventImages = eventCreateDto.photos().stream()
//                    .map(photo -> {
//                        byte[] compressedImage = utilService.compressImage(photo, 0.75f);
//                        return EventImage.builder()
//                                .image(compressedImage)
//                                .event(event)
//                                .build();
//                    })
//                    .collect(Collectors.toSet());
//
//            event.setEventImages(eventImages);
//        }
//    }

    @Mapping(target = "longitude", expression = "java(event.getLocation().getX())")
    @Mapping(target = "latitude", expression = "java(event.getLocation().getY())")
    CertainEventDto toCertainEventDto(Event event);
}
