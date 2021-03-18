package com.yoon.quest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DataModel {
    /**
     * id = primaryKEY
     * autoGenerate -> 자동으로 증가
     * */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    // 생성자
    public DataModel(String title){
        this.title = title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getTitle() { return title; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    /**
     * toString 재정의 -> 내용확인하기 위함
     * */
    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DataModel{");
        sb.append("id=").append(id);
        sb.append(", title=").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        DataModel myEntity = (DataModel) obj;
        return myEntity.id == this.id && myEntity.title == this.title;
    }

    public static DiffUtil.ItemCallback<DataModel> DIFF_CALLBACK = new  DiffUtil.ItemCallback<DataModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return oldItem.id == newItem.id;
        }
        @Override
        public boolean areContentsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
