package com.yoon.quest.SubData;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object -> Todo Entity 에 접근하기 위한 인터페이스
 *
 * @Dao annotation 추가
 * Todo 에서 어떤 동작을 할 것인지 정의한다.
 */
@Dao
public interface SubDataModelDAO {
    /**
     * Todo 라는 테이블에 여러 내용이 있을 것이다.
     * Todo 의 모든 내용을 List 에 담는 getAll() 이라는 메소드가 필요하다.
     * Query annotation -> 실제 sql query 를 사용할 수 있음
     * LiveData<객체> -> 해당 객체는 관찰 가능한 객체가 된다.
     **/
    @Query("SELECT * FROM SubDataModel")
    LiveData<List<SubDataModel>> getAll();

    /**
     * id로 데이터 찾기
     */
    @Query("SELECT * FROM SubDataModel WHERE id = :mId")
    SubDataModel getData(int mId);

    /**
     * color로 데이터 찾기
     */
    @Query("SELECT * FROM SubDataModel WHERE color = :mColor")
    List<SubDataModel> getDataIncludeColor(String mColor);


    /**
     * DataModel에서 color로 데이터 찾기
     */
    @Query("SELECT * FROM DataModel WHERE color = :mColor")
    List<SubDataModel> getDataPickedColor(String mColor);

    /**
     * Insert annotation -> 내용추가
     **/
    @Insert
    void insert(SubDataModel dataModel);

    /**
     * Delete annotation -> 내용 삭제
     **/
    @Delete
    void delete(SubDataModel dataModel);

    /**
     * id로 데이터를 찾고 입력받은 String 으로 전체 내용을 변경
     **/
    @Query("UPDATE SubDataModel SET title =:mTitle, content =:mContent, color = :mColor  WHERE id =:mId ")
    void dataAllUpdate(int mId, String mTitle, String mContent, String mColor);

    /**
     * id로 데이터를 찾고 입력받은 String 으로 내용을 변경
     **/
    @Query("UPDATE SubDataModel SET  content =:mContent,color = :mColor  WHERE id =:mId ")
    void dataContentUpdate(int mId, String mContent, String mColor);

    /**
     * Clear All -> 리스트 전체삭제
     **/
    @Query("DELETE FROM SubDataModel")
    void clearAll();

    /**
     * for onItemMove
     */
    @Query("SELECT * FROM SubDataModel ORDER BY id ASC")
    LiveData<List<SubDataModel>> sortByPosition();
}