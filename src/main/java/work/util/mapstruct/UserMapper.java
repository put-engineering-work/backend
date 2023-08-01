package work.util.mapstruct;


import org.mapstruct.Mapper;
import work.tutor.domain.User;
import work.tutor.dto.tutor.RequestUserDto;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User requestTutorDtoToTutor(RequestUserDto requestUserDto);
}
