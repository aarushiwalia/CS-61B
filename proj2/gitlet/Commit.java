package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/** Represents a gitlet commit object.
 *  This class accounts for all of the commits made by the users.
 *
 *  @author Aarushi Walia
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final File COMMITS_DIR = join(CWD, ".gitlet", "commits");
    private String message;
    private String theDate;
    private HashMap files;
    private String parent;
    private String author;
    private String otherParent;

    public Commit(String msg, String parent, String other, Date date) {
        message = msg;
        this.parent = parent;
        otherParent = other;
        files = new HashMap<String, String>();
        author = "user";

        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d HH:mm:ss y Z");
        theDate = dateFormat.format(date);

        if (!date.equals(new Date(0))) {
            Commit parentCommit = commitLoaded(parent);

            for (Object obj: parentCommit.files.entrySet()) {
                HashMap.Entry entryMap = (Map.Entry) obj;
                files.put(entryMap.getKey(), entryMap.getValue());
            }
            if (otherParent != null) {
                Commit otherComm = commitLoaded(otherParent);
                for (Object obj: otherComm.files.entrySet()) {
                    HashMap.Entry entryMap = (Map.Entry) obj;
                    files.put(entryMap.getKey(), entryMap.getValue());
                }
            }
        }
    }

    public static Commit commitLoaded(String hashedCommits) {
        File loaded = join(COMMITS_DIR, hashedCommits);
        return readObject(loaded, Commit.class);
    }

    public void trackLoadedFiles(String hashedCommits) {
        File trackedLoadedFiles = join(COMMITS_DIR, hashedCommits + "abc");
        files = readObject(trackedLoadedFiles, HashMap.class);
    }

    public void stagingAreaAdd(HashMap stagingArea) {
        for (Object obj: stagingArea.entrySet()) {
            HashMap.Entry entryMap = (Map.Entry) obj;
            files.put(entryMap.getKey(), entryMap.getValue());
        }
    }

    public void stagingAreaRemove(HashMap stagingArea) {
        Iterator stagedIterator = stagingArea.entrySet().iterator();
        while (stagedIterator.hasNext()) {
            Map.Entry current = (Map.Entry) stagedIterator.next();
            files.remove(current.getKey());
        }
    }

    public String message() {
        return message;
    }

    public String parent() {
        return parent;
    }

    public String author() {
        return author;
    }

    public String date() {
        return theDate;
    }

    public HashMap filesVisited() {
        return files;
    }

    public void otherParentChanged(String otherHashedCommits) {
        otherParent = otherHashedCommits;
    }
}
