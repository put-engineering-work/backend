package work.util.mapstruct;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import work.tutor.domain.User;
import work.tutor.dto.tutor.RequestUserDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-01T21:29:27+0200",
    comments = "version: 1.5.1.Final, compiler: javac, environment: Java 17.0.8 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User requestTutorDtoToTutor(RequestUserDto requestUserDto) {
        if ( requestUserDto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( requestUserDto.getEmail() );
        user.setPassword( requestUserDto.getPassword() );

        return user;
    }
}
