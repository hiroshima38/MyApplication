//1つのセルにあるデータを保存するためのデータクラス。
package com.example.hiroshima.myapplication;

public class MessageRecord {
    private String imageUrl;
    private String mainText;
    private String comment;
    private String id;
    private int goodCount;
    private int veryGoodCount;

    //データを１つ作成する関数。
    public MessageRecord(String id,String imageUrl,String comment, String mainText, int goodCount, int veryGoodCount) {
        this.imageUrl = imageUrl;
        this.mainText = mainText;
        this.comment = comment;
        this.id = id;
        this.goodCount = goodCount;
        this.veryGoodCount = veryGoodCount;
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
    public int getGoodCount(){
        return goodCount;
    }
    public void setGoodCount(int goodCount){
        this.goodCount = goodCount;
    }
    public int getVeryGoodCount(){
        return veryGoodCount;
    }
    public void setVeryGoodCount(int veryGoodCount){
        this.veryGoodCount = veryGoodCount;
    }
}
