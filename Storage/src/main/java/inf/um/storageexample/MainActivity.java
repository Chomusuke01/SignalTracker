package inf.um.storageexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Person> people;
    private static final String FILENAME="people.json";
    private EditText nameField;
    private EditText ageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        people=new LinkedList<>();
        nameField=findViewById(R.id.editTextTextPersonName);
        ageField=findViewById(R.id.editTextAge);
    }

    public void onClickAdd(View v){
        // En una app debería hacerse comprobaciones sobre el contenido de los campos (incluso evitar pulsado de botón si no se cumplen los requisitos)
        String name=nameField.getText().toString();
        int age=0;
        try{
            age=Integer.parseInt(ageField.getText().toString());
        }catch (NumberFormatException e){
            Log.e("MainActivity","Error age format: ",e);
        }
        people.add(new Person(name,age));
        nameField.getText().clear();
        ageField.getText().clear();
    }

    public void onClickStore(View v){
        JsonObject res=new JsonObject();
        JsonArray peopleArray=new JsonArray();
        for(Person p:people){
            peopleArray.add(p.toJson());
        }
        res.add("people",peopleArray);
        try {
            StorageHelper.saveStringToFile(FILENAME,res.toString(),this);
        } catch (IOException e) {
            Log.e("MainActivity","Error saving file: ",e);
        }
    }


}