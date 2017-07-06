package me.ghui.v2er.module.imgviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import me.ghui.v2er.R;
import me.ghui.v2er.util.Utils;


/**
 * Created by ghui on 5/18/16.
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private SubsamplingScaleImageView mImageView;
    private ProgressBar progressBar;
    private static String IMG_URL = "com.lantouzi.app.url";

    public static ImageDetailFragment newInstance(String url) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMG_URL, url);
        Log.e("img", "url: " + url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMG_URL) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.imageview);
        mImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        mImageView.setOnClickListener(v -> getActivity().finish());
        loadImage();
    }

    private void loadImage() {
        Glide.with(getContext()).load(mImageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mImageView.setImage(ImageSource.bitmap(resource));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                Utils.toast("图片加载出错");
            }
        });
    }


}
