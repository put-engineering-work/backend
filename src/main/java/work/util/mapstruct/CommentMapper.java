package work.util.mapstruct;

import org.mapstruct.Mapper;
import work.domain.Comment;
import work.dto.event.create.CreateCommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment fromCreateCommentDto(CreateCommentDto createCommentDto);
}
