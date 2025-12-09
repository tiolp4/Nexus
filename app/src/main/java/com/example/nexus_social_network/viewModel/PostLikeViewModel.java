package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.repository.PostLikeRepository;

import java.util.Arrays;

public class PostLikeViewModel extends ViewModel {

    private final PostLikeRepository repo = new PostLikeRepository();

    private final MutableLiveData<Boolean> liked = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> likesCount = new MutableLiveData<>(0);

    public LiveData<Boolean> getLiked() { return liked; }
    public LiveData<Integer> getLikesCount() { return likesCount; }

    public void toggleLike(String token, int postId) {
        Boolean isLiked = liked.getValue();
        if (isLiked != null && isLiked) {
            repo.unlikePost(token, postId).observeForever(success -> {
                if (success) {
                    liked.setValue(false);
                    likesCount.setValue(likesCount.getValue() - 1);
                }
            });
        } else {
            repo.likePost(token, postId).observeForever(success -> {
                if (success) {
                    liked.setValue(true);
                    likesCount.setValue(likesCount.getValue() + 1);
                }
            });
        }
    }

    public void loadLikes(int postId) {
        repo.getLikes(postId).observeForever(usersArray -> {
            if (usersArray != null) {
                likesCount.setValue(Arrays.asList(usersArray).size());
            } else {
                likesCount.setValue(0);
            }
        });
    }



}
