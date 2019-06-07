import Model.Node;
import Tree.TreeNode;
import Tree.TreeNodeSearch;
import Util.FileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TypeDecider {

    public int findTreeDepth(String input) {
        File file = new File(input);
        int maxLevel = 0, count = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {
                count = FileUtil.symbolCount(st);
                if (  count > maxLevel ) maxLevel = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxLevel;
    }

    public String findParent(String child) {
        ArrayList<Node> nodeList = FileUtil.fetchData( FileUtil.ONTOLOGY_FILE);
        Node childNode = null;
        for ( Node n: nodeList) {
            if ( n.getName().equalsIgnoreCase(child)) {
                childNode = n;
                break;
            }
        }
        int childLevel = childNode.getLevel();
        for ( int i = nodeList.indexOf(childNode); i >= 0 ; i--) {
            if ( nodeList.get(i).getLevel() == childLevel - 1) {
                return nodeList.get(i).getName();
            }
        }
        return null;
    }

    public void addAllNodesByLevel( ArrayList<Node> nodeList, TreeNode root, int level) {
        if ( level == 0) {
            for ( Node n: nodeList) {
                if ( n.getLevel() == 0) {
                    root.addChild( n.getName());
                }
            }
        } else  if ( level > 0) {
            for ( Node n: nodeList) {
                if ( n.getLevel() == level) {
                    String childName = n.getName();
                    String parentName = findParent( childName);
                    TreeNode parentNode = TreeNodeSearch.search( root, parentName);
                    TreeNode childNode = parentNode.addChild(childName);
                }
            }
        }
    }

    public boolean isDescendant( String descendant, String ancestor, TreeNode root) {
        TreeNode descNode = TreeNodeSearch.search(root, descendant);
        while ( descNode.parent.getLevel() > 0) {
            descNode = descNode.parent;
            if ( descNode.toString().equalsIgnoreCase(ancestor))
                return true;
        }
        return false;
    }

    public boolean isRelative( String string1, String string2, TreeNode root) {
        TreeNode node1 = TreeNodeSearch.search(root, string1);
        while ( node1.parent.getLevel() > 0) {
            node1 = node1.parent;
            if ( isDescendant( string2, node1.toString(), root))
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception{


        TreeNode root = new TreeNode<String>("root");

//            TreeNode<String> node0 = root.addChild("node0");
//            TreeNode<String> node1 = root.addChild("node1");
//
//            TreeNode<String> node2 = root.addChild("node2");
//
//                TreeNode<String> node20 = node2.addChild(null);
//                TreeNode<String> node21 = node2.addChild("node21");
//
//                    TreeNode<String> node210 = node21.addChild("node210");
//                    TreeNode<String> node211 = node21.addChild("node211");
//
//
//            TreeNode<String> node3 = root.addChild("node3");
//
//                TreeNode<String> node30 = node3.addChild("node30");
//
//        System.out.println(TreeNodeSearch.search(root,"node30").toString());
//        System.out.println(new TypeDecider().findParent("CapitalOfRegion"));
        ArrayList<Node> nodeList = FileUtil.fetchData( FileUtil.ONTOLOGY_FILE);
        TypeDecider td = new TypeDecider();
        int depth = new TypeDecider().findTreeDepth(FileUtil.ONTOLOGY_FILE);

        for (int i = 0; i <= depth; i++) {
            td.addAllNodesByLevel(nodeList, root, i);
        }

//        System.out.println(td.isDescendant("HistoricalProvince", "PopulatedPlace", root));
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File("TypeDecider.txt"),true));

        FileInputStream fis = new FileInputStream(new File("sdtypeframeworknotnull.xlsx"));

        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            String fw = row.getCell(1).toString().replace(" ","");
            String tpre = row.getCell(2).toString();
            System.out.println(fw+"\n"+tpre+"\n");
            if (tpre.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Thing")) {
                pw.append(fw+"\n");
            } else {
                if ( TreeNodeSearch.search(root, fw) != null && TreeNodeSearch.search(root, tpre) != null) {
                    if (fw.equalsIgnoreCase(tpre)) {
                        pw.append(fw+"\n");
                    } else if (td.isDescendant(tpre, fw, root)) {
                        pw.append(tpre+"\n");
                    } else if (td.isDescendant(fw, tpre, root)) {
                        pw.append(fw+"\n");
                    } else {
                        if ( td.isRelative( tpre, fw, root)) {
                            TreeNode tpreNode = TreeNodeSearch.search( root, tpre);
                            TreeNode fwNode = TreeNodeSearch.search( root, fw);
                            if (tpreNode.getLevel() > fwNode.getLevel())
                                pw.append(tpre + "\n");
                            else if (tpreNode.getLevel() == fwNode.getLevel())
                                pw.append(tpre+"/"+fw + "\n");
                            else
                                pw.append(fw + "\n");
                        }
                        else
                            pw.append("null"+"\n");
                    }
                } else {
                    pw.append("null"+"\n");
                }
            }
        }

        pw.close();
//        System.out.println(TreeNodeSearch.search(root, "Colour")!=null && TreeNodeSearch.search(root, "Colour")!=null);
    }

}

