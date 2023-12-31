package work.util.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.domain.Message;
import work.dto.chat.MessageDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(source = "userId", target = "sender.senderId")
    MessageDTO toMessageDTO(Message message);

    @Mapping(source = "userId", target = "sender.senderId")
    List<MessageDTO> toListMessageDTO(List<Message> messages);

    Message fromMessageDTO(MessageDTO messageDTO);
}
