//1つのセルにあるデータを保存するためのデータクラス。
package com.example.hiroshima.myapplication;

public class MessageRecord {
    private String imageUrl;
    private String mainText;
    private String comment;
    private String id;

    //データを１つ作成する関数。
    public MessageRecord(String id,String imageUrl,String comment, String mainText) {
        this.imageUrl = imageUrl;
        this.mainText = mainText;
        this.comment = comment;
        this.id = id;
    }
    //それぞれの項目を返す関数。
    public String getComment() {
        return comment;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getMainText() {
        return mainText;
    }
    public String getId() {
        return id;
    }
}
