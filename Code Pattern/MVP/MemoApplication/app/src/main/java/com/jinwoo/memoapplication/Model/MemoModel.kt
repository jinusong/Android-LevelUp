package com.jinwoo.memoapplication.Model

data class MemoModel(var title: String = "", var contents: String = "", var date: String = ""){
    fun toMap(path: String): Map<String, Any?> {
        var map :HashMap<String, Any?> = HashMap()
        map.put("$path title", this.title)
        map.put("$path contents", this.contents)
        map.put("$path date", this.date)

        return map
    }
}