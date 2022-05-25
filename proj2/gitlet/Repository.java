package gitlet;

import java.io.File;
import static gitlet.Utils.*;

import java.util.*;
import java.io.Serializable;


/** Represents a gitlet repository.
 *  The Repository class implements all of the commands that a user inputs in the command line.
 *
 *  @author Aarushi Walia
 */
public class Repository implements Serializable {
    /**

     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    private HashMap branch;
    private HashMap stagingAreaAdded;
    private HashMap stagingAreaRemoved;
    private String head;
    private String workingBranch;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already "
                   + "exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
            BLOB_DIR.mkdir();
            branch = new HashMap<String, String>();
            saveBranches();
            stagingAreaAdded = new HashMap<String, String>();
            saveAddStagingAreaContents();
            stagingAreaRemoved = new HashMap<String, String>();
            saveRemoveStagingAreaContents();
            Commit initialCommit = new Commit("initial commit", null, null, new Date(0));
            saveCommit(initialCommit);
            head = hashCommit(initialCommit);
            workingBranch = "master";
            branch.put(workingBranch, head);
        }
    }

    public static Commit loadCommit(String hashedCommits) {
        File loadedCommits = join(COMMITS_DIR, hashedCommits);
        Commit loadedCommit = readObject(loadedCommits, Commit.class);
        loadedCommit.trackLoadedFiles(hashCommit(loadedCommit));
        return loadedCommit;
    }

    public static void saveCommit(Commit commit) {
        String hashedCommit = hashCommit(commit);
        File commits = join(COMMITS_DIR, hashedCommit);
        File abcFile = join(COMMITS_DIR, hashedCommit + "abc");
        writeObject(commits, commit);
        writeObject(abcFile, commit.filesVisited());
    }

    public static String hashCommit(Commit commit) {
        byte[] serializedCommit = serialize(commit);
        return sha1(serializedCommit);
    }

    public void loadBranches() {
        File branchesF = join(GITLET_DIR, "branches");
        branch = readObject(branchesF, HashMap.class);
    }

    public void saveBranches() {
        File branchesFile = join(GITLET_DIR, "branches");
        writeObject(branchesFile, branch);
    }

    public void loadRemoveStagingAreaContents() {
        File stagingAreaRemoval = join(GITLET_DIR, "stagingAreaRemoval");
        stagingAreaRemoved = readObject(stagingAreaRemoval, HashMap.class);
    }

    public void saveRemoveStagingAreaContents() {
        File stagingAreaRemoval = join(GITLET_DIR, "stagingAreaRemoval");
        writeObject(stagingAreaRemoval, stagingAreaRemoved);
    }

    public void loadAddStagingAreaContents() {
        File stagingAreaAddition = join(GITLET_DIR, "stagingAreaAddition");
        stagingAreaAdded = readObject(stagingAreaAddition, HashMap.class);
    }

    public void saveAddStagingAreaContents() {
        File stagingAreaAddition = join(GITLET_DIR, "stagingAreaAddition");
        writeObject(stagingAreaAddition, stagingAreaAdded);
    }

    public void add(String name) {
        loadAddStagingAreaContents();
        loadRemoveStagingAreaContents();
        File addedFile = join(CWD, name);
        String blobbed = "File does not exist.";
        if (!addedFile.exists()) {
            System.out.println("File does not exist.");
            return;
        } else {
            blobbed = readContentsAsString(addedFile);
        }
        Commit currentCommit = loadCommit(head);
        Object workingBlob = currentCommit.filesVisited().get(name);
        if (workingBlob != null && ((String) workingBlob).equals(sha1(blobbed))) {
            stagingAreaAdded.remove(name);
            stagingAreaRemoved.remove(name);
        } else {
            stagingAreaAdded.put(name, sha1(blobbed));
            stagingAreaRemoved.remove(name);
            saveBlob(blobbed);
        }
        saveAddStagingAreaContents();
        saveRemoveStagingAreaContents();
    }

    public static void saveBlob(String blob) {
        File savedBlob = join(BLOB_DIR, sha1(blob));
        if (savedBlob.exists()) {
            return;
        }
        writeContents(savedBlob, blob);
    }

    public static String loadBlob(String blob) {
        File loadedBlob = join(BLOB_DIR, blob);
        return readContentsAsString(loadedBlob);
    }

    public void commit(String msg) {
        if (msg.length() == 0) {
            System.out.println("Please enter a commit message.");
        }
        Commit committed = new Commit(msg, head, null, new Date());
        loadAddStagingAreaContents();
        loadRemoveStagingAreaContents();
        if (stagingAreaAdded.isEmpty() && stagingAreaRemoved.isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        committed.stagingAreaAdd(stagingAreaAdded);
        committed.stagingAreaRemove(stagingAreaRemoved);
        saveCommit(committed);
        head = hashCommit(committed);
        branch.put(workingBranch, head);
        saveBranches();
        stagingAreaAdded.clear();
        stagingAreaRemoved.clear();
        saveAddStagingAreaContents();
        saveRemoveStagingAreaContents();
    }

    private String reduce(String loadedCommit) {
        for (Object file: plainFilenamesIn(COMMITS_DIR)) {
            String name = (String) file;
            if (loadedCommit.equals(name.substring(0, loadedCommit.length()))) {
                if (!name.substring(name.length() - 3).equals("abc")) {
                    loadedCommit = name;
                }
            }
        }
        return loadedCommit;
    }

    public void checkout(String loadedCommit, String name) {
        if (loadedCommit.length() < 40) {
            loadedCommit = reduce(loadedCommit);
        }
        if (!plainFilenamesIn(COMMITS_DIR).contains(loadedCommit)) {
            System.out.println("No commit with that id exists");
            return;
        }
        Commit commit = loadCommit(loadedCommit);
        if (!commit.filesVisited().containsKey(name)) {
            System.out.println("FIle does not exist in that commit.");
            return;
        }
        commit.trackLoadedFiles(hashCommit(commit));
        String blob = loadBlob((String) commit.filesVisited().get(name));
        File newFile = join(CWD, name);
        writeContents(newFile, blob);
    }

    public void checkoutFileName(String name) {
        checkout(head, name);
    }

    public void checkoutCommitID(String id, String name) {
        checkout(id, name);
    }

    public void checkoutBranchName(String name) {
        loadBranches();
        String headBranch = (String) branch.get(name);
        if (!branch.containsKey(name)) {
            System.out.println("No such branch exists.");
            return;
        } else if (name.equals(workingBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit branchCom = loadCommit(headBranch);
        Commit currentCom = loadCommit(head);

        for (Object file: plainFilenamesIn(CWD)) {
            String fileName = (String) file;
            if (!currentCom.filesVisited().containsKey(fileName)) {
                if (branchCom.filesVisited().containsKey(fileName)) {
                    String currentBlob = sha1(readContentsAsString(join(CWD, fileName)));
                    String branchedBlob = (String) branchCom.filesVisited().get(fileName);
                    if (!currentBlob.equals(branchedBlob)) {
                        System.out.println("There is an untracked file "
                               + "in the way: delete it, or add and commit it first.");
                        return;
                    }
                }
            }
        }
        for (Object file: branchCom.filesVisited().keySet()) {
            if (!branchCom.filesVisited().containsKey((String) file)) {
                restrictedDelete((String) file);
            }
            checkout(headBranch, (String) file);
        }

        for (Object file: stagingAreaAdded.keySet()) {
            if (!!branchCom.filesVisited().containsKey(file)) {
                restrictedDelete((String) file);
            }
        }

        for (Object file: currentCom.filesVisited().keySet()) {
            if (!branchCom.filesVisited().containsKey(file)) {
                restrictedDelete((String) file);
            }
        }

        File fileVisited = join(CWD, headBranch + "abc");
        writeObject(fileVisited, currentCom.filesVisited());
        stagingAreaAdded.clear();
        stagingAreaRemoved.clear();
        saveAddStagingAreaContents();
        saveRemoveStagingAreaContents();
        workingBranch = name;
        head = headBranch;
    }

    public void log() {
        Commit committed = loadCommit(head);
        while (committed.parent() != null) {
            System.out.println("===");
            System.out.println("commit " + hashCommit(committed));
            System.out.println("Date: " + committed.date());
            System.out.println(committed.message() + "\n");
            committed = loadCommit(committed.parent());
        }
        System.out.println("===");
        System.out.println("commit " + hashCommit(committed));
        System.out.println("Date: " + committed.date());
        System.out.println(committed.message() + "\n");
    }



    public void globalLog() {
        if (plainFilenamesIn(COMMITS_DIR) == null) {
            return;
        } else {
            for (String name: plainFilenamesIn(COMMITS_DIR)) {
                if (!(name.substring(name.length() - 3).equals("abc"))) {
                    Commit comm = loadCommit(name);
                    System.out.println("===");
                    System.out.println("commit " + hashCommit(comm));
                    System.out.println("Date: " + comm.date());
                    System.out.println(comm.message() + "\n");
                }
            }
        }
    }

    public void status() {
        loadBranches();
        loadRemoveStagingAreaContents();
        loadRemoveStagingAreaContents();

        System.out.println("=== Branches ===");
        ArrayList<String> branchIter = new ArrayList<>();
        for (Object string : branch.keySet()) {
            branchIter.add((String) string);
        }
        
        Collections.sort(branchIter);
        for (Object branched : branchIter) {
            String name = (String) branched;
            String code = (String) branch.get(name);
            if (code.equals(head)) {
                System.out.println("*" + name);
            } else {
                System.out.println(name);
            }
        }

        System.out.println();
        System.out.println("=== Staged Files ===");
        ArrayList<String> added = new ArrayList<>();
        for (Object string : stagingAreaAdded.keySet()) {
            added.add((String) string);
        }
        Collections.sort(added);
        for (Object item : added) {
            System.out.println(item);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        ArrayList<String> removed = new ArrayList<>();
        for (Object string : stagingAreaRemoved.keySet()) {
            removed.add((String) string);
        }
        Collections.sort(removed);
        for (Object obj : removed) {
            System.out.println(obj);
        }

        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void find(String msg) {
        if (plainFilenamesIn(COMMITS_DIR) == null) {
            return;
        }

        boolean foundMsg = false;
        for (String name: plainFilenamesIn(COMMITS_DIR)) {
            if (!(name.substring(name.length() - 3).equals("abc"))) {
                Commit com = loadCommit(name);
                if (com.message().equals(msg)) {
                    foundMsg = true;
                    System.out.println(name);
                }
            }
        }
        if (!foundMsg) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void branch(String name) {
        loadBranches();
        if (branch.containsKey(name)) {
            System.out.println("Branch with that name already exists.");
        } else {
            branch.put(name, head);
            saveBranches();
        }
    }

    public void rmBranch(String name) {
        loadBranches();
        if (branch.get(name) == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branch.get(name).equals(head)) {
            System.out.println("Cannot remove the current branch.");
        } else if (branch.containsKey(name)) {
            branch.remove(name);
            saveBranches();
        }
    }

    public void rm(String name) {
        loadRemoveStagingAreaContents();
        loadAddStagingAreaContents();
        String removedBlob = "File does not exist.";
        Commit commit = loadCommit(head);
        File removed = join(CWD, name);
        if (removed.exists()) {
            removedBlob = readContentsAsString(removed);
        }
        if (stagingAreaAdded.containsKey(name)) {
            stagingAreaAdded.remove(name);
        } else if (commit.filesVisited().containsKey(name)) {
            stagingAreaRemoved.put(name, sha1(removedBlob));
            restrictedDelete(name);
        } else {
            System.out.println("No reason to remove the file.");
        }
        saveRemoveStagingAreaContents();
        saveAddStagingAreaContents();
    }

    public void reset(String id) {
        loadBranches();
        if (!plainFilenamesIn(COMMITS_DIR).contains(id)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (id.length() < 40) {
            id = reduce(id);
        }

        Commit currentComm = loadCommit(id);
        Commit headComm = loadCommit(head);
        currentComm.trackLoadedFiles(id);
        headComm.trackLoadedFiles(head);
        String working = workingBranch;
        branch.put("temp", id);
        saveBranches();
        checkoutBranchName("temp");
        branch.remove("temp");
        workingBranch = working;
        branch.put(workingBranch, id);
        head = id;
        saveBranches();
    }

    public void merge(String name) {
        loadBranches();
        loadAddStagingAreaContents();
        loadRemoveStagingAreaContents();

        if (branch.get(name) == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (!(stagingAreaRemoved.isEmpty() && stagingAreaAdded.isEmpty())) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        boolean isUntracked = isUntracked(name);
        if (isUntracked) {
            return;
        }

        if (name.equals(workingBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        String splitPoint = split(name);
        if (splitPoint.equals(branch.get(name))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        ArrayList<String> otherParents = otherParents(name);
        if (otherParents.contains(head)) {
            checkoutBranchName(name);
            System.out.println("Current branch fast-forwarded");
            commit("Merged" + name + " into " + workingBranch + ".");
        }

        Commit currentCom = loadCommit(head);
        Commit branchCom = loadCommit((String) branch.get(name));
        Commit splitCom = loadCommit(splitPoint);
        boolean isConflicted = false;
        for (Object tFile : branchCom.filesVisited().keySet()) {
            String currBlob = (String) currentCom.filesVisited().get(tFile);
            String branchBlob = (String) branchCom.filesVisited().get(tFile);
            String splitBlob = (String) splitCom.filesVisited().get(tFile);
            if (splitBlob == null) {
                if (currBlob == null) {
                    checkout((String) branch.get(name), (String) tFile);
                    stagingAreaAdded.put(tFile, branchBlob);
                } else if (!currBlob.equals(branchBlob)) {
                    isConflicted = isConflicted(currentCom, branchCom, (String) tFile);
                } else if (branchBlob == null) {
                    deleteFile((String) tFile, splitPoint);
                }
            }
        }
    }

    private boolean isUntracked(String name) {
        loadBranches();
        String headBranch = (String) branch.get(name);
        Commit currentCom = loadCommit(head);
        Commit branchCom = loadCommit(headBranch);
        for (Object file: plainFilenamesIn(CWD)) {
            String fileName = (String) file;
            if (!currentCom.filesVisited().containsKey(fileName)) {
                if (branchCom.filesVisited().containsKey(fileName)) {
                    String currentBlob = sha1(readContentsAsString(join(CWD, fileName)));
                    String branchedBlob = (String) branchCom.filesVisited().get(fileName);
                    if (!currentBlob.equals(branchedBlob)) {
                        System.out.println("There is an untracked file "
                                + "in the way: delete it, or add and commit it first.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String split(String name) {
        Commit currentCom = loadCommit(head);
        Commit branchCom = loadCommit((String) branch.get(name));
        ArrayList<String> theParents = new ArrayList<>();

        while (currentCom.parent() != null) {
            theParents.add(currentCom.parent());
            currentCom = loadCommit(currentCom.parent());
        }

        if (theParents.contains((String) branch.get(name))) {
            return (String) branch.get(name);
        }

        while (branchCom.parent() != null) {
            if (theParents.contains(branchCom.parent())) {
                break;
            }
            branchCom = loadCommit(branchCom.parent());
        }
        return branchCom.parent();
    }

    private ArrayList<String> otherParents(String name) {
        ArrayList<String> otherParents = new ArrayList<>();
        Commit branchCom = loadCommit((String) branch.get(name));
        while (branchCom.parent() != null) {
            otherParents.add(branchCom.parent());
            branchCom = loadCommit(branchCom.parent());
        }
        return otherParents;
    }

    private boolean isConflicted(Commit currentCom, Commit branchCom, String tFile) {
        String currString = (String) currentCom.filesVisited().get(tFile);
        String branchString = (String) branchCom.filesVisited().get(tFile);
        File conflicted = join(CWD, tFile);
        String currL = "";
        String branchL = "";

        if (currString != null) {
            currL = loadBlob(currString);
        }
        if (branchString != null) {
            branchL = loadBlob(branchString);
        }
        writeContents(conflicted, "<<<<<<< HEAD" + "\n");
        stagingAreaAdded.put(tFile, sha1(readContentsAsString(conflicted)));
        return true;
    }

    private void deleteFile(String fileName, String splitPoint) {
        loadRemoveStagingAreaContents();
        Commit splitCom = loadCommit(splitPoint);
        String splitBlob = (String) splitCom.filesVisited().get(fileName);
        restrictedDelete(fileName);
        stagingAreaRemoved.put(fileName, splitBlob);
    }
}

