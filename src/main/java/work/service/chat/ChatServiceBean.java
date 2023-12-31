package work.service.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import work.dto.chat.MessageDTO;
import work.repository.MemberRepository;
import work.repository.MessageRepository;
import work.repository.UserDetailsRepository;
import work.service.authentication.AuthenticationService;
import work.util.exception.AuthenticationException;
import work.util.exception.CustomException;
import work.util.mapstruct.MessageMapper;

import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceBean implements ChatService {
    private final MessageRepository messageRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final MemberRepository memberRepository;
    private final MessageMapper messageMapper;
    private final AuthenticationService authenticationService;

    public ChatServiceBean(MessageRepository messageRepository, UserDetailsRepository userDetailsRepository, MemberRepository memberRepository, MessageMapper messageMapper, AuthenticationService authenticationService) {
        this.messageRepository = messageRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.memberRepository = memberRepository;
        this.messageMapper = messageMapper;
        this.authenticationService = authenticationService;
    }


    @Override
    public MessageDTO sendMessage(ServerHttpRequest request, UUID eventId, MessageDTO messageDTO) {
        var user = authenticationService.getUserByToken(request);
        var message = messageMapper.fromMessageDTO(messageDTO);
        var member = memberRepository.findMemberByUserIdAndEventId(user.getId(), eventId).orElseThrow(AuthenticationException::new);
        var event = member.getEvent();
        message.setUserId(user.getId());
        message.setEvent(event);
        message.setMember(member);
        message = messageRepository.saveAndFlush(message);
        var response = messageMapper.toMessageDTO(message);
        var userDetails = userDetailsRepository.findDetailsByUserId(user.getId()).orElseThrow(AuthenticationException::new);
        response.getSender().setName(userDetails.getName());
        response.getSender().setLastname(userDetails.getLastName());
        return response;
    }

    @Override
    public List<MessageDTO> getHistory(ServerHttpRequest request, UUID eventId) {
        var messages = messageRepository.findAll();
        var response = messageMapper.toListMessageDTO(messages);
        response = response.stream().peek(r -> {
            var userDetails = userDetailsRepository.findDetailsByUserId(r.getSender().getSenderId()).orElseThrow(() ->
                    new CustomException("USER_NOT_FOUND", HttpStatus.FORBIDDEN));
            r.getSender().setName(userDetails.getName());
            r.getSender().setLastname(userDetails.getLastName());
        }).toList();
        return response;
    }
}
