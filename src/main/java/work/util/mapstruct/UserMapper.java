package work.util.mapstruct;


import org.mapstruct.Mapper;
import work.domain.User;
import work.domain.UserDetails;
import work.dto.user.RequestUserDTO;
import work.dto.user.userdetails.GetUserDetailsDTO;
import work.dto.user.userdetails.UpdateUserDetailsDTO;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User requestUserDtoToUser(RequestUserDTO requestUserDto);

    GetUserDetailsDTO getUserDetailsData(UserDetails userDetails);

    UserDetails fromUpdateUserDetails(UpdateUserDetailsDTO updateUserDetailsDTO);
}
