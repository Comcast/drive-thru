/**
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.drivethru.misc;

import java.util.ArrayList;
import java.util.List;

import com.comcast.pantry.test.RandomProvider;

public class CatPhoto {

    private String url;
    private String title;
    private int thumbsUp;
    private int thumbsDown;
    private List<String> tags;

    public CatPhoto randomize(RandomProvider random) {
        this.url = random.nextString(10, 20);
        this.title = random.nextString(10, 20);
        this.thumbsUp = random.nextInt(0, 1000);
        this.thumbsDown = random.nextInt(0, 1000);

        int tagCount = random.nextInt(0, 6);
        this.tags = new ArrayList<>(tagCount);
        for (int i = 0; i < tagCount; i++) {
            this.tags.add(random.nextString(3, 10));
        }

        return this;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the thumbsUp
     */
    public int getThumbsUp() {
        return thumbsUp;
    }

    /**
     * @param thumbsUp
     *            the thumbsUp to set
     */
    public void setThumbsUp(int thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    /**
     * @return the thumbsDown
     */
    public int getThumbsDown() {
        return thumbsDown;
    }

    /**
     * @param thumbsDown
     *            the thumbsDown to set
     */
    public void setThumbsDown(int thumbsDown) {
        this.thumbsDown = thumbsDown;
    }

    /**
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + thumbsDown;
        result = prime * result + thumbsUp;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CatPhoto)) {
            return false;
        }
        CatPhoto other = (CatPhoto) obj;
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        if (thumbsDown != other.thumbsDown) {
            return false;
        }
        if (thumbsUp != other.thumbsUp) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }
}
