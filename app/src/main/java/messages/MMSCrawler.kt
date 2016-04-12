package messages

import android.content.Context
import android.net.Uri
import java.util.*

class MMSCrawler(val context: Context) : IMessageCrawler {

    override fun findMessages(address: String): List<Message> {
        val uri = Uri.parse("content://mms/inbox")
        val cursor = context.contentResolver.query(uri, arrayOf("*"), null, null, null)
        val idCol = cursor.getColumnIndex("_id")
        val dateCol = cursor.getColumnIndex("date")

        val msgs = ArrayList<Message>()
        while (cursor.moveToNext()) {
            val id = cursor.getString(idCol)
            val addr = getMMSAddr(id)

            if (addr != address)
                continue

            val body = getMMSText(id)
            if (body == null) // 다운로드 받지 못한 MMS일 가능성이 높음
                continue

            val date_ms = cursor.getLong(dateCol)
            val date = Date(date_ms * 1000)

            val msg = Message(address, body, date)
            msgs.add(msg)
        }

        cursor.close()
        return msgs.sortedBy { it.date }
    }

    private fun getMMSText(id: String): String? {
        val uri = Uri.parse("content://mms/part")
        val cursor = context.contentResolver.query(uri, arrayOf("*"), "mid = $id", null, null)

        if (cursor.moveToFirst() == false) {
            cursor.close()
            return null
        }

        val textCol = cursor.getColumnIndex("text")
        val text = cursor.getString(textCol)
        cursor.close()

        return text
    }

    private fun getMMSAddr(id: String): String? {
        val uri = Uri.parse("content://mms/$id/addr")
        val selection = "msg_id = $id"
        val cursor = context.contentResolver.query(uri, arrayOf("*"), selection, null, null)

        if (cursor.moveToFirst() == false) {
            cursor.close()
            return null
        }

        val addr = cursor.getString(cursor.getColumnIndex("address"))
        cursor.close()

        return addr
    }

}