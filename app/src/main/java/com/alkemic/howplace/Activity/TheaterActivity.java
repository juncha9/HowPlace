package com.alkemic.howplace.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.RestaurantItem;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Item.TheaterItem;
import com.alkemic.howplace.Node.MovieNode;
import com.alkemic.howplace.Node.NaverBlogNode;
import com.alkemic.howplace.Node.TimeNode;
import com.alkemic.howplace.R;
import com.alkemic.howplace.Singletone.InfoFinder;
import com.alkemic.howplace.Singletone.InfoFinderEventListener;
import com.alkemic.howplace.Singletone.ItemManager;

import java.util.List;

public class TheaterActivity extends AppCompatActivity {

    LayoutInflater inflater;
    TheaterItem item;

    TextView nameText;
    TextView typeText;
    TextView isOpenText;
    TextView addressText;
    TextView uriText;
    TextView telNumText;

    ImageView thumbImageView;

    LinearLayout blogRect;
    LinearLayout telNumLayout;
    LinearLayout uriLayout;
    LinearLayout movieRect;

    InfoFinderEventListener infoFinderEventListener = new InfoFinderEventListener() {
        @Override
        public void OnImageUpdateStart(Item item) {

        }

        @Override
        public void OnSubwayUpdateStart(SubwayItem item) {

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
                TheaterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ChangeImage(item);
                    }
                });
            }
        }

        @Override
        public void OnSubwayUpdateEnd(SubwayItem item) {

        }

        @Override
        public void OnShopUpdateEnd(ShopItem newItem) {
            if(newItem == item)
            {
                TheaterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ChangeShopInfo(item);
                    }
                });
            }
        }

        @Override
        public void OnBlogUpdateEnd(ShopItem newItem) {
            if(newItem == item)
            {
                TheaterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CreateNaverBlogs(item);
                    }
                });
            }
        }

        @Override
        public void OnTheaterUpdateEnd(TheaterItem newItem) {
            if(newItem == item)
            {
                TheaterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CreateMovieNode(item);
                    }
                });
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_theater);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //레이아웃
        blogRect = (LinearLayout) findViewById(R.id.layout_detailShop_blogRect);
        uriLayout = (LinearLayout) findViewById(R.id.layout_detailShop_uri);
        telNumLayout = (LinearLayout) findViewById(R.id.layout_detailShop_telNum);
        movieRect = (LinearLayout) findViewById(R.id.layout_detailTheater_movieRect);

        //텍스트뷰
        nameText = (TextView) findViewById(R.id.textView_detail_placeName);
        typeText = (TextView) findViewById(R.id.textView_detail_placeType);
        isOpenText = (TextView) findViewById(R.id.textView_detail_isOpen);
        addressText = (TextView) findViewById(R.id.textView_detail_address);
        uriText = (TextView) findViewById(R.id.textView_detailShop_url);
        telNumText = (TextView) findViewById(R.id.textView_detailShop_telNum);

        thumbImageView = (ImageView) findViewById(R.id.imageView_detail_thumb);

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
            item = (TheaterItem) ItemManager.getInstance().items.get(itemID);
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
            ChangeShopInfo(item);
            if(item.getMovieNodes().size() > 0)
            {
                CreateMovieNode(item);
            }
            if(item.getNaverBlogs().size() > 0)
            {
                CreateNaverBlogs(item);
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


    private void ChangeShopInfo(ShopItem item)
    {

        if(item.getUri() != null)
        {
            uriLayout.setVisibility(View.VISIBLE);
            uriText.setText(item.getUri().toString());
            uriText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, item.getUri());
                    startActivity(intent);
                }
            });

        }
        else
        {
            uriLayout.setVisibility(View.GONE);
        }
        if(item.getTelNumber() != "")
        {
            telNumLayout.setVisibility(View.VISIBLE);
            telNumText.setText(item.getTelNumber());
            telNumText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+item.getTelNumber()));
                    startActivity(intent);
                }
            });

        }
        else
        {
            telNumLayout.setVisibility(View.GONE);
        }
    }

    private void CreateMovieNode(TheaterItem item)
    {
        List<MovieNode> movies = item.getMovieNodes();
        if(movies != null && movies.size() > 0)
        {
            for(MovieNode movie : movies)
            {
                View movieView = (View)inflater.inflate(R.layout.movie_node,movieRect,false);
                TextView titleText = (TextView) movieView.findViewById(R.id.textView_movieNode_title);
                titleText.setText(movie.getTitle());
                if(movie.getUri() != null)
                {
                    titleText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, movie.getUri());
                            startActivity(intent);
                        }
                    });
                }
                GridLayout timeRect = (GridLayout) movieView.findViewById(R.id.layout_movieNode_rect);
                List<TimeNode> times = movie.getTimes();
                if(times != null && times.size() > 0)
                {
                    for(TimeNode time : times)
                    {
                        View timeView = (View)inflater.inflate(R.layout.time_node,timeRect,false);
                        TextView timeText = timeView.findViewById(R.id.textView_timeNode_time);
                        timeText.setText(time.getTimeStr());
                        if(time.getUri() != null)
                        {
                            timeView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, time.getUri());
                                    startActivity(intent);
                                }
                            });
                        }
                        timeRect.addView(timeView);
                    }
                }
                movieRect.addView(movieView);
            }
        }
    }
    private void CreateNaverBlogs(ShopItem item)
    {
        List<NaverBlogNode> nodes = item.getNaverBlogs();
        for(NaverBlogNode node : nodes)
        {
            View view =  inflater.inflate(R.layout.blog_node, blogRect,false);
            TextView titleText = (TextView) view.findViewById(R.id.textView_blogNode_title);
            titleText.setText(Html.fromHtml(node.getTitle()));
            TextView descriptionText = (TextView) view.findViewById(R.id.textView_blogNode_description);
            descriptionText.setText(Html.fromHtml(node.getDescription()));
            TextView bloggerText = (TextView) view.findViewById(R.id.textView_blogNode_bloggerName);
            bloggerText.setText(node.getBloggerName());
            TextView postDateText = (TextView) view.findViewById(R.id.textView_blogNode_postDate);
            postDateText.setText(node.getPostDate().toString());
            if(node.getUri() != null)
            {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, node.getUri());
                        startActivity(intent);
                    }
                });
            }
            blogRect.addView(view);
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
