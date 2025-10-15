package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.registro_cuentas.db.QueueItem;

import java.util.List;

@Dao
public interface QueueItemDao {
    @Insert
    long insert(QueueItem item);

    @Query("SELECT * FROM queue_items ORDER BY `order` ASC")
    List<QueueItem> getAllQueueItems();

    @Query("SELECT * FROM queue_items ORDER BY `order` ASC LIMIT 1")
    QueueItem getFirstQueueItem();

    @Delete
    void delete(QueueItem item);

    @Query("DELETE FROM queue_items")
    void deleteAll();
}

