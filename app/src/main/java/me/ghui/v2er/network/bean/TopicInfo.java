package me.ghui.v2er.network.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.AvatarUtils;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo extends BaseInfo {
    @Pick("div#Wrapper")
    private HeaderInfo headerInfo;
    @Pick("div.box")
    private ContentInfo contentInfo;
    @Pick("div[id^=r_]")
    private List<Reply> replies;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "meta[property=og:url]", attr = "content")
    private String topicLink;

    public String getTopicLink() {
        return topicLink;
    }

    private List<Item> items;

    public String getOnce() {
        return once;
    }

    public Map<String, String> toReplyMap(String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    /**
     * 加载分页后的数据
     *
     * @param isLoadMore
     * @return
     */
    public List<Item> getItems(boolean isLoadMore) {
        if (items == null) {
            items = new ArrayList<>(Utils.listSize(replies) + 2);
        } else {
            items.clear();
        }

        if (!isLoadMore) {
            items.add(headerInfo);
            if (contentInfo.isValid()) {
                items.add(contentInfo);
            }
        }
        String owner = headerInfo.getUserName();
        if (PreConditions.notEmpty(replies)) {
            for (Reply reply : replies) {
                reply.setOwner(owner);
            }
            items.addAll(replies);
        }
        return items;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    public int getTotalPage() {
        return headerInfo.getPage();
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "topicLink=" + topicLink +
                ", headerInfo=" + headerInfo +
                ", replies=" + replies +
                ", once=" + once +
                '}';
    }

    @Override
    public boolean isValid() {
        return headerInfo.isValid();
    }

    public static class ContentInfo extends BaseInfo implements Item {
        @Pick(value = "div.cell div.topic_content", attr = Attrs.INNER_HTML)
        private String contentHtml;
        @Pick("div.subtle")
        private List<PostScript> postScripts;

        public String getContentHtml() {
            return contentHtml;
        }

        public void setContentHtml(String contentHtml) {
            this.contentHtml = contentHtml;
        }

        public List<PostScript> getPostScripts() {
            return postScripts;
        }

        public void setPostScripts(List<PostScript> postScripts) {
            this.postScripts = postScripts;
        }

        @Override
        public String getUserName() {
            return null;
        }

        @Override
        public String getAvatar() {
            return null;
        }

        @Override
        public boolean isValid() {
            return PreConditions.notEmpty(contentHtml);
        }

        @Override
        public boolean isHeaderItem() {
            return false;
        }

        @Override
        public boolean isContentItem() {
            return true;
        }

        @Override
        public boolean isSelf() {
            return false;
        }

        public static class PostScript {
            @Pick("span.fade")
            private String header;
            @Pick(value = "div.topic_content", attr = Attrs.INNER_HTML)
            private String content;

            public String getHeader() {
                return header;
            }

            public String getContent() {
                return content;
            }

            @Override
            public String toString() {
                return "PostScript{" +
                        "header='" + header + '\'' +
                        ", content='" + content + '\'' +
                        '}';
            }
        }
    }

    public static class HeaderInfo extends BaseInfo implements Item {
        @Pick(value = "div.box img.avatar", attr = "src")
        private String avatar;
        @Pick("div.box small.gray a")
        private String userName;
        @Pick(value = "div.box small.gray", attr = "ownText")
        private String time;
        @Pick("div.box a[href^=/go]")
        private String tag;
        @Pick(value = "div.box a[href^=/go]", attr = Attrs.HREF)
        private String tagLink;
        @Pick("div.cell span.gray")
        private String comment;
        @Pick("div.box a.page_normal:last-child")
        private int page;
        @Pick("div.box h1")
        private String title;
        @Pick(value = "div.box a[href*=favorite/]", attr = Attrs.HREF)
        private String favoriteLink;
        @Pick("div.box div[id=topic_thank] span.f11.gray")
        private String thankedText;

        public HeaderInfo() {
        }

        private HeaderInfo(TopicBasicInfo basicInfo) {
            this.avatar = basicInfo.getAvatar();
            this.title = basicInfo.getTitle();
//            this.comment = basicInfo.getCommentNum() + " 回复";
            this.userName = basicInfo.getAuthor();
            this.tag = basicInfo.getTag();
        }

        public static HeaderInfo build(TopicBasicInfo basicInfo) {
            return new HeaderInfo(basicInfo);
        }

        @Override
        public boolean isValid() {
            return PreConditions.notEmpty(userName, tag);
        }

        /**
         * new user can't send thanks
         *
         * @return
         */
        public boolean canSendThanks() {
            return !PreConditions.isEmpty(thankedText);
        }

        public boolean hadThanked() {
            return PreConditions.notEmpty(thankedText) && thankedText.contains("已发送");
        }

        public void setFavoriteLink(String favoriteLink) {
            this.favoriteLink = favoriteLink;
        }

        public String getFavoriteLink() {
            return favoriteLink;
        }

        public void updateThxStatus(boolean thxed) {
            thankedText = thxed ? "感谢已发送" : null;
        }

        public void updateStarStatus(boolean stared) {
            if (stared) {
                // TODO: 21/07/2017 favoriteLink is null
                favoriteLink = favoriteLink.replace("/favorite/", "/unfavorite/");
            } else {
                favoriteLink = favoriteLink.replace("/unfavorite/", "/favorite/");
            }
        }

        public String getT() {
            if (PreConditions.isEmpty(favoriteLink)) {
                return null;
            }
            return UriUtils.getParamValue(Constants.BASE_URL + favoriteLink, "t");
        }

        public boolean hadStared() {
            if (PreConditions.isEmpty(favoriteLink) || !favoriteLink.contains("unfavorite/")) {
                return false;
            }
            return true;
        }

        public String getCommentNum() {
            if (PreConditions.isEmpty(comment)) return null;
            return "评论" + comment.split(" ")[0];
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTag() {
            return tag;
        }

        public String getTime() {
            try {
                return time.split(",")[0].trim().substring(6).replaceAll(" ", "").trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public int getViewCount() {
            try {
                String count = time.split(",")[1].trim();
                return Integer.parseInt(count.substring(0, count.indexOf(" ")));
            } catch (Exception e) {
                return 0;
            }
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public String getTitle() {
            return title;
        }

        public int getPage() {
            return page < 1 ? 1 : page;
        }


        @Override
        public String toString() {
            return "HeaderInfo{" +
                    "tId='" + getT() + '\'' +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + getTime() + '\'' +
                    ", tag='" + tag + '\'' +
                    ", comment='" + comment + '\'' +
                    ", page=" + page +
                    ", title='" + title + '\'' +
                    '}';
        }

        @Override
        public boolean isHeaderItem() {
            return true;
        }

        @Override
        public boolean isContentItem() {
            return false;
        }

        @Override
        public boolean isSelf() {
            try {
                return userName.equals(UserUtils.getUserInfo().getUserName());
            } catch (NullPointerException e) {
                return false;
            }
        }


    }

    public static class Reply implements Item {
        @Pick(value = "div.reply_content", attr = Attrs.INNER_HTML)
        private String replyContent;
        @Pick("strong a.dark[href^=/member]")
        private String userName;
        @Pick(value = "img.avatar", attr = "src")
        private String avatar;
        @Pick("span.fade.small:contains(前)")
        private String time;
        @Pick("span.fade.small:contains(♥)")
        private String love;
        @Pick("span.no")
        private int floor;
        @Pick("div.thank_area.thanked")
        private String alreadyThanked;
        @Pick(attr = "id")
        private String replyId;
        private boolean isOwner = false;

        public void setOwner(String owner) {
            if (PreConditions.notEmpty(userName) && PreConditions.notEmpty(owner) && owner.equals(userName)) {
                isOwner = true;
            } else {
                isOwner = false;
            }
        }

        public boolean isOwner() {
            return isOwner;
        }

        public String getFloor() {
            return floor + "楼";
        }

        public String getReplyId() {
            if (PreConditions.isEmpty(replyId)) return null;
            try {
                return replyId.substring(replyId.indexOf("_") + 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String toString() {
            return "Reply{" +
                    "replyContent='" + replyContent + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", time='" + time + '\'' +
                    ", love='" + love + '\'' +
                    ", floor='" + floor + '\'' +
                    '}';
        }

        public boolean hadThanked() {
            return PreConditions.notEmpty(alreadyThanked);
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getLove() {
            int loveCount = 0;
            if (PreConditions.isEmpty(love)) {
                return loveCount;
            }
            try {
                loveCount = Integer.parseInt(love.substring(2));
            } catch (Exception e) {
                e.printStackTrace();
                loveCount = 0;
            }
            return loveCount;
        }

        public void setLove(int count) {
            this.love = "♥ " + count;
        }

        public void updateThanks(boolean isSuccess) {
            if (isSuccess) {
                alreadyThanked = "感谢已发送";
                this.love = "♥ " + (getLove() + 1);
            }
        }

        @Override
        public boolean isHeaderItem() {
            return false;
        }

        @Override
        public boolean isContentItem() {
            return false;
        }

        @Override
        public boolean isSelf() {
            try {
                return userName.equals(UserUtils.getUserInfo().getUserName());
            } catch (NullPointerException e) {
                return false;
            }
        }
    }

    public interface Item {
        boolean isHeaderItem();

        boolean isContentItem();

        boolean isSelf();

        String getUserName();

        String getAvatar();
    }

}
