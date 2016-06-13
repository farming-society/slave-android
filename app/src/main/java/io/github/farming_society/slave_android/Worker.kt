package io.github.farming_society.slave_android

import android.content.Context
import android.os.Environment
import android.util.Log
import messages.BankMessageGenerator
import messages.CryptoUtil
import messages.MessageCrawler
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class Worker : Thread {

    val context: Context
    var repoUrl: String? = null
    var repoUser: String? = null
    var repoPassword: String? = null
    var repoJsonFile: String? = null
    var repoJsonPass: String? = null
    var accountNo: String? = null

    constructor(context: Context) {
        this.context = context
        loadProps()
    }

    fun loadProps() {
        val ins = contextClassLoader.getResourceAsStream("config.properties")
        val props = Properties()
        props.load(ins)
        ins.close()

        repoUrl = props.getProperty("repo_url")
        repoUser = props.getProperty("repo_user")
        repoPassword = props.getProperty("repo_password")
        repoJsonFile = props.getProperty("repo_json_file")
        repoJsonPass = props.getProperty("repo_json_pass")
        accountNo = props.getProperty("account_no")
    }

    override fun run() {
        val crawler = MessageCrawler(context)
        val msgs = crawler.findMessages("15778000")
        val bankMsgs = BankMessageGenerator.parseAndGenerate(msgs)

        val repoDir = File(getRepoPath())
        if (repoDir.isDirectory)
            repoDir.deleteRecursively()

        val result = Git.cloneRepository().setURI(repoUrl).setDirectory(repoDir).call()
        val dataFile = File(repoDir, repoJsonFile)

        var storedItems = JSONArray()
        if (dataFile.exists() && dataFile.isFile) {
            val encrypted = dataFile.readText()
            val decrypted = CryptoUtil.decrypt(repoJsonPass!!, encrypted)
            storedItems = JSONArray(decrypted)
        }

        var hashes = HashSet<Int>()
        for (i in 0..(storedItems.length()-1)) {
            val item = storedItems.getJSONObject(i)
            hashes.add(item.getInt("hash"))
        }

        var updatedCount = 0
        for (bankMsg in bankMsgs) {
            if (bankMsg.accountNo != accountNo)
                continue

            val hash = bankMsg.hashCode()
            if (hashes.contains(hash))
                continue

            val jsonObj = JSONObject()
            jsonObj.put("hash", hash)
            jsonObj.put("account_no", bankMsg.accountNo)
            jsonObj.put("balance", bankMsg.balance)
            jsonObj.put("body", bankMsg.body)
            jsonObj.put("date", bankMsg.date.toString())
            jsonObj.put("inout", bankMsg.inout)

            storedItems.put(jsonObj)
            updatedCount += 1
        }

        Log.d("Worker", "$updatedCount items were updated")

        val resultJson = storedItems.toString(2)
        dataFile.writeText(CryptoUtil.encrypt(repoJsonPass!!, resultJson))

        val cp = UsernamePasswordCredentialsProvider(repoUser, repoPassword);
        result.add().addFilepattern(repoJsonFile).call();
        result.commit().setAuthor("heejinbot", "hjhome200@naver.com").setCommitter("heejinbot", "hjhome2000+1@gmail.com").setMessage("Update data").call();
        result.push().setCredentialsProvider(cp).call();
        result.close();

        repoDir.deleteRecursively();
    }

    fun getRepoPath(): String {
        val extPath = Environment.getExternalStorageDirectory().absolutePath;
        return extPath + "/slave-android";
    }

}