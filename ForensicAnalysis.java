package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {

        // WRITE YOUR CODE HERE
        Profile newProfile = new Profile();
        int person = StdIn.readInt();
        STR[] s = new STR[person];
        for (int i = 0; i < person; i++){
            String name = StdIn.readString();
            int numOfOccurrence = StdIn.readInt();
            STR newSTR = new STR(name, numOfOccurrence);
            s[i] = newSTR;
        }
        newProfile.setStrs(s);
        
        return newProfile; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        // WRITE YOUR CODE HERE
        TreeNode ptr = treeRoot;
        TreeNode prev = null;

        while (ptr != null){
            if(name.compareTo(ptr.getName()) == 0){ //if the name is already there
                ptr.setProfile(newProfile);
                return;
            }
            prev = ptr;
            if(name.compareTo(ptr.getName()) < 0){ //go left
                ptr = ptr.getLeft();
            }else{ //go right
                ptr = ptr.getRight();
            }
        }
        //once at the end of the tree to insert
        TreeNode newNode = new TreeNode(name, newProfile, null, null);
        if (prev == null){ //if tree is empty
            treeRoot = newNode;
        }else if (name.compareTo(prev.getName()) < 0){ //attach to be the left child
            prev.setLeft(newNode);
        }else{
            prev.setRight(newNode); //attach to be the right child
        }
    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // WRITE YOUR CODE HERE
        int count = 0;
        Queue<TreeNode> profileQueue = new Queue<TreeNode>();
        //traverse and add to queue that has all of the nodes in the bst
        profileQueue.enqueue(treeRoot);
        while(!profileQueue.isEmpty()){ //while there is profiles
            TreeNode ptr = profileQueue.dequeue();
            if(ptr == null){
                System.out.println();
            }else{
                if(ptr.getLeft() != null){ //add left
                    profileQueue.enqueue(ptr.getLeft());
                }
                if(ptr.getRight() != null){ //add right
                    profileQueue.enqueue(ptr.getRight());
                }
                if(ptr.getProfile().getMarkedStatus() == isOfInterest){
                    count++;
                }
            }
        }
        return count; // update this line
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {

        // WRITE YOUR CODE HERE
        Queue<TreeNode> profileQueue = new Queue<TreeNode>();
        //traverse and add to queue that has all of the nodes in the bst
        profileQueue.enqueue(treeRoot);
        while(!profileQueue.isEmpty()){ //while there is profiles
            TreeNode ptr = profileQueue.dequeue();
            if(ptr == null){
                if(!profileQueue.isEmpty()){
                    System.out.println();
                }
            }else{
                if(ptr.getLeft() != null){ //add left
                    profileQueue.enqueue(ptr.getLeft());
                }
                if(ptr.getRight() != null){ //add right
                    profileQueue.enqueue(ptr.getRight());
                }
                int count = 0;
                for(STR str : ptr.getProfile().getStrs()){
                    int unknownSequences = numberOfOccurrences(firstUnknownSequence + secondUnknownSequence, str.getStrString());
                    if(str.getOccurrences() == unknownSequences){
                        count++;
                    }
                }
                if (((ptr.getProfile().getStrs().length + 1) / 2) <= count){
                    ptr.getProfile().setInterestStatus(true);
                }
            }
        }
    }


    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        // WRITE YOUR CODE HERE
        Queue<TreeNode> queue = new Queue<>();
        int unmarkedCount = getMatchingProfileCount(false);
        String[] numOfUnmarkedProfiles = new String[unmarkedCount];
        int index = 0;
        queue.enqueue(treeRoot);
        while(!queue.isEmpty()){
            TreeNode ptr = queue.dequeue();
            if((!ptr.getProfile().getMarkedStatus())){ //if there is a profile and it is false status
                numOfUnmarkedProfiles[index] = ptr.getName(); 
                index++;
            }
            if(ptr.getLeft() != null){
                queue.enqueue(ptr.getLeft());
            }
            if(ptr.getRight() != null){
                queue.enqueue(ptr.getRight());
            }
        }
        return numOfUnmarkedProfiles; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        // WRITE YOUR CODE HERE
        TreeNode ptr = treeRoot;
        TreeNode parentNode = null;
        if(treeRoot == null){
            return;
        }
        while(ptr != null && !ptr.getName().equals(fullName)){
            parentNode = ptr;
            if(ptr.getName().compareTo(fullName) < 0){
                ptr = ptr.getRight();
            }else{
                ptr = ptr.getLeft();
            }
        }
        if(ptr == null){
            return;
        }
        if(ptr.getLeft() == null && ptr.getRight() == null){ //no children
            if(parentNode == null){
                treeRoot = null;
            }else if (ptr == parentNode.getLeft()){
                parentNode.setLeft(null);
            }else{
                parentNode.setRight(null);
            }
        }else if (ptr.getLeft() == null || ptr.getRight() == null){ // 1 child
            TreeNode child = null;
            if(ptr.getLeft() != null){
                child = ptr.getLeft();
            }else{
                child = ptr.getRight();
            }
            if(parentNode == null){
                treeRoot = child;
            }else if (ptr == parentNode.getLeft()){
                parentNode.setLeft(child);
            }else{
                parentNode.setRight(child);
            }
        }else{ //2 children
            TreeNode successorParent = ptr;
            TreeNode successor = ptr.getRight();
            while(successor.getLeft() != null){
                successorParent = successor;
                successor = successor.getLeft();
            }
            ptr.setName(successor.getName());
            ptr.setProfile(successor.getProfile());
            if(successorParent != ptr){
                successorParent.setLeft(successor.getRight());
            }else{
                successorParent.setRight(successor.getRight());
            }
        }
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        //WRITE YOUR CODE HERE
        String[] unmarkedPeople = getUnmarkedPeople();
        for(int i = 0; i < unmarkedPeople.length; i++){
            removePerson(unmarkedPeople[i]);
        }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
