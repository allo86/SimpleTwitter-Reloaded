package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ALLO on 3/8/16.
 */
@Parcel
public class Entities {

    @SerializedName("media")
    @Expose
    ArrayList<Media> media;

    @SerializedName("user_mentions")
    @Expose
    ArrayList<TwitterUser> userMentions;

    @SerializedName("hashtags")
    @Expose
    ArrayList<Hashtag> hashtags;

    public Entities() {

    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public ArrayList<TwitterUser> getUserMentions() {
        return userMentions;
    }

    public void setUserMentions(ArrayList<TwitterUser> userMentions) {
        this.userMentions = userMentions;
    }

    public ArrayList<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    /* Helpers */

    public boolean hasVideo() {
        if (media != null && media.size() > 0) {
            for (Media mediaItem : media) {
                if (mediaItem.isVideo()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPhoto() {
        if (media != null && media.size() > 0) {
            for (Media mediaItem : media) {
                if (mediaItem.isPhoto()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Media getPhoto() {
        if (media != null && media.size() > 0) {
            for (Media mediaItem : media) {
                if (mediaItem.isPhoto()) {
                    return mediaItem;
                }
            }
        }
        return null;
    }

    public VideoInfo getVideoInfo() {
        if (media != null && media.size() > 0) {
            for (Media mediaItem : media) {
                if (mediaItem.isVideo()) {
                    return mediaItem.getVideoInfo();
                }
            }
        }
        return null;
    }

    public VideoVariant getVideo() {
        if (media != null && media.size() > 0) {
            for (Media mediaItem : media) {
                if (mediaItem.isVideo()) {
                    if (mediaItem.getVideoInfo() != null) {
                        if (mediaItem.getVideoInfo().getVariants() != null && mediaItem.getVideoInfo().getVariants().size() > 0) {
                            return mediaItem.getVideoInfo().getVariants().get(0);
                        }
                    }
                }
            }
        }
        return null;
    }
}
