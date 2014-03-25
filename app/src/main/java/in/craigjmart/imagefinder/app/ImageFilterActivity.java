package in.craigjmart.imagefinder.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ImageFilterActivity extends Activity {
    Spinner spSize;
    Spinner spColor;
    Spinner spType;
    EditText etImageSite;

    //query filter params
    String qSize = "";
    String qColor = "";
    String qType = "";
    String qSite = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        setupViews();

        setInitialValues();
    }

    private void setupViews() {
        spSize = (Spinner)findViewById(R.id.spSize);
        spColor = (Spinner)findViewById(R.id.spColor);
        spType = (Spinner)findViewById(R.id.spType);
        etImageSite = (EditText)findViewById(R.id.etImageSite);
    }

    private void setInitialValues() {
        //get data from the intent
        qSize = getIntent().getStringExtra(ImageSearchActivity.SIZE);
        qColor = getIntent().getStringExtra(ImageSearchActivity.COLOR);
        qType = getIntent().getStringExtra(ImageSearchActivity.TYPE);
        qSite = getIntent().getStringExtra(ImageSearchActivity.SITE);

        //for each spinner set the value
        if(!qSize.isEmpty()){
            setSpinnerToValue(spSize, qSize);
        }
        if(!qColor.isEmpty()){
            setSpinnerToValue(spColor, qColor);
        }
        if(!qType.isEmpty()){
            setSpinnerToValue(spType, qType);
        }

        etImageSite.setText(qSite);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_filter, menu);
        return true;
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void onSavePressed(View view) {
        qSize = spSize.getSelectedItem().toString();
        qColor = spColor.getSelectedItem().toString();
        qType = spType.getSelectedItem().toString();
        qSite = etImageSite.getText().toString();

        Intent i = new Intent();
        i.putExtra(ImageSearchActivity.SIZE, qSize);
        i.putExtra(ImageSearchActivity.COLOR, qColor);
        i.putExtra(ImageSearchActivity.TYPE, qType);
        i.putExtra(ImageSearchActivity.SITE, qSite);
        setResult(RESULT_OK, i);
        finish();
    }
}
