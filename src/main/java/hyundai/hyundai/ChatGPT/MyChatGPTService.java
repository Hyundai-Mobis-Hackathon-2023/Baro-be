package hyundai.hyundai.ChatGPT;

import hyundai.hyundai.ChatGPT.model.ChatGptRes;
import hyundai.hyundai.ExceptionHandler.BaseException;
import hyundai.hyundai.ExceptionHandler.BaseResponseStatus;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MyChatGPTService {
    private ChatgptService chatgptService;

    @Autowired
    public MyChatGPTService(ChatgptService chatgptService){
        this.chatgptService = chatgptService;
    }

    @Value("${openai.api-key}")
    private String apikey;

    List<String> keywordList = Arrays.asList("의료", "거주", "운송", "스토어", "오피스", "엔터테인먼트");

    public ChatGptRes getChatResponse(String prompt) throws BaseException {
        try {
            String responseMessage = chatgptService.sendMessage(prompt);
            // headers.set("Authorization", "Bearer " + apikey);
            String category = getKeywordInAnswer(responseMessage);
            String resultMessage = parsingResponseMessage(responseMessage);
            String splitResultMessage = splitMessage(resultMessage);
            return new ChatGptRes(splitResultMessage, category);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    // chatGPT 가 리턴해주는 답변 문자열에서 특정 키워드(카테고리) 추출
    public String getKeywordInAnswer(String responseMessage){
        String result = null;
        for (String keyword :keywordList){
            if(responseMessage.contains(keyword)){
                // result = keyword;
                if(keyword == "의료"){
                    result = "medical";
                }
                else if(keyword == "거주"){
                    result = "living";
                }
                else if(keyword == "엔터테인먼트"){
                    result = "entertainment";
                }
                else if(keyword == "운송"){
                    result = "transit";
                }
                else if(keyword == "스토어"){
                    result = "store";
                }
                else if(keyword == "오피스"){
                    result = "office";
                }
                break;
            }
        }
        if(result == null){  // 8개의 키워드중에 아무것도 해당 안되면 "커스텀" 리턴
            result = "커스텀";
        }
        return result;
    }

    public String parsingResponseMessage(String responseMessage){
        responseMessage = responseMessage.toLowerCase();
        String parsingResult = responseMessage;
        if(responseMessage.contains("answer")) {
            parsingResult = responseMessage.substring(responseMessage.lastIndexOf("answer") + 8);
        }
        parsingResult = parsingResult.replace(String.valueOf("\n"), "");
        // parsingResult = parsingResult.replaceAll("[^\\n+]", "");
        return parsingResult;
    }



    public String splitMessage(String message){
        String[] tempArr = message.split("[.]");
        String[] resultArr = new String[tempArr.length];
        for(int i=0; i< 2; i++){
            resultArr[i] = tempArr[i];
        }
        String res = String.join(".", resultArr);
        String splitResult = res.replace(String.valueOf("null"), "");
        String realSplitResult = splitResult.substring(0, splitResult.length());
        return realSplitResult;
    }
}

