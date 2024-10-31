package couriertest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CourierData {
    public String login;
    public String password;
    public String firstName;

    public CourierData(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create(); // Убрала prettyPrinting для более компактного JSON
        return gson.toJson(this);
    }
}

