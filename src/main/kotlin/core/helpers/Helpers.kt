package core.helpers

import com.google.gson.Gson


class Helpers {
    companion object {
        fun jsonify(obj: Any): String {
            return Gson().toJson(obj)
        }

        fun queryToMap(query: String): Map<String, String> {
            val result: MutableMap<String, String> = HashMap()

            for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                if (pair.size > 1) {
                    result[pair[0]] = pair[1]
                } else {
                    result[pair[0]] = ""
                }
            }
            return result
        }
    }
}
