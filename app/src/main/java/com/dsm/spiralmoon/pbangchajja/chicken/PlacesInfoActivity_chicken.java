package com.dsm.spiralmoon.pbangchajja.chicken;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsm.spiralmoon.pbangchajja.R;

import java.util.ArrayList;

public class PlacesInfoActivity_chicken extends AppCompatActivity {

    ImageView infoModify;
    ImageView placeImage;
    Button yesButton;
    Button noButton;
    CheckBox reviewOn;

    private SearchResult_chicken searchResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_info_chicken);

        searchResult = (SearchResult_chicken) getIntent().getSerializableExtra("SearchResult");

        //actionbar event
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //ImageView set
        infoModify = (ImageView) findViewById(R.id.chicken_modify_icon);
        infoModify.setOnClickListener(infoSet);
        placeImage = (ImageView) findViewById(R.id.chicken_place_image);
        placeImage.setOnClickListener(imageSet);

        ArrayList<TextView> shopInfo = new ArrayList<>();
        shopInfo.add((TextView) findViewById(R.id.chicken_info_name));
        shopInfo.add((TextView) findViewById(R.id.chicken_info_text));
        shopInfo.add((TextView) findViewById(R.id.chicken_info_fried));
        shopInfo.add((TextView) findViewById(R.id.chicken_info_spicy));
        shopInfo.add((TextView) findViewById(R.id.chicken_info_soya));
        shopInfo.add((TextView) findViewById(R.id.chicken_info_cola));

        //searchResult.jsonParse(searchResult.request(), shopInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            this.finish();
            return true;
        }

        return false;
    }
    public View.OnClickListener infoSet = new View.OnClickListener() {
        @Override
        public void onClick (View view) {

            //dialog view
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View sendInfo = inflater.inflate(R.layout.dialog_info_chicken, (ViewGroup) findViewById(R.id.dialog_info_chicken));

            //input fields
            final ArrayList<EditText> editText = new ArrayList<EditText>();
            editText.add((EditText) sendInfo.findViewById(R.id.chicken_dialog_text));
            editText.add((EditText) sendInfo.findViewById(R.id.chicken_dialog_fried));
            editText.add((EditText) sendInfo.findViewById(R.id.chicken_dialog_spicy));
            editText.add((EditText) sendInfo.findViewById(R.id.chicken_dialog_soya));
            editText.add((EditText) sendInfo.findViewById(R.id.chicken_dialog_cola));

            //dialog view create
            AlertDialog.Builder sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_chicken.this);
            sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_chicken.this);
            sendInfoDialog.setTitle("정보 업데이트");
            sendInfoDialog.setView(sendInfo);
            sendInfoDialog.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (searchResult.dataCheck(editText)
                            && searchResult.typeCheck("fried", editText.get(1).toString())
                            && searchResult.typeCheck("spicy", editText.get(2).toString())
                            && searchResult.typeCheck("soya", editText.get(3).toString())) {

                        searchResult.modify(editText);
                        Toast.makeText(PlacesInfoActivity_chicken.this, "정보 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(PlacesInfoActivity_chicken.this, "가격은 숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
            sendInfoDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            sendInfoDialog.show();
        }
    };
    public View.OnClickListener imageSet = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    public View.OnClickListener like = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    //dialog button event
}
