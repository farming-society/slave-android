package io.github.farming_society.slave_android

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import messages.BankMessageGenerator
import messages.MessageCrawler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() };

        val msgCrawler = MessageCrawler(this)
        val msgs = msgCrawler.findMessages("15778000")

        Log.d("crawler", msgs.count().toString())

        val bankMsgs = BankMessageGenerator.parseAndGenerate(msgs).filter { it.accountNo == "***" } // TODO implement properties
        bankMsgs.forEach {
            Log.d("crawler", it.hashCode().toString() + "@" + it.accountNo + " >> " + it.date.toString() + "::" + it.inout + " @ " + it.body)
        }

        // pseudo
        // from git
        // convert to json object
        // merge & sort by date
        // upload
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
