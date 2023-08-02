package work.util.mapstruct;


import org.mapstruct.Mapper;
import work.user.domain.User;
import work.user.dto.user.RequestUserDto;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User requestTutorDtoToTutor(RequestUserDto requestUserDto);
}
