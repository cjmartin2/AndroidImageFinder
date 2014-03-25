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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.widget.SearchView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageSearchActivity extends SherlockFragmentActivity {
    String query;
    GridView gvResults;
    public static String KEY_IMG_RESULT = "img_result";

    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;

    //query filter params
    String qSize = "";
    String qColor = "";
    String qType = "";
    String qSite = "";


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
                doSearch(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.miFilter) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSearch(int page){
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        //create query string
        String url =
                "https://ajax.googleapis.com/ajax/services/search/images?" +
                "&v=1.0" +
                "&rsz=8" +
                "&q=" + Uri.encode(query);

        Log.d("DEBUG", url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray imageJsonResults = null;
                try{
                    imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.clear();
                    //addAll warns about not being compatible with API v10
                    imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                    Log.d("DEBUG", imageResults.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
