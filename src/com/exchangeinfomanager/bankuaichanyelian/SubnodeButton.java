package com.exchangeinfomanager.bankuaichanyelian;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI2;

public class SubnodeButton extends javax.swing.JButton{
    
    private int direction = BanKuaiAndChanYeLianGUI2.NONE;
        
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    public int getDirection(){
        return direction;
    }
    
}

