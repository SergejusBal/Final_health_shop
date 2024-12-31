package wellness.shop.Integration.ChatGPD;



import wellness.shop.Integration.ChatGPD.Models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {

//  Example of message gson
//    {
//        "model": "gpt-4",
//            "messages": [
//        { "role": "system", "content": "You are a helpful assistant." },
//        { "sadfasasf" "role": "system", "content": "What is the capital of France?" },
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//        { "role": "user", "content": "And what is its population?" }
//  ]
//    }

    private String model;
    private List<Message> messages;
    private double temperature;

    public ChatMessage() {
    }

    public ChatMessage(String model,double temperature) {
        this.model = model;
        messages = new ArrayList<>();
        this.temperature = temperature;
    }

    public ChatMessage(String model, List<Message> messages, double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message){
        this.messages.add(message);
    }
    public void clearAllMessages(){
        this.messages = new ArrayList<>();
    }

}
