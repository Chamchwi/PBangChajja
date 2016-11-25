package com.dsm.spiralmoon.pbangchajja.pccafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsm.spiralmoon.pbangchajja.ImageLoadThread;
import com.dsm.spiralmoon.pbangchajja.R;
import com.dsm.spiralmoon.pbangchajja.ReviewAdapter;
import com.dsm.spiralmoon.pbangchajja.ReviewThread;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class PlacesInfoActivity_pccafe extends AppCompatActivity {

    ImageView infoModify;
    ImageView placeImage;
    ImageView likeButton;
    ImageView hateButton;
    ListView reviewList = null;
    ReviewAdapter reviewAdpater = null;
    TextView likeCount;
    TextView hateCount;
    Button reviewButton;

    //image
    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int CROP_FROM_CAMERA = 2;
    private Uri imageCaptureUri;

    private SearchResult_pccafe searchResult = null;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_info_pccafe);

        searchResult = (SearchResult_pccafe) getIntent().getSerializableExtra("SearchResult_pccafe");

        //actionbar event
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //listener set
        infoModify = (ImageView) findViewById(R.id.pccafe_modify_icon);
        infoModify.setOnClickListener(infoSet);
        placeImage = (ImageView) findViewById(R.id.pccafe_place_image);
        placeImage.setOnClickListener(imageSet);
        likeButton = (ImageView) findViewById(R.id.pccafe_like);
        likeButton.setOnClickListener(like);
        hateButton = (ImageView) findViewById(R.id.pccafe_hate);
        hateButton.setOnClickListener(hate);
        likeCount = (TextView) findViewById(R.id.pccafe_like_count);
        hateCount = (TextView) findViewById(R.id.pccafe_hate_count);
        reviewButton = (Button) findViewById(R.id.pccafe_review_button);
        reviewButton.setOnClickListener(reviewListener);

//        reviewThread = new ReviewThread("" , reviewAdpater);


        //Information list
        ArrayList<TextView> shopInfo = new ArrayList<>();
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_name));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_text));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_cpu));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_ram));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_gcard));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_monitor));
        shopInfo.add((TextView) findViewById(R.id.pccafe_info_price));

        reviewAdpater = new ReviewAdapter();
        reviewList = (ListView) findViewById(R.id.pccafe_review_list);
        reviewList.setAdapter(reviewAdpater);

        //Place information Loading process
        String request = searchResult.request(); //파싱 전 데이터 저장
        searchResult.jsonParse(request, shopInfo, likeCount, hateCount); //데이터 파싱 및 정보 출력
        ImageLoadThread imageLoadThread = new ImageLoadThread("", placeImage); //사진을 쓰레드로 불러옴
//        imageLoadThread.run();
        ReviewThread reviewThread = new ReviewThread(searchResult.jsonReviewList, reviewAdpater); //리뷰를 쓰레드로 불러옴
        reviewThread.run();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM:
                imageCaptureUri = data.getData();
                Log.i("NR", imageCaptureUri.getPath().toString());
                //이후 처리는 카메라와 동일하여 break문 없이 진행
            case PICK_FROM_CAMERA:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageCaptureUri, "image/*");
                intent.putExtra("outputX", 640);
                intent.putExtra("outputY", 640);
                intent.putExtra("aspectX", 16);
                intent.putExtra("aspectY", 9);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            case CROP_FROM_CAMERA:
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    placeImage.setImageBitmap(photo);
                }

                searchResult.imageModify(placeImage);

                //임시파일삭제
                File f = new File(imageCaptureUri.getPath());
                if(f.exists()) {
                    f.delete();
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return false;
    }

    public View.OnClickListener infoSet = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //dialog view
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View sendInfo = inflater.inflate(R.layout.dialog_info_pccafe, (ViewGroup) findViewById(R.id.dialog_info_pccafe));

            //input fields
            final ArrayList<EditText> editText = new ArrayList<EditText>();
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_text));
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_cpu));
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_ram));
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_graphic_card));
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_monitor_size));
            editText.add((EditText) sendInfo.findViewById(R.id.pccafe_dialog_cost));

            //dialog view create
            AlertDialog.Builder sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_pccafe.this);
            sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_pccafe.this);
            sendInfoDialog.setTitle("정보 업데이트");
            sendInfoDialog.setView(sendInfo);
            sendInfoDialog.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (searchResult.typeCheck("monitorSize", editText.get(4).getText().toString())
                            && searchResult.typeCheck("price", editText.get(5).getText().toString())
                            && searchResult.dataCheck(editText)) {

                        searchResult.modify(editText);
                        Toast.makeText(PlacesInfoActivity_pccafe.this, "정보 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(PlacesInfoActivity_pccafe.this, "모니터의 사이즈와 요금은\n" +
                                "숫자로만 입력해야 합니다.", Toast.LENGTH_SHORT).show();
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
    public Button.OnClickListener reviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Toast.makeText(PlacesInfoActivity_pccafe.this, "리뷰는 1번만 작성할 수 있으며\n" +
                    "삭제하거나 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
            //dialog view
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View reviewInfo = inflater.inflate(R.layout.dialog_review, (ViewGroup) findViewById(R.id.dialog_review));

            final ArrayList<EditText> editText = new ArrayList<EditText>();
            editText.add((EditText) reviewInfo.findViewById(R.id.dialog_review_title));
            editText.add((EditText) reviewInfo.findViewById(R.id.dialog_review_content));

            AlertDialog.Builder sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_pccafe.this);
            sendInfoDialog = new AlertDialog.Builder(PlacesInfoActivity_pccafe.this);
            sendInfoDialog.setTitle("리뷰쓰기");
            sendInfoDialog.setView(reviewInfo);
            sendInfoDialog.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    searchResult.review(editText.get(0).getText().toString(),
                            editText.get(1).getText().toString());
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

            //현재위치 : 사진첩                 취소 카메라

            AlertDialog.Builder dialog = new AlertDialog.Builder(PlacesInfoActivity_pccafe.this);
            dialog.setTitle("사진 업데이트");
            dialog.setMessage("이 장소의 사진을 업데이트합니다.");
            dialog.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    takePhoto();
//                    searchResult.imageModify(placeImage);
                }
            });
            dialog.setNeutralButton("사진첩", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    takeAlbum();
//                    searchResult.imageModify(placeImage);
                }
            });
            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.create();
            dialog.show();
        }
    };
    public View.OnClickListener like = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Toast.makeText(PlacesInfoActivity_pccafe.this, "의견 감사합니다. 한 사용자당 추천/비추천을\n" +
                    "한번만 반영할 수 있습니다.", Toast.LENGTH_SHORT).show();

            searchResult.isLike(true);
        }
    };
    public View.OnClickListener hate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(PlacesInfoActivity_pccafe.this, "의견 감사합니다. 한 사용자당 추천/비추천을\n" +
                    "한번만 반영할 수 있습니다.", Toast.LENGTH_SHORT).show();

            searchResult.isLike(false);
        }
    };
    //dialog button event
    public void takePhoto() { //사진 촬영
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "temp_" + String.valueOf(System.currentTimeMillis()) + ".png";
        imageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    public void takeAlbum() { //갤러리에서 불러옴
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
}
