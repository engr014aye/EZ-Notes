package com.fawadini.eznotes.data.local.converter

import androidx.room.TypeConverter
import com.fawadini.eznotes.data.model.TodoItem
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class Converters {

    @TypeConverter
    fun fromTodoItemList(items: List<TodoItem>?): String {
        if (items.isNullOrEmpty()) return "[]"
        return try {
            val array = JSONArray()
            for (item in items) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("text", item.text)
                obj.put("isDone", item.isDone)
                array.put(obj)
            }
            array.toString()
        } catch (e: Exception) {
            "[]"
        }
    }

    @TypeConverter
    fun toTodoItemList(json: String?): List<TodoItem> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<TodoItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TodoItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        text = obj.optString("text", ""),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
