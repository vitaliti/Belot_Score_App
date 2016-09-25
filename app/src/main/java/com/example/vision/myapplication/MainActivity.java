package com.example.vision.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Method;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    int counter = 0;
    int NGames = 0;
    int VGames = 0;
    int NResult = 0;
    int VResult = 0;
    LinearLayout vertLayout;
    LayoutInflater inflator;
    Stack<Integer> endGameRows = new Stack<Integer>();
    Stack<Integer> endGameResult = new Stack<Integer>();
    static final int PICK_CONTACT_REQUEST = 1;
    TextView N,V;
    int colorN = -1;
    int colorV = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflator = LayoutInflater.from(getApplicationContext());
         vertLayout = (LinearLayout) findViewById(R.id.verticalLayout);

        inflator.inflate( R.layout.head_teams_layout, vertLayout, true);
        N = (TextView) findViewById(R.id.headN);
        V = (TextView) findViewById(R.id.headV);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){

                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    public void AttachListenerToEditTexts(View v){
        attachListener((EditText)v.findViewById(R.id.N));
        attachListener((EditText)v.findViewById(R.id.V));
    }

    private int calculateSum(String numbers){
        String[] parts = numbers.split("[- ]");
        if (parts.length != 2){
            return -1;
        }
        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[1]);
        return a + b;
    }

    private boolean checkPreviousRowScore(){
        //Gets previous Row and it's EditText's
        View row =  vertLayout.getChildAt(counter - 1);

        EditText scoreN = (EditText)row.findViewById(R.id.N);
        EditText scoreV = (EditText)row.findViewById(R.id.V);

        //If previus Row was header it inflates New row
        if (scoreN == null){
            inflator.inflate(R.layout.row_layout, vertLayout, true);
            LinearLayout currentRow = (LinearLayout) vertLayout.getChildAt(counter);
            AttachListenerToEditTexts(currentRow);
            return false;
        }

        //If previous Row was not header it get's it's values
        String N = scoreN.getText().toString();
        String V = scoreV.getText().toString();

        this.NResult = calculateSum(N);
        this.VResult = calculateSum(V);

        //If previous Row Values are not correct it returns -1
        if(NResult < 0 || VResult < 0){
            counter--; //Counter get's set coreclty!
            Toast.makeText(this, "Wrong input in score",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    public void AddNewLine(View view) {
        counter++;

        //checks if Previouws Score exists and inflates next row
        if (!checkPreviousRowScore()){
            return;
        }

        //Checks if last row wag Red Game Score row and just inflates Header Teams Row
        if (endGameRows.empty() == false && endGameRows.peek() == counter - 1){
            inflator.inflate(R.layout.head_teams_layout, vertLayout, true);
            return;
        }

        //Inflates Normal Row and it's score if it has score
        inflator.inflate(R.layout.row_layout, vertLayout, true);

        LinearLayout rowNext = (LinearLayout) vertLayout.getChildAt(counter);
        AttachListenerToEditTexts(rowNext);
        EditText scoreNextN = (EditText)rowNext.findViewById(R.id.N);
        EditText scoreNextV = (EditText)rowNext.findViewById(R.id.V);

        scoreNextN.setText(this.NResult + "-");
        scoreNextV.setText(this.VResult  + "-");


    }

    public void DeleteLastLine(View view){
        if (counter == 0){
            Toast.makeText(this, "No more rows to delete",
                    Toast.LENGTH_LONG).show();
            return;
        }

        vertLayout.removeViewAt(counter);

        //Checks if the removed row was Red Game Score Row and lowers the score of the game
        if (endGameRows.empty() == false && counter ==  endGameRows.peek()){
            endGameRows.pop();
            int res = endGameResult.pop();
            if(res == 0){
                NGames--;
            }else{
                VGames--;
            }
        }
        counter--;
    }

    public void attachListener(EditText v){
        v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    goToEndText((EditText) v);
                }else {
                   // Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToEndText(EditText view) {
        view.setSelection(view.getText().length());
    }

    public void NewGameLine(View view) {
        counter++;

        //checks if Previouws Score exists and inflates next row
        if(!checkPreviousRowScore()){
            return;
        }

        //checks if previus row was a End Game RED row
        if (endGameRows.empty() == false && endGameRows.peek() == counter - 1){
            return;
        }

        endGameRows.push(counter);

        //Checks previus Score and sets Game score
        if(NResult > VResult){
            endGameResult.push(0);
            NGames++;
        }else if(NResult == VResult) {
            counter--;
            endGameRows.pop();
            Toast.makeText(this, "The game is a Draw",
                    Toast.LENGTH_LONG).show();
            return;
        } else{
            endGameResult.push(1);
            VGames++;
        }

        //Inflates New RED End Game Row
        inflator.inflate(R.layout.row_layout, vertLayout, true);

        LinearLayout gameEndRow = (LinearLayout) vertLayout.getChildAt(counter);
        EditText gameScoreNextN = (EditText)gameEndRow.findViewById(R.id.N);
        EditText gameScoreNextV = (EditText)gameEndRow.findViewById(R.id.V);

        //Sets New RED End Game Row score and makes it red
        gameScoreNextN.setText(NGames + "");
        gameScoreNextN.setBackgroundColor(Color.parseColor("#ff0000"));
        gameScoreNextV.setText(VGames + "");
        gameScoreNextV.setBackgroundColor(Color.parseColor("#ff0000"));

        //Inflates New Game Header Row
        inflator.inflate( R.layout.head_teams_layout, vertLayout, true);
        counter++;
        ChangeAllHeadCells();
    }

    public void Exit(MenuItem item) {
        System.exit(1);
    }

    public void GoToSettingScreen(MenuItem item) {
        Intent settingsScreen = new Intent(this,SettingsScreen.class);

        settingsScreen.putExtra("firstTeamName",N.getText());
        settingsScreen.putExtra("secondTeamName",V.getText());
        settingsScreen.putExtra("firstTeamColor", colorN);
        settingsScreen.putExtra("secondTeamColor",colorV);

        startActivityForResult(settingsScreen,PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                N.setText(data.getStringExtra("firstTeamName"));
                V.setText(data.getStringExtra("secondTeamName"));
                this.colorN = data.getIntExtra("firstTeamColor",this.colorN);
                this.colorV = data.getIntExtra("secondTeamColor",this.colorV);

                //Make all views with this color
                N.setBackgroundColor(this.colorN);
                V.setBackgroundColor(this.colorV);

                ChangeAllHeadCells();
            }
        }
    }

    public void ChangeAllHeadCells(){
        int children = vertLayout.getChildCount();
        for (int i = 0; i < children; i++){
            try {
                LinearLayout headRow = (LinearLayout) vertLayout.getChildAt(i);
                LinearLayout NLayout = (LinearLayout) headRow.getChildAt(0);
                LinearLayout VLayout = (LinearLayout) headRow.getChildAt(1);

                TextView NText = (TextView) NLayout.findViewById(R.id.headN);
                TextView VText = (TextView) VLayout.findViewById(R.id.headV);

                NText.setText(N.getText());
                VText.setText(V.getText());

                NText.setBackgroundColor(colorN);
                VText.setBackgroundColor(colorV);

            }catch (Exception e){
                continue;
            }
        }
    }

    public void GotoInfoScreen(MenuItem item) {
        Intent infoScreen = new Intent(this,InfoScreen.class);
        startActivity(infoScreen);
    }
}
