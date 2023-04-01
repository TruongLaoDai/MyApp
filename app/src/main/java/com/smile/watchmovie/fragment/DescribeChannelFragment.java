package com.smile.watchmovie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.FragmentDescribeChannelBinding;


public class DescribeChannelFragment extends Fragment {
    FragmentDescribeChannelBinding binding;

    public DescribeChannelFragment() {
        // Required empty public constructor
    }

    public static DescribeChannelFragment newInstance(String param1, String param2) {
        DescribeChannelFragment fragment = new DescribeChannelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDescribeChannelBinding.inflate(inflater, container, false);
        return inflater.inflate(R.layout.fragment_describe_channel, container, false);
    }
}