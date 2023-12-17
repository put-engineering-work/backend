package work.util.mapstruct;

import org.mapstruct.Mapper;
import work.domain.Member;
import work.dto.event.get.certainevent.MembersForUserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    List<MembersForUserDto> toMemberForUserDtoList(List<Member> members);
}
