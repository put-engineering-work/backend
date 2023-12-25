package work.util.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.domain.User;
import work.domain.UserDetails;
import work.dto.user.RequestLoginDTO;
import work.dto.user.RequestUserDTO;
import work.dto.user.userdetails.GetUserDetailsDTO;
import work.dto.user.userdetails.UpdateUserDetailsDTO;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "name", target = "userDetails.name")
    @Mapping(source = "lastname", target = "userDetails.lastName")
    User requestUserDtoToUser(RequestUserDTO requestUserDto);

    User fromRequestUserDto(RequestLoginDTO requestLoginDTO);
    GetUserDetailsDTO getUserDetailsData(UserDetails userDetails);

    UserDetails fromUpdateUserDetails(UpdateUserDetailsDTO updateUserDetailsDTO);
}
