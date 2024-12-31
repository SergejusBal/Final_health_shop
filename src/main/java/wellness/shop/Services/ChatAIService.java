package wellness.shop.Services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wellness.shop.Integration.ChatGPD.ChatGPD;
import wellness.shop.Integration.ChatGPD.ChatMessage;
import wellness.shop.Integration.ChatGPD.Models.Message;
import wellness.shop.Models.FoodItem;

@Service
public class ChatAIService {
    @Value("${ai.key}")
    private String AI_KEY;

    private ChatGPD chatGPD;

    @PostConstruct
    private void init(){
        chatGPD = new ChatGPD(AI_KEY);
    }

    public FoodItem getFoodItemFromAI(String foodName){

        String unfilteredAIResponse = aIResponse(foodName);

        if ("-1".equals(unfilteredAIResponse)) return null;


        String[] responseArray = unfilteredAIResponse.split(",");
        if (responseArray.length != 5) return null;

        FoodItem foodItem = new FoodItem();
        foodItem.setFood(foodName);

        try{
            foodItem.setCalories(Double.parseDouble(responseArray[0]));
            foodItem.setProteins(Double.parseDouble(responseArray[1]));
            foodItem.setFats(Double.parseDouble(responseArray[2]));
            foodItem.setCarbohydrates(Double.parseDouble(responseArray[3]));
            foodItem.setFibers(Double.parseDouble(responseArray[4]));

        }catch (NumberFormatException e){
            return null;
        }

        return foodItem;


    }


    private String aIResponse(String foodName){
        Message systemPrompt = new Message("system",    "Calories in kcal, proteins, fats, carbohydrates, fibers in g. \n" +
                                                                    "Your response: {calories},{proteins},{fats},{carbohydrates},{fibers}.\n" +
                                                                    "Account for preparation and food description.\n" +
                                                                    "Calculate for 100g.\n For unclear food items reply -1.\n" +
                                                                    "Use numeric values rounded to one decimal place.");


        Message userPrompt = new Message("user", foodName);
        ChatMessage chatMessage = new ChatMessage("gpt-4o",0.25);
        chatMessage.addMessage(systemPrompt);
        chatMessage.addMessage(userPrompt);

        return chatGPD.sendChatRequest(chatMessage);
    }



}
