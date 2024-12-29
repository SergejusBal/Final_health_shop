package wellness.shop.Models.Diet;

import java.util.List;

public class Diet {

    // JSON example bellow

    private int id;
    private String description;
    private String userUUID;
    private String employeeUUID;
    private int age;
    private double height;
    private double weight;
    private List<MealPlan> mealPlans;

    public Diet() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public String getEmployeeUUID() {
        return employeeUUID;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public List<MealPlan> getMealPlans() {
        return mealPlans;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public void setEmployeeUUID(String employeeUUID) {
        this.employeeUUID = employeeUUID;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setMealPlans(List<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
    }

//    {
//        "id": 1,
//            "description": "Balanced diet plan for weight loss",
//            "userUUID": "John Doe",
//            "age": 30.0,
//            "height": 175.5,
//            "weight": 70.0,
//            "employeeUUID": "Nutritionist Jane Doe",
//            "mealPlans": [
//        {
//            "day": 1,
//                "meals": [
//            {
//                "time": "2024-12-01T07:30:00",
//                    "type": "Pusryčiai",
//                    "dishName": "Avižinių dribsnių košė su bananu",
//                    "ingredients": [
//                {
//                    "name": "Avižiniai dribsniai",
//                        "quantity": 70.0
//                },
//                {
//                    "name": "Bananas",
//                        "quantity": 60.0
//                },
//                {
//                    "name": "Chia sėklos",
//                        "quantity": 20.0
//                }
//          ],
//                "nutritionDetails": {
//                "calories": 563.0,
//                        "proteins": 15.7,
//                        "fats": 18.9,
//                        "carbohydrates": 68.7,
//                        "fiber": 17.2
//            },
//                "preparationSteps": "Sumaišykite avižas su bananais, chia sėklomis ir augaliniu pienu."
//            },
//            {
//                "time": "2024-12-01T12:30:00",
//                    "type": "Pietūs",
//                    "dishName": "Daržovių sriuba ir sumuštiniai su vištiena",
//                    "ingredients": [
//                {
//                    "name": "Ekologiška daržovių sriuba",
//                        "quantity": 400.0
//                },
//                {
//                    "name": "Vištos krūtinėlė",
//                        "quantity": 80.0
//                },
//                {
//                    "name": "Duona",
//                        "quantity": 4.0
//                }
//          ],
//                "nutritionDetails": {
//                "calories": 635.0,
//                        "proteins": 42.9,
//                        "fats": 21.4,
//                        "carbohydrates": 39.8,
//                        "fiber": 23.0
//            },
//                "preparationSteps": "Uždėkite sriubą į dubenį ir paruoškite sumuštinius su vištiena ir daržovėmis."
//            }
//      ],
//            "nutritionSummary": {
//            "calories": 1198.0,
//                    "proteins": 58.6,
//                    "fats": 40.3,
//                    "carbohydrates": 108.5,
//                    "fiber": 40.2
//        }
//        },
//        {
//            "day": 2,
//                "meals": [
//            {
//                "time": "2024-12-02T17:30:00",
//                    "type": "Vakarienė",
//                    "dishName": "Grikiai su pievagrybiais",
//                    "ingredients": [
//                {
//                    "name": "Grikiai",
//                        "quantity": 80.0
//                },
//                {
//                    "name": "Pievagrybiai",
//                        "quantity": 40.0
//                },
//                {
//                    "name": "Špinatai",
//                        "quantity": 30.0
//                }
//          ],
//                "nutritionDetails": {
//                "calories": 490.0,
//                        "proteins": 17.7,
//                        "fats": 12.3,
//                        "carbohydrates": 71.7,
//                        "fiber": 15.5
//            },
//                "preparationSteps": "Išvirkite grikius ir sumaišykite su troškintais pievagrybiais ir špinatais."
//            }
//      ],
//            "nutritionSummary": {
//            "calories": 490.0,
//                    "proteins": 17.7,
//                    "fats": 12.3,
//                    "carbohydrates": 71.7,
//                    "fiber": 15.5
//        }
//        }
//  ]
//    }
}
