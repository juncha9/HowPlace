package com.alkemic.howplace.Singletone;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.TheaterItem;
import com.alkemic.howplace.Node.ArrivalNode;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Node.MovieNode;
import com.alkemic.howplace.Node.NaverBlogNode;
import com.alkemic.howplace.Node.TimeNode;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoFinder {
    static {
        System.loadLibrary("keys");
    }

    private static InfoFinder instance = new InfoFinder();
    public boolean activated;
    Context context;
    PlacesClient placeClient;
    OkHttpClient httpClient;
    List<SubwayItem> subwayItems = new ArrayList<SubwayItem>();
    List<InfoFinderEventListener> listeners = new ArrayList<InfoFinderEventListener>();
    AsyncTask updateTask;
    List<AsyncTask> tasks = new ArrayList<AsyncTask>();
    List<AsyncTask> subwayTasks = new ArrayList<AsyncTask>();

    public void AddListener(InfoFinderEventListener listener)
    {
        listeners.add(listener);
    }

    private InfoFinder() {
        activated = false;
    }

    public boolean Initialize(Context context)
    {
        this.context = context;
        Places.initialize(context, Define.getWebApiKey());
        placeClient = Places.createClient(context);
        httpClient = new OkHttpClient();
        TimerTask timerTask = new TimerTask()
        {
            public void run()
            {
                UpdateSubway();
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long interval = 1 * 20000;
        timer.scheduleAtFixedRate(timerTask, delay, interval);
        activated = true;
        return true;
    }

    public static synchronized InfoFinder getInstance() {
        if (instance == null)
        {
            instance = new InfoFinder();
        }
        return  instance;
    }

    public boolean Update(List<Item> items)
    {
        if(items == null) return false;
        if(items.size() <= 0) return false;
        for(AsyncTask task : subwayTasks)
        {
            task.cancel(true);
        }
        subwayItems.clear();
        for(Item item : items)
        {
            if(item instanceof SubwayItem)
            {
                subwayItems.add((SubwayItem) item);
            }
        }
        UpdateSubway();
        if(updateTask != null)
        {
            updateTask.cancel(true);
        }
        for(AsyncTask task : tasks)
        {
            task.cancel(true);
        }
        updateTask = new UpdateTask(items).execute();
        return  true;
    }

    public boolean isUpdating()
    {
        if(updateTask == null) return false;
        return updateTask.getStatus() == AsyncTask.Status.RUNNING;
    }


    private class UpdateTask extends AsyncTask <Void, Void, Void>
    {
        List<Item> items;
        public UpdateTask(List<Item> items) {
            super();
            this.items = items;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tasks.clear();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (Item item : items)
            {
                TaskEventListner taskEventListener = new TaskEventListner() {
                    @Override
                    public void OnTaskComplete(AsyncTask task) {
                        tasks.remove(task);
                        Log.d("InfoFinder","Task completed ("+tasks.size()+")");
                    }

                    @Override
                    public void OnTaskCanceled(AsyncTask task) {
                        tasks.remove(task);
                        Log.d("InfoFinder","Task canceled ("+tasks.size()+")");
                    }
                };
                AsyncTask searchImageTask = new SearchImageTask(item, taskEventListener).execute();
                tasks.add(searchImageTask);
                if(item instanceof  ShopItem)
                {
                    ShopItem shopItem = (ShopItem)item;
                    AsyncTask searchShopTask = new SearchShopTask(shopItem,taskEventListener).execute();
                    tasks.add(searchShopTask);
                    AsyncTask searchBlogTask = new SearchBlogTask(shopItem,taskEventListener).execute();
                    tasks.add(searchBlogTask);
                }
                if(item instanceof TheaterItem)
                {
                    TheaterItem theaterItem = (TheaterItem)item;
                    AsyncTask searchMovieTask = new SearchMovieTask(theaterItem,taskEventListener).execute();
                    tasks.add(searchMovieTask);
                }
                Log.d("InfoFinder","Task started ("+tasks.size()+")");
                if(isCancelled())
                {
                    return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }
    private void UpdateSubway()
    {
        TaskEventListner taskEventListner = new TaskEventListner() {
            @Override
            public void OnTaskComplete(AsyncTask task) {
                subwayTasks.remove(task);
            }

            @Override
            public void OnTaskCanceled(AsyncTask task) {
                subwayTasks.remove(task);
            }
        };
        Log.d("InfoFinder>Subway>","Start subway update (" +subwayItems.size() +")");
        for (SubwayItem item : subwayItems)
        {
            AsyncTask task =  new SubwayUpdateTask(item,taskEventListner).execute();
            subwayTasks.add(task);
        }
    }
    private class SubwayUpdateTask extends AsyncTask <Void, Void, Void>
    {
        SubwayItem item;
        TaskEventListner taskListen;
        public SubwayUpdateTask(SubwayItem item, TaskEventListner taskListen) {
            this.item = item;
            this.taskListen = taskListen;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("InfoFinder>Subway>", "Start subway update");
            listeners.removeIf(p -> p == null);
            for (InfoFinderEventListener listener : listeners) {

                listener.OnSubwayUpdateStart(item);
            }
        }
        @Override
        protected Void doInBackground(Void... voids) {

            Request request = request = getArrivalRequest(item.getName());
            item.setArrivalNodes(requestArrival(request));
            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("InfoFinder>Subway>", "End subway update");
            if(ItemManager.getInstance().items.contains(item)) {
                listeners.removeIf(p -> p == null);
                for (InfoFinderEventListener listener : listeners) {
                    listener.OnSubwayUpdateEnd(item);
                }
            }
            if(taskListen != null)
            {
                taskListen.OnTaskComplete(this);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(taskListen != null)
            {
                taskListen.OnTaskCanceled(this);
            }
        }

        private List<ArrivalNode> requestArrival(Request request)
        {
            List<ArrivalNode> nodes = new ArrayList<ArrivalNode>();
            if(request == null) return  nodes;
            Response response = null;
            String responseText = null;
            try {
                response = httpClient.newCall(request).execute();
                responseText = response.body().string();
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("InfoFinder>Arrival>" + item.getName(), e.toString());
                return nodes;
            }
            if (response == null || responseText == null || !response.isSuccessful()) return nodes;
            Log.d("InfoFinder>Arrival>" + item.getName(), response.message());
            Log.d("InfoFinder>Arrival>" + item.getName(), "Subway response exist" + responseText);
            JsonParser parser = new JsonParser();
            JsonObject root = (JsonObject) parser.parse(responseText);
            JsonArray jsonArray = (JsonArray) root.get("realtimeArrivalList");
            if (jsonArray != null && jsonArray.size() > 0)
            {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonItem = (JsonObject) jsonArray.get(i);
                    ArrivalNode node =  parseArrivalJsonItem(jsonItem);
                    nodes.add(node);
                }
            }
            return nodes;

        }

        private ArrivalNode parseArrivalJsonItem(JsonObject jsonItem)
        {
            int trainId = jsonItem.get("bstatnId").getAsInt();
            String upDown = jsonItem.get("updnLine").getAsString();
            String lineName = jsonItem.get("trainLineNm").getAsString();
            String curStation = jsonItem.get("arvlMsg3").getAsString();
            String arrivalLeft = "";
            String arvlMsg2 =  jsonItem.get("arvlMsg2").getAsString();
            if(arvlMsg2 != null)
            {
                String[] messages = arvlMsg2.split(" ");
                if(messages.length >0)
                {
                    if(messages[0].matches("분"))
                    {
                        arrivalLeft = messages[0];
                    }
                    else
                    {
                        arrivalLeft = messages[0] + " " + messages[1];
                    }
                }

            }
            ArrivalNode node = new ArrivalNode.builder()
                    .setTrainId(trainId)
                    .setUpDown(upDown)
                    .setLineName(lineName)
                    .setCurStation(curStation)
                    .setArrivalLeft(arrivalLeft)
                    .build();
            return node;
        }

        private Request getArrivalRequest(String subwayName)
        {
            Request request;
            subwayName = subwayName.replace(" ", "");
            subwayName = subwayName.replace("역", "");
            //String prefix = "http://swopenAPI.seoul.go.kr/api/subway//json/realtimeStationArrival/0/2/까치산";
            String prefix = "http://swopenAPI.seoul.go.kr/api/subway";
            String key = Define.getSubwayKey();
            String docType = "json";
            String service = "realtimeStationArrival";
            String start = "0";
            String end = "5";
            String query = subwayName;
            String url = prefix + "/" + key +"/" + docType + "/" + service + "/" +start + "/" + end + "/" + query;
            Log.d("InfoFinder"+subwayName, "Subway Request:"+url);
            request = new Request.Builder()
                    .url(url)
                    .build();
            return  request;
        }


    }
    private class SearchImageTask extends AsyncTask <Void, Void, Void>
    {

        Item item;
        TaskEventListner taskListen;
        public SearchImageTask(Item item, TaskEventListner taskListen) {
            super();
            this.item = item;
            this.taskListen = taskListen;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("InfoFinder>Image>"+item.getName(),"Start search image");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnImageUpdateStart(item);
            }
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if(item.getPhotoMetadata() != null)
            {
                FetchPhotoRequest photoRequest = FetchPhotoRequest
                        .builder(item.getPhotoMetadata())
                        .setMaxWidth(1000)
                        .setMaxHeight(1000)
                        .build();
                Task task = placeClient.fetchPhoto(photoRequest)
                        .addOnFailureListener((exception) ->
                        {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                int statusCode = apiException.getStatusCode();
                                // Handle error with given status code.
                                Log.e("InfoFinder>"+item.getName(), "Exception: " + exception.getMessage());
                            }
                        })
                        .addOnSuccessListener((photoRespone) ->
                        {
                            //Log.d("InfoFinder>Photo","Photo exist (" +place.getName()+")" );
                            Bitmap thumb = photoRespone.getBitmap();
                            if (thumb != null)
                            {
                                item.setThumbnail(thumb);
                            }
                        });
                while (!task.isComplete())
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("InfoFinder>Image>"+item.getName(),"End search image");
            if(ItemManager.getInstance().items.contains(item))
            {
                listeners.removeIf(p -> p == null);
                for(InfoFinderEventListener listener : listeners)
                {
                    listener.OnImageUpdateEnd(item);
                }
            }
            if(taskListen != null)
            {
                taskListen.OnTaskComplete(this);
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(taskListen != null)
            {
                taskListen.OnTaskCanceled(this);
            }
        }
    }
    private class SearchShopTask extends AsyncTask <Void,Void,Void>
    {
        ShopItem item;
        TaskEventListner taskListen;

        public SearchShopTask(ShopItem item, TaskEventListner taskListen) {
            super();
            this.item = item;
            this.taskListen = taskListen;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d("InfoFinder>Shop>"+item.getName(),"Start search shop information");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnShopUpdateStart(item);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //구글 검색
            if(item.getPlaceID() != "")
            {
                //Place Fectch Init
                String placeId = item.getPlaceID();
                List<Place.Field> fields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
                //Place Fectch Start
                FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId,fields).build();
                Task task = placeClient.fetchPlace(placeRequest)
                    .addOnFailureListener((exception) ->
                    {
                        if(exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("InfoFinder>"+item.getName(), "Exception: " + exception.getMessage());
                        }
                    })
                    .addOnSuccessListener((respone) ->
                    {
                        Place foundPlace = respone.getPlace();
                        if(foundPlace != null)
                        {
                            item.setPlace(foundPlace);
                        }
                    });
                while (!task.isComplete())
                {
                    try
                    {
                        if(isCancelled()) return null;
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Place place =item.getPlace();
                if(place != null)
                {
                    if(item instanceof ShopItem)
                    {
                        ShopItem shopItem = (ShopItem) item;
                        String phoneNumber = place.getPhoneNumber();
                        if(phoneNumber != null)
                        {
                            //phoneNumber = phoneNumber.replace("+82 ","0");
                            shopItem.setTelNumber(phoneNumber);
                        }
                        Uri uri = place.getWebsiteUri();
                        if(uri != null)
                        {
                            shopItem.setUri(uri);
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("InfoFinder>Shop>"+item.getName(),"End search shop information");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnShopUpdateEnd(item);
            }
            if(taskListen != null)
            {
                taskListen.OnTaskComplete(this);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(taskListen != null)
            {
                taskListen.OnTaskCanceled(this);
            }
        }
    }
    private class SearchBlogTask extends AsyncTask <Void,Void,Void>
    {
        ShopItem item;
        TaskEventListner taskListen;

        public SearchBlogTask(ShopItem item, TaskEventListner taskListen) {
            super();
            this.item = item;
            this.taskListen = taskListen;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d("InfoFinder>Blog>"+ item.getName(), "Start search blog information");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnBlogUpdateStart(item);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(item.getName() != "")
            {
                Request request = getBlogRequest(item.getName());
                if(request != null)
                {
                    item.setNaverBlogs(requestBlog(request));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("InfoFinder>Blog>"+ item.getName(),"End search blog information");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnBlogUpdateEnd(item);
            }
            if(taskListen != null)
            {
                taskListen.OnTaskComplete(this);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(taskListen != null)
            {
                taskListen.OnTaskCanceled(this);
            }
        }
        private List<NaverBlogNode> requestBlog(Request request)
        {
            List<NaverBlogNode> nodes = new ArrayList<NaverBlogNode>();
            if(request == null) return nodes;
            Response response = null;
            String responseText = null;
            try {
                response = httpClient.newCall(request).execute();
                responseText = response.body().string();
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                Log.e("InfoFinder>Blog>"+ item.getName(), e.toString());
                return nodes;
            }
            if (response == null || responseText == null || !response.isSuccessful()) return nodes;
            Log.d("InfoFinder>Blog"+ item.getName(), "Response exist: " + response.message());
            JsonParser parser = new JsonParser();
            JsonObject root = (JsonObject) parser.parse(responseText);
            JsonArray jsonArray = (JsonArray) root.get("items");
            if(jsonArray != null && jsonArray.size()>0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    if(jsonObject != null)
                    {
                        NaverBlogNode node = parseBlogJson(jsonObject);
                        nodes.add(node);
                    }
                }
            }
            return nodes;
        }
        private NaverBlogNode parseBlogJson(JsonObject jsonItem)
        {
            String title = jsonItem.get("title").getAsString();
            Uri url = null;
            try {
                url = Uri.parse(jsonItem.get("link").getAsString());
            } catch (Exception e) {
                Log.e("InfoFinder>Blog", e.toString());
            }
            String description = jsonItem.get("description").getAsString();
            String bloggerName = jsonItem.get("bloggername").getAsString();
            Uri bloggerUrl = null;
            try {
                bloggerUrl = Uri.parse(jsonItem.get("bloggerlink").getAsString());
            } catch (Exception e) {
                Log.e("InfoFinder>Blog", e.toString());
            }
            String postDateStr = jsonItem.get("postdate").getAsString();
            Date postDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
            try {
                postDate = format.parse(postDateStr);
            } catch (Exception e) {
                Log.e("InfoFinder>Blog", e.toString());
            }
            NaverBlogNode node = new NaverBlogNode.builder()
                    .setTitle(title)
                    .setDescription(description)
                    .setBloggerName(bloggerName)
                    .setBloggerUri(bloggerUrl)
                    .setUri(url)
                    .setPostDate(postDate)
                    .build();
            return node;
        }

        private Request getBlogRequest(String keyword)
        {
            Request request;
            keyword = keyword.replace(" ", "");
            String prefix = "https://openapi.naver.com/v1/search/blog.json?";
            String query = "query="+ keyword;
            String display = "display=5";
            String start = "start=1";
            String sort = "sort=sim";
            String url = prefix + query +"&" + display + "&" + start + "&" + sort ;
            Log.d("InfoFinder"+item.getName(), "Blog Request:"+url);
            request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Naver-Client-Id",Define.getNaverClientID())
                    .addHeader("X-Naver-Client-Secret",Define.getNaverClientSecret())
                    .build();
            return  request;
        }

    }
    private class SearchMovieTask extends AsyncTask <Void,Void,Void>
    {
        TheaterItem item;
        TaskEventListner taskListen;

        public SearchMovieTask(TheaterItem item, TaskEventListner taskListen) {
            super();
            this.item = item;
            this.taskListen = taskListen;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d("InfoFinder>Movie>"+ item.getName(), "Start search movie");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnTheaterUpdateStart(item);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(item.getName() != "")
            {
                String url = getMovieUrl(item.getName());
                if(url != null && url != "")
                {
                    item.setMovieNodes(requestMovieNodes(url));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("InfoFinder>Movie>"+ item.getName(),"End search movie");
            listeners.removeIf(p -> p == null);
            for(InfoFinderEventListener listener : listeners)
            {
                listener.OnTheaterUpdateEnd(item);
            }
            if(taskListen != null)
            {
                taskListen.OnTaskComplete(this);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(taskListen != null)
            {
                taskListen.OnTaskCanceled(this);
            }
        }
        private List<MovieNode> requestMovieNodes(String url)
        {
            List<MovieNode> nodes = new ArrayList<MovieNode>();
            if(url == null || url == "") return nodes;
            Document response = null;
            try {
                response = Jsoup.connect(url).get();
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                Log.e("InfoFinder>Movie>"+ item.getName(), e.toString());
                return nodes;
            }
            if (response == null) return nodes;
            Log.d("InfoFinder>Movie"+ item.getName(), "Response exist");
            Element movieBox = response.select(".screen_list").first();
            if(movieBox == null) return nodes;
            Elements movieElements = movieBox.select("li");

            for(Element movieElement : movieElements)
            {
                MovieNode node =parseMovieElement(movieElement);
                nodes.add(node);
            }
            return nodes;
        }
        private MovieNode parseMovieElement(Element movieElement)
        {
            Elements elements = movieElement.select("a");
            String title = "";
            Uri uri = null;
            String timeName = "";
            List<TimeNode> timeNodes = new ArrayList<TimeNode>();
            int i = 0;
            for(Element element : elements)
            {
                if(i == 0)
                {
                    if(element.hasText())
                    {
                        title = element.ownText();
                    }
                    if(element.hasAttr("lp-api")) {
                        String prefix = element.baseUri();
                        String api = "#api=";
                        String uriStr = Define.encodeURI(element.attr("lp-api"));
                        String lpType = "&_lp_type=cm";
                        uri = Uri.parse(prefix+api+uriStr+lpType);
                    }
                }
                else
                {
                    if(element.hasText())
                    {
                        timeName = element.text();
                    }
                    Uri timeUri = null;
                    if(element.hasAttr("href"))
                    {
                        String timeUriStr = element.attr("href");
                        timeUri = Uri.parse(timeUriStr);
                    }
                    timeNodes.add( new TimeNode.builder()
                            .setTimeStr(timeName)
                            .setUri(timeUri)
                            .build()
                    );
                }
                i++;
            }
            MovieNode node = new  MovieNode.builder()
                    .setTitle(title)
                    .setUri(uri)
                    .setTimes(timeNodes)
                    .build();
            //node.Logging();
            return node;
        }

        private String getMovieUrl(String keyword)
        {
            keyword = keyword.replace(" ", "+");
            String prefix = "https://m.search.naver.com/search.naver?";
            String query = "query="+keyword;
            String url = prefix+query;
            Log.d("InfoFinder>Movie>"+ item.getName(), "Movie Request:"+ url );
            return url;
        }

    }


}


