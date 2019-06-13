package com.alkemic.howplace.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Item.TheaterItem;
import com.alkemic.howplace.Node.ArrivalNode;
import com.alkemic.howplace.R;
import com.alkemic.howplace.Singletone.InfoFinder;
import com.alkemic.howplace.Singletone.InfoFinderEventListener;
import com.alkemic.howplace.Singletone.ItemManager;

import java.util.List;

public class SubwayActivity extends AppCompatActivity {

    LayoutInflater inflater;
    SubwayItem item;

    TextView nameText;
    TextView typeText;
    TextView isOpenText;
    TextView addressText;
    ImageView thumbImageView;

    LinearLayout subwayRect;

    ProgressBar bar;

    InfoFinderEventListener infoFinderEventListener = new InfoFinderEventListener() {
        @Override
        public void OnImageUpdateStart(Item item) {

        }

        @Override
        public void OnSubwayUpdateStart(SubwayItem item) {
            SubwayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bar != null) {
                        bar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public void OnShopUpdateStart(ShopItem item) {

        }

        @Override
        public void OnBlogUpdateStart(ShopItem item) {

        }

        @Override
        public void OnTheaterUpdateStart(TheaterItem item) {

        }

        @Override
        public void OnImageUpdateEnd(Item newItem) {
            if(newItem == item)
            {
                SubwayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ChangeImage(item);
                    }
                });
            }
        }

        @Override
        public void OnSubwayUpdateEnd(SubwayItem newItem) {
            if(newItem == item)
            {
                SubwayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ChangeArrival(item);
                    }
                });
            }
        }

        @Override
        public void OnShopUpdateEnd(ShopItem item) {
        }

        @Override
        public void OnBlogUpdateEnd(ShopItem item) {
        }

        @Override
        public void OnTheaterUpdateEnd(TheaterItem item) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_subway);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //레이아웃
        subwayRect = (LinearLayout) findViewById(R.id.layout_detailSubway_subwayRect);

        //텍스트뷰
        nameText = (TextView) findViewById(R.id.textView_detail_placeName);
        typeText = (TextView) findViewById(R.id.textView_detail_placeType);
        isOpenText = (TextView) findViewById(R.id.textView_detail_isOpen);
        addressText = (TextView) findViewById(R.id.textView_detail_address);

        thumbImageView = (ImageView) findViewById(R.id.imageView_detail_thumb);

        bar = (ProgressBar) findViewById(R.id.progressBar_detail);

        //버튼
        Button mapButton = (Button) findViewById(R.id.button_detail_map);
        if(mapButton != null)
        {
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoveMap(item);
                }
            });
        }
        Intent intent =  getIntent();
        int itemID = intent.getIntExtra("itemID",-1);
        if(itemID > -1)
        {
            item = (SubwayItem) ItemManager.getInstance().items.get(itemID);
        }
        if(item != null)
        {
            nameText.setText(item.getName());
            typeText.setText(Define.PlaceTypeToString(item.getType()));
            addressText.setText(item.getAddress());
            if(item.isOpen())
            {
                isOpenText.setText(R.string.text_isOpen);
            }
            else
            {
                isOpenText.setText("");
            }
            ChangeImage(item);
            if(item.getArrivalNodes().size() > 0)
            {
                ChangeArrival(item);
            }
        }
        InfoFinder.getInstance().AddListener(infoFinderEventListener);
    }

    private void ChangeImage(Item item)
    {
        if(item.getThumbnail() != null)
        {
            thumbImageView.setImageBitmap(item.getThumbnail());
        }
    }

    private void ChangeArrival(SubwayItem item) {
        if(bar != null) {
            bar.setVisibility(View.GONE);
        }
        List<ArrivalNode> arrivals =  item.getArrivalNodes();
        if(arrivals != null && arrivals.size() > 0)
        {
            subwayRect.removeAllViews();
            for(ArrivalNode arrival : arrivals)
            {
                View nodeView = (View)inflater.inflate(R.layout.arrival_node,subwayRect,false);
                TextView lineNameText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_lineName);
                lineNameText.setText(arrival.getLineName());
                TextView arrivalLeftText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_arrivalLeft);
                arrivalLeftText.setText(arrival.getArrivalLeft());
                TextView upDownText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_upDown);
                upDownText.setText(arrival.getUpDown());
                TextView curStationText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_curStation);
                curStationText.setText(arrival.getCurStation());
                subwayRect.addView(nodeView);
            }
        }
    }

    private void MoveMap(Item item)
    {
        Intent intent = null;
        intent = new Intent(this, MapActivity.class);
        intent.putExtra("itemID", item.getId());
        if(intent != null)
        {
            startActivity(intent);
        }
    }
}
