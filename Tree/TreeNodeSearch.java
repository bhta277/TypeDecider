package Tree;

public class TreeNodeSearch {
    public static TreeNode search(TreeNode root, String str) {
        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(String treeData) {
                if (treeData == null)
                    return 1;
                boolean nodeOk = treeData.equalsIgnoreCase(str);
                return nodeOk ? 0 : 1;
            }
        };

        TreeNode<String> found = root.findTreeNode(searchCriteria);

        return found;
    }
}
