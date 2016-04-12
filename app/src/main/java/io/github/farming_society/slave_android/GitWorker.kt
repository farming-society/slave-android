package io.github.farming_society.slave_android

import android.os.Environment
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitWorker : Thread {

    constructor() {
    }

    override fun run() {
        val extPath = Environment.getExternalStorageDirectory().absolutePath;
        val repoPath = extPath + "/slave-android";
        var repoDir = File(repoPath);

        if (repoDir.isDirectory) {
            repoDir.deleteRecursively();
        }

        val remoteUri = "https://github.com/farming-society/farming-society.github.io.git";
        val result = Git.cloneRepository().setURI(remoteUri).setDirectory(repoDir).call();

        val testFile = File(repoDir, "test.json");
        testFile.writeText("{}");

        val cp = UsernamePasswordCredentialsProvider("***", "***"); // TODO Inject user information

        result.add().addFilepattern("test.json").call();
        result.commit().setAuthor("heejinbot", "hjhome200@naver.com").setCommitter("heejinbot", "hjhome2000+1@gmail.com").setMessage("Modified test.json").call();
        result.push().setCredentialsProvider(cp).call();
        result.close();

        repoDir.deleteRecursively();
    }

}