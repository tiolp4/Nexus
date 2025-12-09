package com.example.nexus_social_network.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_social_network.Models.PostsAdapter;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.viewModel.FeedViewModel;

public class FeedFragment extends Fragment {

    private FeedViewModel viewModel;
    private PostsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView recycler = view.findViewById(R.id.recyclerPosts);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostsAdapter();
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        observe();
        load();

        return view;
    }

    private void observe() {
        viewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null)
                adapter.setPosts(posts);
        });
    }

    private void load() {
        String token = requireContext()
                .getSharedPreferences("my_prefs", getContext().MODE_PRIVATE)
                .getString("jwt_token", "");

        viewModel.loadPosts(token);
    }
}
