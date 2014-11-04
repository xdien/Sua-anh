package com.crazy.xdien.imageedit.sliding.sliding;

/**
 * Created by xdien on 9/26/14.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazy.xdien.imageedit.R;

public class HomeFragment extends Fragment {

    public HomeFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //imageView = (ImageViewTouch) rootView.findViewById(R.id.main_imageView);
        return rootView;
    }
}