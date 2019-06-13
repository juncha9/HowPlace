package com.alkemic.howplace.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.CafeItem;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.RestaurantItem;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Item.TheaterItem;
import com.alkemic.howplace.Node.ArrivalNode;
import com.alkemic.howplace.Node.MovieNode;
import com.alkemic.howplace.Node.TimeNode;
import com.alkemic.howplace.Singletone.InfoFinder;
import com.alkemic.howplace.Singletone.InfoFinderEventListener;
import com.alkemic.howplace.Singletone.ItemManager;
import com.alkemic.howplace.Singletone.ItemManagerEventListener;
import com.alkemic.howplace.Singletone.LocationChecker;
import com.alkemic.howplace.Singletone.PlaceFinder;
import com.alkemic.howplace.Singletone.PlaceFinderEventListener;
import com.alkemic.howplace.R;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView notifyTextView;
    LinearLayout container;
    Button updateButton;
    ProgressBar updatePrograssBar;
    AppCompatActivity thisActivity;
    HashMap <Item, View> itemViewMap = new HashMap<Item,View>();
    LayoutInflater inflater;
    ItemManagerEventListener itemManagerEventListener = new ItemManagerEventListener() {
        @Override
        public void OnItemCreate() {
            List<Item> items = ItemManager.getInstance().items;
            if(items != null && items.size() > 0)
            {
                CreateItemLayouts(items);
            }
        }

        @Override
        public void OnItemEmpty() {
            notifyTextView.setVisibility(View.VISIBLE);
            notifyTextView.setText(R.string.notify_items_empty);
        }
    };

    InfoFinderEventListener infoFinderEventListener = new InfoFinderEventListener() {
        @Override
        public void OnImageUpdateStart(Item item) {
        }

        @Override
        public void OnSubwayUpdateStart(SubwayItem item) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view = itemViewMap.get(item);
                    if(view != null)
                    {
                        ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar_container);
                        if(bar != null)
                        {
                            bar.setVisibility(View.VISIBLE);
                        }
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
        public void OnImageUpdateEnd(Item item) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (item instanceof ShopItem) {
                        ChangeLayoutImage(item);
                    }
                }
            });
        }

        @Override
        public void OnSubwayUpdateEnd(SubwayItem item) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChangeLayoutArrival(item);
                }
            });
        }

        @Override
        public void OnShopUpdateEnd(ShopItem item) {

        }

        @Override
        public void OnBlogUpdateEnd(ShopItem item) {

        }

        @Override
        public void OnTheaterUpdateEnd(TheaterItem item) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChangeLayoutMovie(item);
                }
            });
        }

    };

    PlaceFinderEventListener placeFinderEventListener = new PlaceFinderEventListener() {
        @Override
        public void OnFindStart() {
            updatePrograssBar.setVisibility(View.VISIBLE);
            notifyTextView.setVisibility(View.GONE);
        }

        @Override
        public void OnFindComplete(List<Item> newItems) {
            updatePrograssBar.setVisibility(View.GONE);
            notifyTextView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ItemManager.getInstance().AddListener(itemManagerEventListener);
        PlaceFinder.getInstance().AddListener(placeFinderEventListener);
        InfoFinder.getInstance().AddListener(infoFinderEventListener);
        Log.d("MainActivity", "OnCreate");
        container = (LinearLayout) findViewById(R.id.layout_main_scrollRect);
        updatePrograssBar = (ProgressBar) findViewById(R.id.prgressBar_main_update);
        updateButton = (Button) findViewById(R.id.button_update);
        notifyTextView = (TextView) findViewById(R.id.main_textView_notify);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InfoFinder.getInstance().isUpdating() || PlaceFinder.getInstance().isUpdating())
                {
                    Toast.makeText(thisActivity,"이미 업데이트 중 입니다.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    itemViewMap.clear();
                    container.removeAllViews();
                    PlaceFinder.getInstance().Update();
                    Toast.makeText(thisActivity,"업데이트를 시작합니다.",Toast.LENGTH_LONG).show();
                }

            }
        });
        Intent intent = getIntent();
        if(LocationChecker.getInstance().getPermission() <= LocationChecker.PERMISSION_BASE)
        {
            notifyTextView.setVisibility(View.VISIBLE);
            notifyTextView.setText(R.string.notify_has_no_permission);
        }
        boolean isFindStart = intent.getBooleanExtra("isFindStart",false);
        if(isFindStart)
        {
            updatePrograssBar.setVisibility(View.VISIBLE);
            notifyTextView.setVisibility(View.GONE);
        }
        else
        {
            updatePrograssBar.setVisibility(View.GONE);
        }

    }

    private void CreateItemLayouts(List<Item> items)
    {
        if(container == null) return;
        for (Item item : items)
        {
            View view = null;
            if(item instanceof SubwayItem)
            {
                view = (View) inflater.inflate(R.layout.subway_container, container, false);
            }
            if(item instanceof RestaurantItem)
            {
                view = (View) inflater.inflate(R.layout.restaurant_container, container, false);
            }
            if(item instanceof CafeItem)
            {
                view = (View) inflater.inflate(R.layout.cafe_container, container, false);
            }
            if(item instanceof TheaterItem)
            {
                view = (View) inflater.inflate(R.layout.theater_container, container, false);
            }
            if(view != null)
            {
                itemViewMap.put(item,view);
                TextView nameText = (TextView) view.findViewById(R.id.textView_container_placeName);
                nameText.setText(item.getName());
                TextView typeText = (TextView) view.findViewById(R.id.textView_container_placeTypes);
                typeText.setText(Define.PlaceTypeToString(item.getType()));
                TextView isOpenText =(TextView) view.findViewById(R.id.textView_container_isOpen);
                if(item.isOpen())
                {
                    isOpenText.setText("영업 중");
                }
                else
                {
                    isOpenText.setText("");
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MoveDetailActivity(item);
                    }
                });
            }
            container.addView(view, item.getId());
        }
    }
    private void MoveDetailActivity(Item item)
    {
        Intent intent = null;
        if(item instanceof RestaurantItem)
        {
            intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("itemID", item.getId());
        }
        if(item instanceof CafeItem)
        {
            intent = new Intent(this, CafeActivity.class);
            intent.putExtra("itemID", item.getId());
        }
        if(item instanceof SubwayItem)
        {
            intent = new Intent(this, SubwayActivity.class);
            intent.putExtra("itemID", item.getId());
        }
        if(item instanceof TheaterItem)
        {
            intent = new Intent(this, TheaterActivity.class);
            intent.putExtra("itemID", item.getId());
        }
        if(intent != null)
        {
            startActivity(intent);
        }

    }

    private void ChangeLayoutImage(Item item) {
        View view = itemViewMap.get(item);
        if(view != null)
        {
            ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar_container);
            if(bar != null)
            {
                bar.setVisibility(View.GONE);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView_shopContainer_thumb);
            Bitmap thumb = item.getThumbnail();
            if(imageView != null && thumb != null)
            {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(thumb);
            }
        }
    }
    private void ChangeLayoutArrival(SubwayItem item) {

        View view = itemViewMap.get(item);
        if(view != null)
        {
            ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar_container);
            if(bar != null) {
                bar.setVisibility(View.GONE);
            }
            LinearLayout rect = (LinearLayout) view.findViewById(R.id.layout_subwayContainer_rect);
            rect.removeAllViews();
            List<ArrivalNode> arrivals =  item.getArrivalNodes();
            if(arrivals != null && arrivals.size() > 0)
            {
                for(ArrivalNode arrival : arrivals)
                {
                    View nodeView = (View)inflater.inflate(R.layout.arrival_node,rect,false);
                    TextView lineNameText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_lineName);
                    lineNameText.setText(arrival.getLineName());
                    TextView arrivalLeftText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_arrivalLeft);
                    arrivalLeftText.setText(arrival.getArrivalLeft());
                    TextView upDownText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_upDown);
                    upDownText.setText(arrival.getUpDown());
                    TextView curStationText = (TextView) nodeView.findViewById(R.id.textView_arrivalNode_curStation);
                    curStationText.setText(arrival.getCurStation());
                    rect.addView(nodeView);
                }
            }
        }
    }
    private void ChangeLayoutMovie(TheaterItem item)
    {
        View view = itemViewMap.get(item);
        if(view != null)
        {
            ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar_container);
            if(bar != null) {
                bar.setVisibility(View.GONE);
            }
            LinearLayout rect = (LinearLayout) view.findViewById(R.id.layout_theaterContainer_rect);
            List<MovieNode> movies = item.getMovieNodes();
            if(movies != null && movies.size() > 0)
            {
                for(MovieNode movie : movies)
                {
                    View movieView = (View)inflater.inflate(R.layout.movie_node,rect,false);
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
                    rect.addView(movieView);
                }
            }
        }
    }

}
