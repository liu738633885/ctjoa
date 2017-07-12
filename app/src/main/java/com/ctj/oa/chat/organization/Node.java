package com.ctj.oa.chat.organization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/5/5.
 */

public class Node {
    public int iconExpand = -1, iconNoExpand = -1;
    private int dept_id;
    private int dept_parent_id;
    private String dept_name;

    private String nickname;
    private int type;
    private String signature;
    private String portrait;
    private boolean isClick;




    /*是否展开*/
    private boolean isExpand = false;

    private int icon = -1;
    /**
     * 当前的级别
     */
    private int level;
    /**
     * 下一级的子Node
     */
    private List<Node> children = new ArrayList<>();
    /**
     * 父Node
     */
    private Node parent;
    /**
     * 是否为新添加的
     */
    public boolean isNewAdd = true;

    public Node() {
    }

    public Node(int dept_id, int dept_parent_id, String nickname,int type) {
        this.dept_id = dept_id;
        this.dept_parent_id = dept_parent_id;
        this.nickname = nickname;
        this.type = type;
    }


/*=====基础逻辑======*/

    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf() {
        if(getType()==2){
            return true;
        }else {
            return children.size() == 0;
        }
    }

    /**
     * 获取level
     */
    public int getLevel() {

        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (Node node : children) {
                node.setExpand(isExpand);
            }
        }
    }




    /*======getset======*/

    public int getDept_id() {
        return dept_id;
    }

    public void setDept_id(int dept_id) {
        this.dept_id = dept_id;
    }

    public int getDept_parent_id() {
        return dept_parent_id;
    }

    public void setDept_parent_id(int dept_parent_id) {
        this.dept_parent_id = dept_parent_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }


    public boolean isExpand() {
        return isExpand;
    }


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
