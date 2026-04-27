package termproject;

/**
 * Title:        Term Project 2-4 Trees
 * Description:  A balanced search tree implementation that maintains O(log n) performance
 *               for insertions, deletions, and searches. Supports duplicate keys.
 * 
 * A 2-4 tree is a multiway tree where:
 * - Each internal node has 2, 3, or 4 children
 * - Each node stores 1, 2, or 3 items in sorted order
 * - All leaves are at the same depth (perfectly balanced)
 * - Items are stored only in leaves; internal nodes store duplicates of keys for navigation
 * 
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author    David Tarwater and Jadon Spears
 * @version 1.0
 */
public class TwoFourTree
        implements Dictionary {

    // Comparator used for comparing elements during tree operations
    private Comparator treeComp;
    
    // Total number of items stored in the tree (including duplicates)
    private int size = 0;
    
    // Reference to the root node of the tree
    private TFNode treeRoot = null;

    /**
     * Creates a new 2-4 tree with the specified comparator
     * @param comp the comparator to use for ordering elements
     */
    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    /**
     * Gets the root node of the tree
     * @return the root TFNode, or null if tree is empty
     */
    private TFNode root() {
        return treeRoot;
    }

    /**
     * Sets the root node of the tree
     * @param root the new root node
     */
    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    /**
     * Returns the number of items currently in the tree
     * @return the size of the tree
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the tree is empty
     * @return true if tree contains no items, false otherwise
     */
    public boolean isEmpty() {
        return (size == 0);
    }

/**
 * Checks if a node is internal (has at least one child)
 * @param node the node to check
 * @return true if node has children, false if it's a leaf
 */
private Boolean isInternal(TFNode node) {
    for (int i = 0; i < 5; i++) {
        if (node.getChild(i) != null) {
            return true;
        }
    }
    return false;
}

/**
 * Find First Greater Than or Equal: returns the index of the first item
 * in the node that is greater than or equal to the given key.
 * Used for binary search-like navigation through the tree.
 * @param node the current node to search
 * @param key the key to compare against
 * @return index of first item >= key, or numItems if no such item exists
 */
private int FFGTE(TFNode node, Object key) {
    for (int i = 0; i < node.getNumItems(); i++) {
        if (treeComp.isGreaterThanOrEqualTo(node.getItem(i).key(), key)) {
            return i;
        }
    }
    return node.getNumItems();
}

/**
 * What Child Is This (WCIT): determines which child position this node
 * occupies in its parent node. Used during tree restructuring operations.
 * @param node the node to locate in its parent
 * @return the child index (0 to parent.numItems), or -1 if node is root
 */
private int WCIT(TFNode node) {
    TFNode parent = node.getParent();
    if (parent == null) {
        return -1;
    }
    // A node with n items has n+1 children, so check all positions
    for (int i = 0; i <= parent.getNumItems(); i++) {
        if (parent.getChild(i) == node) {
            return i;
        }
    }
    return -1;
}



    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    public Object findElement(Object key) {
        // Handle empty tree case
        if (treeRoot == null) {
            return null;
        }
        
        // Start at root and find first item >= key
        int index = FFGTE(treeRoot, key);
        TFNode current = treeRoot;

        // Traverse down the tree following appropriate child pointers
        while(index == current.getNumItems() || !treeComp.isEqual(current.getItem(index).element(), key)) {
            TFNode child = current.getChild(index);
            // If we reach a leaf without finding exact match, key not in tree
            if (child == null) {
                break;
            } else {
                // Continue searching in the appropriate child
                current = child;
                index = FFGTE(current, key);
            }
        }

        // Return the item if we found an exact match
        if (index < current.getNumItems() && treeComp.isEqual(current.getItem(index).element(), key)){
            return current.getItem(index);
        }
        return null;
    }

    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    public void insertElement(Object key, Object element) {
        TFNode current = treeRoot;

        // Handle empty tree - create root with single item
        if (treeRoot == null) {
            treeRoot = new TFNode();
            treeRoot.addItem(0, new Item(key, element));
            size++;
            return;
        }

        // Walk down the tree to find the appropriate leaf node
        int index = FFGTE(current, key);
        while (current.getChild(index) != null) {
            current = current.getChild(index);
            index = FFGTE(current, key);
        }

        // Insert into the leaf at the correct sorted position
        current.insertItem(index, new Item(key, element));
        size++;

        // Cascade up: split any node that has too many items (> 3)
        while (current.getNumItems() > 3) {
            TFNode parent = current.getParent();
            splitNode(current);
            current = parent;
            if (current == null) break; // Stop if we reach the new root
        }
    }

/**
 * Splits an overflowed node (4 items) into two smaller nodes.
 * The middle item is promoted to the parent node, and the node's
 * items are redistributed: left keeps item 0-1, right gets item 3,
 * and item 2 is promoted up.
 * @param node the overflowed node to split (must have 4 items)
 */
private void splitNode(TFNode node) {
    TFNode parent = node.getParent();

    // Extract the middle item (index 2) to promote up
    Item middleItem = (Item) node.getItem(2);

    // Create new right node and move the rightmost item there
    TFNode rightNode = new TFNode();
    rightNode.addItem(0, node.getItem(3));

    // Move the rightmost children to the new right node
    rightNode.setChild(0, node.getChild(3));
    rightNode.setChild(1, node.getChild(4));

    // Update parent pointers for the children that moved
    if (rightNode.getChild(0) != null) {
        rightNode.getChild(0).setParent(rightNode);
        rightNode.getChild(1).setParent(rightNode);
    }
    
    // Clean up the original node: remove items 2 and 3
    node.deleteItem(3);
    node.deleteItem(2);
    node.setChild(3, null);
    node.setChild(4, null);

    // Case 1: Node is the root - create new root with the middle item
    if (parent == null) {
        TFNode newRoot = new TFNode();
        newRoot.addItem(0, middleItem);
        newRoot.setChild(0, node);
        newRoot.setChild(1, rightNode);
        node.setParent(newRoot);
        rightNode.setParent(newRoot);
        setRoot(newRoot);
    } 
    // Case 2: Node has a parent - promote middle item to parent
    else {
        int parentIndex = WCIT(node);
        parent.insertItem(parentIndex, middleItem);
        parent.setChild(parentIndex, node);
        parent.setChild(parentIndex + 1, rightNode);
        node.setParent(parent);
        rightNode.setParent(parent);
    }
}

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */

    private void fixUnderflow(TFNode current) { 
        if (current.getNumItems() == 0) {
            TFNode parent = current.getParent();
            if (parent != null) {
                int index = this.WCIT(current);

                if (index > 0 && parent.getChild(index - 1) != null && parent.getChild(index - 1).getNumItems() > 1) {
                    //leftTransfer
                    TFNode leftSibling = parent.getChild(index - 1);
                    current.setChild(1, current.getChild(0));
                    current.setChild(0, leftSibling.getChild(leftSibling.getNumItems() + 1));
                    if (leftSibling.getChild(leftSibling.getNumItems() + 1) != null) {
                        leftSibling.getChild(leftSibling.getNumItems() + 1).setParent(current);
                    }
                    leftSibling.setChild(leftSibling.getNumItems() + 1, null);
                    current.insertItem(0, leftSibling.removeItem(leftSibling.getNumItems()));
                } 
                else if (index < 3 && parent.getChild(index + 1) != null && parent.getChild(index + 1).getNumItems() > 1) {
                    //righttransfer
                    TFNode rightSibling = parent.getChild(index + 1);
                    current.setChild(1, rightSibling.getChild(0));
                    if (rightSibling.getChild(0) != null) {
                        rightSibling.getChild(0).setParent(current);
                    }
                    rightSibling.setChild(0, null);
                    current.insertItem(0, rightSibling.removeItem(0));
                } 
                else if (index > 0 && parent.getChild(index - 1) != null) {
                    //leftfusion
                    TFNode temp = parent.getChild(index - 1);
                    temp.insertItem(temp.getNumItems(), parent.removeItem(index - 1));
                    parent.setChild(0, temp);
                    this.fixUnderflow(parent);
                } 
                else {
                    //rightfusion
                    TFNode temp = parent.getChild(index + 1);
                    if (temp != null) {
                        temp.insertItem(0, parent.removeItem(index));
                    }
                    this.fixUnderflow(parent);
                }

            }
        }
    }

    public Object removeElement(Object key) throws ElementNotFoundException {
        // This would involve: finding the element, removing it from leaf,
        // and rebalancing the tree if nodes become too small (< 1 item)
        if(size == 0){
            throw new ElementNotFoundException("Error: The element was not found in the tree");
        }
        Item element = null;

        TFNode current = treeRoot;
        int index = FFGTE(current, key);
        while (current.getChild(index) != null) {
            current = current.getChild(index);
            index = FFGTE(current, key);
            if (current.getItem(index) != null && treeComp.isEqual(current.getItem(index).key(), key)) {
                break;
            }
        }
        //if (current.getItem(index).key() == key) {
            element = current.getItem(index);
        //}
        if (treeComp.isEqual(key, 2)) {
            System.out.println();
        }

        if (this.isInternal(current)) {
            TFNode inorderSuccessor = current;
            Item inorderSuccessorItem;

            if (current.getChild(index + 1) != null) {
                inorderSuccessor = current.getChild(index + 1);
                while (inorderSuccessor.getChild(0) != null) {
                    inorderSuccessor = inorderSuccessor.getChild(0);
                }
                inorderSuccessorItem = inorderSuccessor.getItem(0);
                current.replaceItem(index, inorderSuccessorItem);
                inorderSuccessor.removeItem(0);
                current = inorderSuccessor;
            } else {
                current.removeItem(index);
            }
        }
        else {
            current.removeItem(index);
        }
        size--;
        
        //underflow
        fixUnderflow(current);
        
        if (element == null) {
            System.out.println();
        }

        return element;
    }

    /**
     * Main method for testing the 2-4 tree implementation
     * Tests insertion with various values and removal operations
     */
    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        /*
        myTree.insertElement(myInt1, myInt1);
        myTree.printAllElements();
        myTree.checkTree();
        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        myTree.printAllElements();
        myTree.checkTree();
        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);
        myTree.printAllElements();
        myTree.checkTree();

        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);
   
        myTree.checkTree();

        myTree.printAllElements();
        System.out.println("done");*/

        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
            //myTree.printAllElements();
            myTree.checkTree();
        }
        //myTree.printAllElements();
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) ((Item) myTree.removeElement(new Integer(i))).element();
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                //myTree.printAllElements();
            }
            myTree.checkTree();
        }
        System.out.println("done");
    }

/**
 * Prints all elements in the tree in a hierarchical format
 */
    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    /**
     * Recursively prints the tree structure with indentation
     * @param start the node to start printing from
     * @param indent the current indentation level
     */
    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        // Add indentation for visual hierarchy
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        // Print current node's items
        printTFNode(start);
        
        // Recursively print all children with increased indentation
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    /**
     * Prints all items in a single node on one line
     * @param node the node to print
     */
    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    /**
     * Validates tree integrity: checks that all parent-child relationships
     * are properly maintained and no structural inconsistencies exist
     */
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    /**
     * Recursively validates tree structure from a given node
     * Checks for: parent-child consistency, null/non-null child consistency,
     * and duplicate children
     * @param start the node to start validation from
     */
    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        // Check 1: Verify parent-child relationship is bidirectional
        // If this node has a parent, verify that this node is actually a child of that parent
        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            // Search all child positions (0 through numItems, since n items = n+1 children)
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // If child wasn't found in parent's children, structural error
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        // Check 2: Validate children consistency
        // All children must be either all null (leaf node) or all non-null (internal node)
        // No mixed null and non-null children allowed
        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                // Check for mixed null and non-null children
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    // Verify reverse pointer: child's parent must be this node
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    // Check for duplicate children (same child in multiple positions)
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        // Recursively check all children
        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }
}
