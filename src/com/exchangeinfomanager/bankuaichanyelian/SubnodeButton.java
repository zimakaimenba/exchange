package com.exchangeinfomanager.bankuaichanyelian;

import net.ginkgo.dom4jcopy.Ginkgo2;

public class SubnodeButton extends javax.swing.JButton{
    
    private int direction = Ginkgo2.NONE;
        
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    public int getDirection(){
        return direction;
    }
    
}

