package in.craigjmart.imagefinder.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.widget.SearchView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageSearchActivity extends SherlockFragmentActivity {
    String query = "";
    GridView gvResults;
    public static final String KEY_IMG_RESULT = "img_result";
    public static final String SIZE = "qSize";
    public static final String COLOR = "qColor";
    public static final String TYPE = "qType";
    public static final String SITE = "qSite";
    private static final int FILTER_REQUEST = 1010;

    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;

    //query filter params
    String qSize = "";
    String qColor = "";
    String qType = "";
    String qSite = "";
    int qPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        setupViews();

        imageAdapter = new ImageResultArrayAdapter(this, imageResults);
        gvResults.setAdapter(imageAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
                ImageResult imageResult = imageResults.get(position);
                i.putExtra(KEY_IMG_RESULT, imageResult);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                qPage = page;
                if(qPage > 0){
                    performJustSearch();
                }
            }
        });

        //when there are no results to show (at app open), should display an image/tutorial,
        // and a search button, where search button will trigger the SearchView
    }

    private void setupViews(){
        gvResults = (GridView)findViewById(R.id.gvResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.image_search, menu);

        //setup Menu Search
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchString) {
                // perform query here
                query = searchString;
                doNewSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //auto-complete would have been cool
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void onFilterPressed(MenuItem mi){
        Intent i = new Intent(this, ImageFilterActivity.class);
        i.putExtra(SIZE, qSize);
        i.putExtra(COLOR, qColor);
        i.putExtra(TYPE, qType);
        i.putExtra(SITE, qSite);

        startActivityForResult(i, FILTER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if(requestCode == FILTER_REQUEST){
            qSize = i.getStringExtra(SIZE);
            qColor = i.getStringExtra(COLOR);
            qType = i.getStringExtra(TYPE);
            qSite = i.getStringExtra(SITE);

            //update results
            if(query != ""){
                doNewSearch();
            }
        }
    }

    //need separate onClick function, so I can clear search results
    private void doNewSearch(){
        imageAdapter.clear();
        qPage = 0;
        performJustSearch();
    }

    private void performJustSearch(){
        //create query string
        String url =
                "https://ajax.googleapis.com/ajax/services/search/images?" +
                "&v=1.0" +
                "&rsz=8" +
                "&imgsz=" + qSize +
                "&imgtype=" + qType +
                "&imgcolor=" + qColor +
                "&as_sitesearch=" + qSite +
                "&start=" + Integer.toString(qPage * 8) + //request size didn't seem to factor in, and only got 1 new image each 'page'
                "&q=" + Uri.encode(query);

Log.d("DEBUG", url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray imageJsonResults = null;
                try{
                    imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
                    //addAll warns about not being compatible with API v10
                    imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
