package work.util.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.user.domain.User;
import work.user.domain.UserDetails;
import work.user.dto.user.RequestUserDTO;
import work.user.dto.user.userdetails.GetUserDetailsDTO;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User requestUserDtoToUser(RequestUserDTO requestUserDto);

    GetUserDetailsDTO getUserDetailsData(UserDetails userDetails);
}
