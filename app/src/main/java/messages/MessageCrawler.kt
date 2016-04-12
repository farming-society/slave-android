package messages

import android.content.Context
import java.util.*

data class Message (val address: String, val body: String, val date: Date)

interface IMessageCrawler {
    fun findMessages(address: String) : List<Message>
}

class MessageCrawler(val context: Context) : IMessageCrawler {

    val mmsCrawler : IMessageCrawler = MMSCrawler(context)
    val smsCrawler : IMessageCrawler = SMSCrawler(context)

    override fun findMessages(address: String): List<Message> {
        val mmsMsgs = mmsCrawler.findMessages(address)
        val smsMsgs = smsCrawler.findMessages(address)
        return mergeMsgs(mmsMsgs, smsMsgs)
    }

    fun mergeMsgs(a: List<Message>, b: List<Message>) : List<Message> {
        val merged = ArrayList<Message>(a.count() + b.count())
        merged.addAll(a)
        merged.addAll(b)
        return merged.sortedBy { it.date }
    }

}