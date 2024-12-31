package wellness.shop.Integration.ChatGPD;


import wellness.shop.Integration.ChatGPD.Models.Choice;
import wellness.shop.Integration.ChatGPD.Models.Usage;

import java.util.List;

public class ChatResponse {

// Here is example how response should looks like (note: json is more complicated)
//    {
//            "id": "chatcmpl-7XXtD2T1FXkP9vld5FY8ZVW9ElEya",
//            "object": "chat.completion",
//            "created": 1677652287,
//            "model": "gpt-4",
//            "choices": [{
//            "index": 0,
//                "message": {
//                     "role": "assistant",
//                     "content": "Hello! How can I assist you today?"
//                     },
//                "finish_reason": "stop"
//            }],
//        "usage": {
//                "prompt_tokens": 12,
//                "completion_tokens": 9,
//                "total_tokens": 21
//                 }
//    }

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    public ChatResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }


}