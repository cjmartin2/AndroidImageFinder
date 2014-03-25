package in.craigjmart.imagefinder.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created by admin on 3/24/14.
 */
public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {

    public ImageResultArrayAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageResult imageInfo = this.getItem(position);
        SmartImageView ivImage;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ivImage = (SmartImageView) inflater.inflate(R.layout.item_image_result, parent, false);
        }else {
            ivImage = (SmartImageView)convertView;
            ivImage.setImageResource(android.R.color.transparent);
        }
        ivImage.setImageUrl(imageInfo.getThumbnailUrl());
        return ivImage;
    }
}
