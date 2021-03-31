package com.yoon.quest.SubData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.yoon.quest.AppData;

@Entity
public class SubDataModel {
    /**
     * id = primaryKEY
     * autoGenerate -> 자동으로 증가
     * */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String color;

    // 생성자
    public SubDataModel(String title, String content, String color) {
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String title) {
        this.content = content;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() { return title; }

    public int getId() { return id; }
    public String getContent() {
        return content;
    }
    public String getColor() {
        return color;
    }


    public int getCount(){
        return AppData.GetInstance().mSubDataCnt;
    }

    /**
     * toString 재정의 -> 내용확인하기 위함
     * */
    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DataModel{");
        sb.append("id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", color=").append(color).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        SubDataModel myEntity = (SubDataModel) obj;
//        return myEntity.id == this.id && myEntity.title == this.title && myEntity.content == this.content && myEntity.color == this.color;
        return myEntity.id == this.id;
    }

    public static DiffUtil.ItemCallback<SubDataModel> DIFF_CALLBACK = new  DiffUtil.ItemCallback<SubDataModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull SubDataModel oldItem, @NonNull SubDataModel newItem) {
            return oldItem.id == newItem.id;
        }
        @Override
        public boolean areContentsTheSame(@NonNull SubDataModel oldItem, @NonNull SubDataModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
