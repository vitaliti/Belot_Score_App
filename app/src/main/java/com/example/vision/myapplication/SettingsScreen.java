package com.example.vision.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

/**
 * Created by Vision on 10.9.2016 г..
 */
public class SettingsScreen extends Activity {
    CheckBox first;
    CheckBox second;
    EditText firstTeam;
    EditText secondTeam;
    Intent data = new Intent();
    int ColorN,ColorV = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        Intent mainActivity = getIntent();

        first = (CheckBox) findViewById(R.id.checkbox_first);
        second =(CheckBox) findViewById(R.id.checkbox_second);

        firstTeam  = (EditText) findViewById(R.id.rename_N);
        secondTeam = (EditText) findViewById(R.id.rename_V);

        //set backgrounds and set names
        String firstName = mainActivity.getExtras().getString("firstTeamName");
        String secondName = mainActivity.getExtras().getString("secondTeamName");

        firstTeam.setText(firstName);
        secondTeam.setText(secondName);

        ColorN = mainActivity.getIntExtra("firstTeamColor",-1);
        ColorV = mainActivity.getIntExtra("secondTeamColor",-1);

        firstTeam.setBackgroundColor(ColorN);
        secondTeam.setBackgroundColor(ColorV);

    }

    public void CreateColorPicker(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(-1)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(getApplicationContext(),selectedColor +  " ",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void setColor(int color){
        if (first.isChecked()){
            firstTeam.setBackgroundColor(color);
            ColorN = color;
        }

        if (second.isChecked()){
            secondTeam.setBackgroundColor(color);
            ColorV = color;
        }
    }

    public void ChangeColor(View v) {
        CreateColorPicker();
    }

    public void FinishActivity(View view) {
        setResult(RESULT_CANCELED,data);
        finish();
    }

    public void SaveChanges(View view) {
        data.putExtra("firstTeamName",firstTeam.getText().toString());
        data.putExtra("secondTeamName",secondTeam.getText().toString());
        data.putExtra("firstTeamColor",ColorN);
        data.putExtra("secondTeamColor",ColorV);
        setResult(RESULT_OK,data);
        finish();
    }
}
