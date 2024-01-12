package com.iamverycute.ascii_art_sample;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.neberox.library.asciicreator.ASCIIConverter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ASCIIArtSampleActivity extends AppCompatActivity implements ItemChangeListener {

    ArrayList<SlideModel> imageResources = new ArrayList<>();
    ImageView imgPreview;
    ASCIIConverter converter;
    ImageSlider slider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //add img
        imageResources.add(new SlideModel(R.drawable.linux, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.android, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.freebsd, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.mm, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.zz, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.zz2, ScaleTypes.CENTER_INSIDE));
        imageResources.add(new SlideModel(R.drawable.zz3, ScaleTypes.CENTER_INSIDE));
        //create ascii converter object
        converter = new ASCIIConverter(this);
        converter.setFontSize(5);
        converter.setReversedLuminance(false);
        converter.setGrayScale(false);
        //init view
        imgPreview = findViewById(R.id.img_preview);
        slider = findViewById(R.id.image_slider);
        slider.setImageList(imageResources);
        slider.setItemChangeListener(this);
        onItemChanged(0);
    }

    @Override
    public void onItemChanged(int i) {
        SlideModel m = imageResources.get(i);
        if (m.getImagePath() != null && m.getImagePath() > 0) {
            try {
                imgPreview.setImageBitmap(converter.createASCIIImage(BitmapFactory.decodeResource(getResources(), m.getImagePath())));
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }
    }
}