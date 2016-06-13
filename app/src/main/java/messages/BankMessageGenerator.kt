package messages

import android.util.Log
import java.util.*

data class BankMessage (val accountNo: String, val date: Date, val inout: Int, val balance: Int, val body: String)

object BankMessageGenerator {

    val rule = Regex("""신한(\d\d)\/(\d\d)\s+(\d\d):(\d\d)[^\d]+(\d\d\d-\d\d\d-\d\d\d\d\d\d)\n+(출금|입금)\s+([\d,]+)\n+잔액\s+([\d,]+)\n?\s*([^\n\r\u0000]*)""")

    fun parseAndGenerate(messages: List<Message>) : List<BankMessage> {
        val bankMsgs = ArrayList<BankMessage>()

        for (msg in messages) {
            val result = rule.find(msg.body)
            if (result == null) {
                Log.d("Parser", "Not matched: " + msg.body)
                continue
            }

            val year = msg.date.year
            val month = result.groupValues[1].toInt() - 1
            val day = result.groupValues[2].toInt()
            val hour = result.groupValues[3].toInt()
            val minute = result.groupValues[4].toInt()
            val date = Date(year, month, day, hour, minute)

            val accountNo = result.groupValues[5]
            val inoutType = result.groupValues[6]
            var inout = result.groupValues[7].replace(",", "").toInt()
            if (inoutType == "출금")
                inout = -inout

            val balance = result.groupValues[8].replace(",", "").toInt()
            val body = result.groupValues[9].trim()

            val bankMsg = BankMessage(accountNo, date, inout, balance, body)
            bankMsgs.add(bankMsg)
        }

        Log.d("Parser", "${messages.count()} => ${bankMsgs.count()}")
        return bankMsgs.sortedBy { it.date }
    }

}