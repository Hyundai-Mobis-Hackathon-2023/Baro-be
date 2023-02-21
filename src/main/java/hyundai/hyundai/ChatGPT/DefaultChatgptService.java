package hyundai.hyundai.ChatGPT;

// import io.github.flashvayne.chatgpt.dto.ChatRequest;
// import io.github.flashvayne.chatgpt.exception.ChatgptException;
// import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import hyundai.hyundai.Config.ChatRequest;
import hyundai.hyundai.Config.ChatgptProperties;
import io.github.flashvayne.chatgpt.dto.ChatResponse;
import io.github.flashvayne.chatgpt.exception.ChatgptException;
// import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class DefaultChatgptService  {

    protected final ChatgptProperties chatgptProperties;

    private final String URL = "https://api.openai.com/v1/completions";

    private final String AUTHORIZATION;

    public DefaultChatgptService(ChatgptProperties chatgptProperties) {
        this.chatgptProperties = chatgptProperties;
        AUTHORIZATION = "Bearer " + chatgptProperties.getApiKey();
    }

    public String sendMessage(String message) {
        ChatRequest chatRequest = new ChatRequest(chatgptProperties.getModel(), message,
                chatgptProperties.getMaxTokens(), chatgptProperties.getTemperature(), chatgptProperties.getTopP());
        ChatResponse chatResponse = this.getResponse(this.buildHttpEntity(chatRequest));
        try {
            return chatResponse.getChoices().get(0).getText();
        } catch (Exception e) {
            log.error("parse chatgpt message error", e);
            throw e;
        }
    }

    public ChatResponse sendChatRequest(ChatRequest chatRequest) {
        return this.getResponse(this.buildHttpEntity(chatRequest));
    }

    public HttpEntity<ChatRequest> buildHttpEntity(ChatRequest chatRequest) {
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Authorization", AUTHORIZATION);
        return new org.springframework.http.HttpEntity<>(chatRequest, headers);
    }

    public ChatResponse getResponse(HttpEntity<ChatRequest> chatRequestHttpEntity) {
        log.info("request url: {}, httpEntity: {}",URL, chatRequestHttpEntity);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChatResponse> responseEntity = restTemplate.postForEntity(URL, chatRequestHttpEntity, ChatResponse.class);
        if (responseEntity.getStatusCode().isError()) {
            log.error("error response status: {}", responseEntity);
            throw new ChatgptException("error response status :" + responseEntity.getStatusCode().value());
        } else {
            log.info("response: {}", responseEntity);
        }
        return responseEntity.getBody();
    }

}
