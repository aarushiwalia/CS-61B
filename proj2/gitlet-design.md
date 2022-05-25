# Gitlet Design Document

**Aarushi Walia**:

## Classes and Data Structures

### Main
The main class here serves a similar purpose to that of the Capers lab main class. It takes in arguments from the command line and makes calls to the functions stored away in the Repository file. It also accounts for some failure cases and ensures that the argument(s) being passed in is/are not empty.
#### Fields

1. public static final File CWD = new File(System.getProperty("user.dir")) represents the current working directory, and is useful for the other Files we will use.
2. public static final File GITLET_DIR = join(CWD, ".gitlet") represents the information in the Gitlet directory.
3. public static final File REPO_OBJECT = join(GITLET_DIR, "repo") represents where all of the information from Repository.java will be stored.

These fields are static because we don't instantiate these files.

### Repository

#### Fields

1. public static final File CWD = new File(System.getProperty("user.dir")) represents the current working directory, and is useful for the other Files we will use.
2. public static final File GITLET_DIR = join(CWD, ".gitlet") represents the information in the .gitlet directory.
3. public static final File COMMITS_DIR = join(GITLET_DIR, "commits") represents the full directory of commits made by the user at the command line.
4. public static final File BLOB_DIR = join(GITLET_DIR, "blob") represents the actual content within each commited piece of code made by the user.
5. private HashMap branch represents the set of all of the branches in the program.
6. private HashMap stagingAreaAdded represents the files that are staged for addition.
7. private HashMap stagingAreaRemoved represents the files that are staged for removal.
8. private String head represents the head branch pointer, or where the current hashed commit is.
9. private String workingBranch represents the current branch the user is on.

This class represents a repository containing all of the methods/commands that are to be entered by the user at the command line. All of the commands are serialized within the respective directories.

### Commit

#### Fields

1. public static final File CWD = new File(System.getProperty("user.dir")) represents the current working directory, and is useful for the other Files we will use.
3. public static final File COMMITS_DIR = join(GITLET_DIR, "commits") represents the full directory of commits made by the user at the command line.
3. private String message represents the message that the commit outputs.
4. private String theDate represents the date/time that the commit is made at. 
5. private HashMap files represents the files tracked by the specific commit.
6. private String parent represents the parent of the commit.
7. private String author represents the author/user of the specific commit.
8. private String otherParent represents the second parent of this specific commit.

This class essentially instantiates and stores the information of a commit made by the user. It includes methods that simply return the instance variables.

## Algorithms
- Instantiation: this is represented by the init command, which instantiates the three directories: gitlet, commits, blob, in addition to the other noted instance variables. Then, all of the branches are saved and the first commit is made and hashed away. Additionally, the current working branch is set to be "master".
- Addition: First, load the staging area files that are to be added and removed and create a new file to be added. If it's the case that it doesn't exist, print the specific failure case log. Otherwise, "string" the file. If the sha1 value for the working blob and file blob are equal, remove that file from the staging area, otherwise, put the file in the staging area and save the contents of the blob and staging area.
- Commit: If it's the case that the message is empty, print out the specific failure case to enter a commit message. Otherwise, instantiate a new commit and load the staging area contents. If it's the case that the staging area contents are emprt and the message is "merged", then, that means there's no change to the commit. We then want to add the staging area contents to the commit, hash the head, add the current branch to the list of branches, and save all of the branches. Finally, clear all of the contents anf save the staging area contents.
- Deletion: First, load the staging area files that are to be added and removed and create a new file to be removed. If it's the case that it doesn't exist, print the specific failure case log. Otherwise, "string" the file. If the sha1 value for the working blob and file blob are equal, remove that file from the staging area, otherwise, put the file in the staging area files for removal and save the contents of the blob and staging area.
- Log: load the commit and ensure that the commit has a parent. Then, print the specific contents as notated in the spec and reset the commit variable to the value of loading the commit's parent and again, print the contents.
- Global Log: similar to log, but we want to ensure that the files in the commits directory aren't empty. Then, we want to iterate over those files in that directory and do what we did above in log.
- Find: We want to first ensure that the commits directory isn't empty, and then iterate over the files in that directory. If it's the case the current commit's message equals the passed in message, this means that we found the commit and want to print the file. Otherwise, if we haven't found the commit message, print that specific failure case message.
- Status: We want to iterate over all of the branches and add the string, and do the same for the staging area files and removed files. There isn't really much going on this command that is special, it's more of just handling specific cases and printing those specific contents.
- Checkout: We want to reduce the content size of the commit of interest if it's too large, otherwise, we can check to see if the commit of interest is within the files of the commits directory; if it's not, print the specific failure case. Otherwise, we want to create a new variable that represents the current commit and ensure that the files we have already visited contain the passed in file. We then hash that commit and write the contents of the new file and the blobbed contents.
- CheckoutFile: checkout the head branch pointer and the passed in file.
- CheckoutID: checkout the passed in file and the id passed in.
- CheckoutBranch: load the branches and create a headbranch variable that represents the passed in branch value in the branch list. Then, iterate over the files in the CWD and ensure that the branched commit contains the passed in file. Check to make sure that the current blob doesn't equal the blob contents of the branch of interest, otherwise, print the specific failure case. Similarly, iterate over the staging area files to be added, the branch commit files, and the current commit files. Then clear the contents, save the staging area contents, and reset the current branch to the passed in branch and the head to the instantiated head branch.
- Branch: load all of the branches and ensure that the existing branch list contains the passed in branch; add that specific branch to the map of existing branches and save all of the branches. Otherwise, print the specific failure case.
- Remove Branch: We want to first load all of the branches and ensure that the map of branches isn't empty. If it's the case that the passed in branch is within the map of already exisiting branches, then we cannot remove that branch, otherwise, we want to simply remove the branch from the map and save the remaining branches left within the hashmap.
- Reset: We want to load all of the branches and first ensure that the passed in commit exists within the commits directory and isn't too long. Then, we want to create two new commits that represent the current commit and the first head commit. We want to track those files and add those temporary stringed commits into the branches map, save those contents, and checkout that branch. After checking out that branch, we can then remove the temporary branch and save the contents.
- Merge: There is a lot going on in this command, where we first load all of the contents and branches. We then want to ensure that the branch of interest isn't empty and that the staging area files for addition and removal aren't empty. We also want to ensure that the current branch isn't untracked. If it's the case that the branch of interest equals the current branch we're on, then we know we can't merge the branch to itself so we jusr return. Otherwise, if the split point equals the passed in branch, we also know that we can't do anything with ancestral branches.

## Persistence

So, we know we have the few directories representing the current working directory, commits, blobs, and the .gitlet one. So the hierarchical order of these directories is as follows:
CWD
-->.gitlet
---->Commits
---->blobs
The Repository will set up all persistence. It will create the .gitlet folder if it doesn't exist and create the commits and blobs folders respectively if they do not exist as well. I discussed above the methods in the Repository, Commits, and Main class files.