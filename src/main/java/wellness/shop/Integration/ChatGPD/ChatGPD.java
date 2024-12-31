package wellness.shop.Integration.ChatGPD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class ChatGPD {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private String API_KEY;

    ObjectMapper objectMapper;

    public ChatGPD(String API_KEY){
        this.API_KEY = API_KEY;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String sendChatRequest(ChatMessage chatMessage)  {
        OkHttpClient client = new OkHttpClient();

        String requestBodyJson = generateJson(chatMessage);
        //System.out.println(requestBodyJson);
        RequestBody body = RequestBody.create(
                requestBodyJson, MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {

                String responseJson = response.body().string();
               // System.out.println(responseJson);

                ChatResponse chatResponse = (ChatResponse) generateObjectFromJSon(responseJson,ChatResponse.class);

                return chatResponse.getChoices().getFirst().getMessage().getContent();
            } else {
                return "Response failed!\n" + response;
            }
        }catch (IOException e){
            return "Unexpected Error!\n" +e.getMessage();
        }
    }


    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    private  <T> T generateObjectFromJSon(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
