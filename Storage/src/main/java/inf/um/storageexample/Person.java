package inf.um.storageexample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Clase generada de forma automática usando http://www.jsonschema2pojo.org/
 */
public class Person {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("age")
    @Expose
    private int age;

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    /**
     * No args constructor for use in serialization
     *
     */
    public Person() {
    }

    /**
     *
     * @param name
     * @param age
     */
    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public JsonElement toJson(){
        return gson.toJsonTree(this);
    }

}
