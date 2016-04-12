package messages

import android.content.Context
import android.net.Uri
import java.util.*

class SMSCrawler(val context: Context) : IMessageCrawler {

    override fun findMessages(address: String): List<Message> {
        val uri = Uri.parse("content://sms/inbox")
        val selection = "address = $address"
        val cursor = context.contentResolver.query(uri, arrayOf("*"), selection, null, null)

        val msgs = ArrayList<Message>()

        val dateCol = cursor.getColumnIndex("date")
        val bodyCol = cursor.getColumnIndex("body")

        while (cursor.moveToNext()) {
            val date_ms = cursor.getLong(dateCol)
            val body = cursor.getString(bodyCol)
            val date = Date(date_ms)
            val msg = Message(address, body, date)
            msgs.add(msg)
        }
        cursor.close()

        return msgs.sortedBy { it.date }
    }

}